(ns hipsterprise.metaschema
  (:require [hipsterprise.schema :as hs]
            [hipsterprise.xml :as hx]))

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

(def uri
  (xs "anyURI"))

(def schemaschema
  "The schema of an XML Schema document"
  {::hs/elems {(xs "schema") {::hs/type (xs "schema")}}
   ::hs/types {(xs "schema")      {::hs/attrs   {(xs "targetNamespace") {::hs/type uri}}
                                   ::hs/content [::hs/choice
                                                 {::hs/multi [0 :n]
                                                  ::hs/elems {(xs "element")
                                                              {::hs/type (xs "element")}
                                                              (xs "complexType")
                                                              {::hs/type (xs "complexType")}}}]}
               (xs "element")     {::hs/attrs {(xs "name")      {::hs/type ncname}
                                               (xs "type")      {::hs/type ncname}
                                               (xs "minOccurs") {::hs/type integer}
                                               (xs "maxOccurs") {::hs/type string}}}
               (xs "complexType") {::hs/attrs   {(xs "name") {::hs/type string}}
                                   ::hs/content [::hs/sequence
                                                 {::hs/vals [{::hs/element (xs "sequence")
                                                              ::hs/type    (xs "sequence")
                                                              ::hs/multi   [0 :n]}]}]}
               (xs "sequence")    {::hs/content [::hs/choice
                                                 {::hs/multi [0 :n]
                                                  ::hs/elems {(xs "element")
                                                              {::hs/type (xs "element")}}}]}}})

(def parse-opts
"Default parsing options for schema documents"
  {:hipsterprise.core/namespaces {sns 'hipsterprise.metaschema}})
