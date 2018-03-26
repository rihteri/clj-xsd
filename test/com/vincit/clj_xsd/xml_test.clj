(ns com.vincit.clj-xsd.xml-test
  (:require [com.vincit.clj-xsd.xml :as hx]
            [clojure.test :as t]
            [com.vincit.clj-xsd.metaschema.nss :as xs-nss]))

(t/deftest tag-extraction
  (t/is (= [xs-nss/sns "schema"]
           (hx/extract-tag nil nil :xmlns.http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema/schema))))
