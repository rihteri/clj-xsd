(ns hipsterprise.xml-test
  (:require [hipsterprise.xml :as hx]
            [clojure.test :as t]
            [hipsterprise.metaschema :as xs]))

(t/deftest tag-extraction
  (t/is (= [xs/sns "schema"]
           (hx/extract-tag :xmlns.http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema/schema))))
