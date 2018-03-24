# clj-xsd

A library for reading XML documents into nice clojure data structures
with the help of xsd.

This is just a small demo - NOT SUITABLE FOR PRODUCTION. The whole
thing may be based on a misunderstanding of XML schemas and be 
completely unworkable.

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
      <xs:element name="a" type="subType" minOccurs="0" maxOccurs="1" />
      <xs:element name="b" type="xs:string" minOccurs="1" maxOccurs="1" />
      <xs:element name="c" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
    </xs:sequence>
    <xs:attribute name="soma" type="xs:string" form="qualified" />
    <xs:attribute name="numa" type="xs:integer" />
  </xs:complexType>

  <xs:complexType name="subType">
    <xs:sequence>
      <xs:element name="ugh" minOccurs="1" maxOccurs="1" type="xs:string" />
      <xs:element name="argh" minOccurs="1" maxOccurs="1" type="xs:integer" />
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```

And a document like this:

```
<?xml version="1.0" encoding="utf-8"?>
<ex:top xmlns:ex="http://example.org/test-schema-1"
        ex:soma="jau" numa="42">
  <ex:a>
    <ex:ugh>jabada</ex:ugh>
    <ex:argh>1</ex:argh>
  </ex:a>
  <ex:b>
    asdf
  </ex:b>
  <ex:c>123</ex:c>
  <ex:c>!!!</ex:c>
</ex:top>
```

You can get a nice clojure representation of the document like this:

```
(require '[com.vincit.clj-xsd.core :as cxs])
(require '[clojure.java.io :as io])

(def schema 
  (with-open [schema (io/input-stream "test_resources/schema1.xsd")]
    (cxs/read-schema schema)))
    
(with-open [file (io/input-stream "test_resources/doc1.xml")]
  (cxs/parse schema file))
  
 => {:top {:soma "jau"
           :numa 42
           :a {:ugh "jabada"
               :argh 1}
           :b "asdf"
           :c ("123" "!!!")}}
```

## TODO
Many things, but most useful might be
* extension improvements (choice -> sequence, choice -> choice etc)
* xsi:type
* anonymous types
* element refs
* attribute refs
* groups

## License

Copyright © 2018 Vincit

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
