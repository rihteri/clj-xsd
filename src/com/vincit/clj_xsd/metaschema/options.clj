(ns com.vincit.clj-xsd.metaschema.options
  (:require [com.vincit.clj-xsd.parser.custom.simple-types :as parsers]
            [com.vincit.clj-xsd.metaschema.post.schema :as sp]
            [com.vincit.clj-xsd.metaschema.nss :as xs-nss]
            [com.vincit.clj-xsd.metaschema.post.complex-type :as sp-complex]
            [com.vincit.clj-xsd.metaschema.post.element :as sp-element]
            [com.vincit.clj-xsd.metaschema.post.choice :as sp-choice]
            [com.vincit.clj-xsd.metaschema.post.sequence :as sp-sequence]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.custom.order-preserving :as order-parser]))

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

(def sequence-parser
  (comp
   (partial filter (complement string?))
   (order-parser/make-order-preserving-parser [xs-nss/sns "sequence"])))

(def parse-opts
  "Default parsing options for schema documents"
  {:com.vincit.clj-xsd.core/namespaces
   {xs-nss/sns 'com.vincit.clj-xsd.metaschema}
   :com.vincit.clj-xsd.core/parsers
   {:com.vincit.clj-xsd.core/simple
    {[xs-nss/sns "allNNI"]     parse-all-nni
     [xs-nss/sns "formChoice"] parse-form-choice
     [xs-nss/sns "useType"]    parse-use-attr}
    :com.vincit.clj-xsd.core/complex
    {[xs-nss/sns "sequence"] sequence-parser}}
   :com.vincit.clj-xsd.core/post
   {[xs-nss/sns "schema"]      sp/schema-to-internal
    [xs-nss/sns "complexType"] sp-complex/fix
    [xs-nss/sns "sequence"]    sp-sequence/fix
    [xs-nss/sns "choice"]      sp-choice/fix
    [xs-nss/sns "element"]     sp-element/fix}})
