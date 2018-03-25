# clj-xsd

A library for reading XML documents into nice Clojure data structures
with the help of XSD.

This library is not used anywhere and definitely not in anything resembling
production, so I recommend you don't do that either.

## Rationale
XML is a somewhat human readable serialization format. Clojure has
libraries like clojure.data.xml (used here too) that can read and write
XML.

However, an XML file alone is not enough to describe the full meaning of
the file. Say we have an element like
```
<answer>42</answer>
```

Is that 42 a number, or maybe a string? Is 'answer' a single value, or a list
of one?

This information can be guessed, hardcoded or read from an out-of-band
(meaning separate) schema. One standard for representing the
schemas is [XML Schema](https://www.w3.org/TR/xmlschema-0/).

An XML schema might have a line such as
```
<element name="answer" type="xs:int" minOccurs="0" maxOccurs="1" />
```
... which tells that the previous snippet was a single value with
the type integer. And that you might have to get along without having
an answer.

The aim of this library is to combine the schema and the data files
to produce Clojure data structures that are as simple as possible
and require as little additional processing as possible to extract
whatever information needs to be extracted.

The key things are
* Numbers should be numbers, dates should be dates etc
* Repeating elements should be lists - even if there is only
  one of them in a given file
* XML element and attribute names should become namespaced
  Clojure keywords
* Simple representation - even if it means some information
  is lost on extremely complex schemas
* Allow pluggable deserializers so that problems caused by the former
  point can be worked around

Obvious prior art can be found in the XML serialization libraries
of your favourite object oriented enterprise languages.

## Usage

Let's say you have a schema like this:

```
<?xml version="1.0" encoding="utf-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:ex="http://example.org/test-schema-1"
           xmlns="http://example.org/test-schema-1"
           targetNamespace="http://example.org/test-schema-1"
           elementFormDefault="qualified">
  <xs:element name="top" type="ex:topType" />

  <xs:complexType name="topType">
    <xs:sequence>
      <xs:element name="optional-element" type="subType" minOccurs="0" maxOccurs="1" />
      <xs:element name="mandatory-element" type="xs:string" minOccurs="1" maxOccurs="1" />
      <xs:element name="repeating-element" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
    </xs:sequence>
    <xs:attribute name="some-attribute" type="xs:string" form="qualified" />
    <xs:attribute name="numeric-attribute" type="xs:integer" />
  </xs:complexType>

  <xs:complexType name="subType">
    <xs:sequence>
      <xs:element name="some-string" minOccurs="1" maxOccurs="1" type="xs:string" />
      <xs:element name="some-number" minOccurs="1" maxOccurs="1" type="xs:integer" />
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```

And a document like this:

```
<?xml version="1.0" encoding="utf-8"?>
<ex:top xmlns:ex="http://example.org/test-schema-1"
        ex:some-attribute="jau" numeric-attribute="987">
  <ex:optional-element>
    <ex:some-string>jabada</ex:some-string>
    <ex:some-number>1</ex:some-number>
  </ex:optional-element>
  <ex:mandatory-element>
    asdf
  </ex:mandatory-element>
  <ex:repeating-element>yippee</ex:repeating-element>
  <ex:repeating-element>!!!</ex:repeating-element>
</ex:top>
```

You can get a nice clojure representation of the document like this:

```
(require '[com.vincit.clj-xsd.core :as cxs])
(require '[clojure.java.io :as io])

; read schema out-of-band
(def schema 
  (with-open [schema (io/input-stream "test_resources/schema1.xsd")]
    (cxs/read-schema schema)))
    
; read a single data file
(with-open [file (io/input-stream "test_resources/doc1.xml")]
  (cxs/parse schema file))
  
 => {:top {:some-attribute    "jau"
           :numeric-attribute 987           ; it's a number!
           :optional-element  {:some-string "jabada"
                               :some-number 1}
           :mandatry-element  "asdf"
           :repeating-element ("yippee" "!!!")}}    ; it's a list!

       ; ... and the keys would have been namespaced if we provided a
       ;     namespace mapping from XML namespaces (strings) to clojure
       ;     namespace symbols on our call to cxs/parse
```

## Structure
For parsing the XML files (schemas and data files alike), [clojure.data.xml](https://github.com/clojure/data.xml)
is used.

The schema is read into an internal representation that
is somewhat simplified from the original. In theory you may write that
yourself if you don't have an XML Schema available but would like to
guess the structure of your document. Or if the schema is too hard for this
little library to understand. The internal format is not stable.

## Things that may work
* producing clojure data structures with namespaced keys
* some simple types - like xs:integer - will have the correct type in the deserialization result
* multiplicity of elements causes clojure lists
* sequence and choice type complex types
* xsi:type schema overrides
* complex type extensions
* anonymous types
* pluggable type handling, at least for simple types

## TODO
* add tests and fix complicated nested choice/sequence combos (some of these might prove impossible to support)
* make sure support for custom complex type parser functions works, allowing the lib to be useful even below 100% functionality
* more simple type deserializers and choosing deserializer for a general simple type if one is not available for an extended type
* anonymous simple types
* attribute and element refs
* attribute and element groups
* multiple schema files
* simplify internal schema representation (hopefully 'relaxation' and 'accretion' more than 'breakage')
* extension improvements (choice -> sequence, choice -> choice etc)
* produce metaschema by reading the actual W3C XML Schema XSD
* pluggable keyword generator
* CLJS support
* error reporting - although the aim is not to be a schema validator
* mixed content complex types
* ... and of course serialization

## License

Copyright © 2018 Vincit

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
