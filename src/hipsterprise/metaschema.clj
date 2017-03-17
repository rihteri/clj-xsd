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

(def ncname
  (xs "NCName"))

(def uri
  (xs "anyURI"))

(def schemaschema
  "The schema of an XML Schema document"
  {::hs/elems {(xs "schema") {::hs/attrs {(xs "targetNamespace") {::hs/type uri}}
                              ::hs/elems [{(xs "schemaTop") {::hs/multi [0 :n]
                                                             ::hs/type  (xs "element")}}]}}
   ::hs/types {(xs "element") {::hs/attrs {(xs "name") {::hs/type ncname}
                                           (xs "type") {::hs/type ncname}}}}})

(def parse-opts
"Default parsing options for schema documents"
  {})
