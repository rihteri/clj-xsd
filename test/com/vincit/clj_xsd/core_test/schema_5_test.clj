(ns com.vincit.clj-xsd.core-test.schema-5-test
  (:require [clojure.test :as t]
            [clojure.java.io :as io]
            [com.vincit.clj-xsd.core :as cxs]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema.nss :as xs]
            [clojure.spec.alpha :as s]
            [clojure.data.xml :as xml]
            [com.rpl.specter :as sc]))

(def sch-file-name "test_resources/schema5.xsd")
(def file-name-1 "test_resources/doc5_1.xml")
(def tns "http://example.org/test-schema-5")

(def expected-schema
  {::hs/tns        tns
   ::hs/el-default ::hs/unqualified
   ::hs/elems      {[tns "top"]
                    {::hs/type [tns "topType"]}}
   ::hs/types      {[tns "topType"]
                    {::hs/content
                     [::hs/sequence
                      {::hs/vals [[::hs/choice
                                   {::hs/multi [0 10]
                                    ::hs/elems {[tns "stringer"]
                                                {::hs/type  [xs/sns "string"]}
                                                [tns "nummer"]
                                                {::hs/type  [xs/sns "integer"]}}}]
                                  {::hs/type    [xs/sns "string"]
                                   ::hs/element [tns "someMore"]}]}]}}})

(defn read-sch []
  (with-open [schema (io/input-stream sch-file-name)]
    (cxs/read-schema schema)))

(t/deftest schema-read
  (let [exp expected-schema
        act (read-sch)]
    (let [[exp act] (map ::hs/el-default [exp act])]
      (t/is (= exp act)))
    (let [[exp act] (map #(sc/select [::hs/types (sc/keypath [tns "topType"]) ::hs/content] %) [exp act])]
      (t/is (= exp act)))
    (t/is (= exp act))))


(def exp-1
  {:a/top {:a/nummer [1 2 3]
           :a/stringer ["strer"]
           :a/some-more "asdf"}})

(t/deftest doc1-read
  (with-open [file (io/input-stream file-name-1)]
    (t/is (= exp-1 (cxs/parse {::cxs/namespaces {tns 'a}} expected-schema file)))))
