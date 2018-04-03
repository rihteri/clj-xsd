(ns com.vincit.clj-xsd.parser.simple-test
  (:require [com.vincit.clj-xsd.parser.simple :as sut]
            [clojure.test :as t]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.core :as cxs]))

(def tt ["ex" "lst"])
(def t2 ["ex" "lst-ext"])

(def type-def
  {::hs/list-of ["x" "str"]})

(def t2-def
  {::hs/base tt})

(def context
  (-> {::cxs/parsers {::cxs/simple {["x" "str"] (fn [c val] val)}}}
      (assoc-in [::hs/schema ::hs/types tt] type-def)
      (assoc-in [::hs/schema ::hs/types t2] t2-def)))

(t/deftest parse-simple-list
  (t/is (= ["aa" "bee"]
           (sut/parse-simple context type-def "aa bee"))))

(t/deftest parse-simple-list-ext
  (t/is (nil? (sut/get-parser context t2-def)))
  (t/is (= type-def (sut/get-type-def context tt)))
  (t/is (= ["aa" "bee"]
           (sut/parse-simple context t2-def "aa bee")))
  (t/is (= ["aa" "bee"]
           (sut/parse-simple context type-def "aa bee"))))
