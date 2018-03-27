(ns com.vincit.clj-xsd.metaschema
  "
  The internal description of the XML Schema file
  "
  (:require [com.vincit.clj-xsd.schema :as hs]
            [clojure.spec.alpha :as s]
            [com.vincit.clj-xsd.metaschema.nss :as xs-nss]))

(s/fdef xs
        :args (s/cat :name ::hs/name)
        :ret ::hs/qname)

(defn xs
"Make an NCName in the XMLSchema namespace"
  [name]
  [xs-nss/sns name])

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
                                                                 {::hs/type (xs "complexType")}
                                                                 (xs "simpleType")
                                                                 {::hs/type (xs "simpleType")}}}]}
               (xs "element")        {::hs/attrs   {(xs "name")      {::hs/type ncname}
                                                    (xs "type")      {::hs/type qname}
                                                    (xs "minOccurs") {::hs/type integer}
                                                    (xs "maxOccurs") {::hs/type (xs "allNNI")}}
                                      ::hs/content [::hs/sequence {::hs/vals [{::hs/element (xs "complexType")
                                                                               ::hs/type    (xs "complexType")
                                                                               ::hs/multi   [0 1]}
                                                                              {::hs/element (xs "attribute")
                                                                               ::hs/type    (xs "attribute")
                                                                               ::hs/multi   [0 :n]}]}]}
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
               (xs "sequence")       {::hs/attrs   {(xs "minOccurs") {::hs/type integer}
                                                    (xs "maxOccurs") {::hs/type (xs "allNNI")}}
                                      ::hs/content [::hs/choice
                                                    {::hs/multi [0 :n]
                                                     ::hs/elems {(xs "element")
                                                                 {::hs/type (xs "element")}
                                                                 (xs "choice")
                                                                 {::hs/type (xs "choice")}
                                                                 (xs "sequence")
                                                                 {::hs/type (xs "sequence")}}}]}
               (xs "choice")         {::hs/attrs   {(xs "minOccurs") {::hs/type integer}
                                                    (xs "maxOccurs") {::hs/type (xs "allNNI")}}
                                      ::hs/content [::hs/choice
                                                    {::hs/multi [0 :n]
                                                     ::hs/elems {(xs "element")
                                                                 {::hs/type (xs "element")}}}]}
               (xs "attribute")      {::hs/attrs {(xs "name")    {::hs/type string}
                                                  (xs "type")    {::hs/type qname}
                                                  (xs "form")    {::hs/type (xs "formChoice")}
                                                  (xs "default") {::hs/type string}
                                                  (xs "use")     {::hs/type (xs "useType")}}}
               (xs "simpleType")     {::hs/attrs   {(xs "id")   {::hs/type (xs "ID")}
                                                    (xs "name") {::hs/type (xs "NCName")}}
                                      ::hs/content [::hs/choice
                                                    {::hs/multi [0 1]
                                                     ::hs/elems {(xs "restriction")
                                                                 {::hs/type (xs "restriction")}
                                                                 (xs "list")
                                                                 {::hs/type (xs "list")}
                                                                 (xs "union")
                                                                 {::hs/type (xs "union")}}}]}
               (xs "list")           {::hs/attrs {(xs "itemType") {::hs/type (xs "QName")}}}}})
