(ns hipsterprise.metaschema
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.parser.default-parsers :as parsers]
            [hipsterprise.schema :as hs]))

(def sns
  "XML Schema namespace"
  "http://www.w3.org/2001/XMLSchema")

(defn xs
"Make an NCName in the XMLSchema namespace"
  [name]
  {::hx/name name
   ::hx/ns   sns})

(def string
  (xs "string"))

(def integer
  (xs "integer"))

(def ncname
  (xs "NCName"))

(def qname
  (xs "QName"))

(def uri
  (xs "anyURI"))

(def schemaschema
  "The schema of an XML Schema document"
  {::hs/elems {(xs "schema") {::hs/type (xs "schema")}}
   ::hs/types {(xs "schema")      {::hs/attrs   {(xs "targetNamespace")    {::hs/type uri}
                                                 (xs "elementFormDefault") {::hs/type (xs "formChoice")}}
                                   ::hs/content [::hs/choice
                                                 {::hs/multi [0 :n]
                                                  ::hs/elems {(xs "element")
                                                              {::hs/type (xs "element")}
                                                              (xs "complexType")
                                                              {::hs/type (xs "complexType")}}}]}
               (xs "element")     {::hs/attrs {(xs "name")      {::hs/type ncname}
                                               (xs "type")      {::hs/type qname}
                                               (xs "minOccurs") {::hs/type integer}
                                               (xs "maxOccurs") {::hs/type (xs "allNNI")}}}
               (xs "complexType") {::hs/attrs   {(xs "name") {::hs/type ncname}}
                                   ::hs/content [::hs/sequence
                                                 {::hs/vals [{::hs/element (xs "sequence")
                                                              ::hs/type    (xs "sequence")
                                                              ::hs/multi   [0 :n]}
                                                             {::hs/element (xs "attribute")
                                                              ::hs/type    (xs "attribute")
                                                              ::hs/multi   [0 :n]}]}]}
               (xs "sequence")    {::hs/content [::hs/choice
                                                 {::hs/multi [0 :n]
                                                  ::hs/elems {(xs "element")
                                                              {::hs/type (xs "element")}}}]}
               (xs "attribute")   {::hs/attrs {(xs "name") {::hs/type string}
                                               (xs "type") {::hs/type qname}
                                               (xs "form") {::hs/type (xs "formChoice")}}}}})

(defn parse-all-nni [opts value]
  (if (= value "unbounded")
    :n
    (parsers/parse-integer opts value)))

(defn parse-form-choice [opts value]
  (case value
    "qualified" ::qualified
    "unqualified" ::unqualified))

(def parse-opts
"Default parsing options for schema documents"
  {:hipsterprise.core/namespaces {sns 'hipsterprise.metaschema}
   :hipsterprise.core/parsers
   {:hipsterprise.core/simple
    {(xs "allNNI")     parse-all-nni
     (xs "formChoice") parse-form-choice}}})
