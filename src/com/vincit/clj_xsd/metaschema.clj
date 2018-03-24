(ns com.vincit.clj-xsd.metaschema
  "
  The internal description of the XML Schema file
  "
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.default-parsers :as parsers]
            [com.vincit.clj-xsd.schema :as hs]
            [clojure.spec.alpha :as s]))

(def sns
  "XML Schema namespace"
  "http://www.w3.org/2001/XMLSchema")

(def xsi-ns
  "XML Schema Instance namespace"
  "http://www.w3.org/2001/XMLSchema-instance")

(s/fdef xs
        :args (s/cat :name ::hs/name)
        :ret ::hs/qname)

(defn xs
"Make an NCName in the XMLSchema namespace"
  [name]
  [sns name])

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
   ::hs/types {(xs "schema")         {::hs/attrs   {(xs "targetNamespace")      {::hs/type uri}
                                                    (xs "elementFormDefault")   {::hs/type (xs "formChoice")
                                                                                 ::hs/use  ::hs/optional}
                                                    (xs "attributeFormDefault") {::hs/type (xs "formChoice")
                                                                                 ::hs/use  ::hs/optional}}
                                      ::hs/content [::hs/choice
                                                    {::hs/multi [0 :n]
                                                     ::hs/elems {(xs "element")
                                                                 {::hs/type (xs "element")}
                                                                 (xs "complexType")
                                                                 {::hs/type (xs "complexType")}}}]}
               (xs "element")        {::hs/attrs {(xs "name")      {::hs/type ncname}
                                                  (xs "type")      {::hs/type qname}
                                                  (xs "minOccurs") {::hs/type integer}
                                                  (xs "maxOccurs") {::hs/type (xs "allNNI")}}}
               (xs "complexType")    {::hs/attrs   {(xs "name") {::hs/type ncname}}
                                      ::hs/content [::hs/sequence
                                                    {::hs/vals [{::hs/element (xs "sequence")
                                                                 ::hs/type    (xs "sequence")
                                                                 ::hs/multi   [0 1]}
                                                                {::hs/element (xs "complexContent")
                                                                 ::hs/type    (xs "complexContent")
                                                                 ::hs/multi   [0 1]}
                                                                {::hs/element (xs "choice")
                                                                 ::hs/type    (xs "choice")
                                                                 ::hs/multi   [0 1]}
                                                                {::hs/element (xs "attribute")
                                                                 ::hs/type    (xs "attribute")
                                                                 ::hs/multi   [0 :n]}]}]}
               (xs "complexContent") {::hs/content [::hs/sequence
                                                    {::hs/vals [{::hs/element (xs "extension")
                                                                 ::hs/type    (xs "extension")
                                                                 ::hs/multi   [1 1]}]}]}
               (xs "extension")      {::hs/attrs   {(xs "base") {::hs/type qname}}
                                      ::hs/content [::hs/sequence
                                                    {::hs/vals [{::hs/element (xs "sequence")
                                                                 ::hs/type    (xs "sequence")
                                                                 ::hs/multi   [0 1]}
                                                                {::hs/element (xs "choice")
                                                                 ::hs/type    (xs "choice")
                                                                 ::hs/multi   [0 1]}
                                                                {::hs/element (xs "attribute")
                                                                 ::hs/type    (xs "attribute")
                                                                 ::hs/multi   [0 :n]}]}]}
               (xs "sequence")       {::hs/content [::hs/choice
                                                    {::hs/multi [0 :n]
                                                     ::hs/elems {(xs "element")
                                                                 {::hs/type (xs "element")}}}]}
               (xs "choice")         {::hs/content [::hs/choice
                                                    {::hs/multi [0 :n]
                                                     ::hs/elems {(xs "element")
                                                                 {::hs/type (xs "element")}}}]}
               (xs "attribute")      {::hs/attrs {(xs "name")    {::hs/type string}
                                                  (xs "type")    {::hs/type qname}
                                                  (xs "form")    {::hs/type (xs "formChoice")}
                                                  (xs "default") {::hs/type string}
                                                  (xs "use")     {::hs/type ::use-type}}}}})

(defn parse-all-nni [opts value]
  (if (= value "unbounded")
    :n
    (parsers/parse-integer opts value)))

(defn parse-form-choice [opts value]
  (case value
    "qualified" ::hs/qualified
    "unqualified" ::hs/unqualified))

(defn parse-use-attr [opts value]
  (case value
    "optional"   ::hs/optional
    "prohibited" ::hs/prohibited
    "required"   ::hs/required))

(def parse-opts
"Default parsing options for schema documents"
  {:com.vincit.clj-xsd.core/namespaces {sns 'com.vincit.clj-xsd.metaschema}
   :com.vincit.clj-xsd.core/parsers
   {:com.vincit.clj-xsd.core/simple
    {(xs "allNNI")     parse-all-nni
     (xs "formChoice") parse-form-choice
     ::use-type        parse-use-attr}}})
