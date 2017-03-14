(ns hipsterprise.xml-test
  (:require [hipsterprise.xml :as hx]
            [clojure.test :as t]))

(t/deftest tag-extraction
  (t/is (= {::hx/name "schema"
            ::hx/ns  "http://www.w3.org/2001/XMLSchema"}
           (hx/extract-tag :xmlns.http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema/schema))))
