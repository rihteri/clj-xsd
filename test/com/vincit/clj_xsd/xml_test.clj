(ns com.vincit.clj-xsd.xml-test
  (:require [com.vincit.clj-xsd.xml :as hx]
            [clojure.test :as t]
            [com.vincit.clj-xsd.metaschema :as xs]))

(t/deftest tag-extraction
  (t/is (= [xs/sns "schema"]
           (hx/extract-tag nil nil :xmlns.http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema/schema))))
