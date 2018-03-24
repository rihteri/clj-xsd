(ns com.vincit.clj-xsd.core-test.schema-3
  (:require [clojure.test :as t]
            [clojure.java.io :as io]
            [com.vincit.clj-xsd.core :as cxs]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema :as xs]))

(def expected-doc-1
  {:top {:base {:soma "jau"
                :b    "jabada"}
         :sub  {:soma "yippe"
                :numa 123
                :ugh  "asdf"
                :b    "tsib dab"}}})

(def tns "http://example.org/test-schema-3")

(def expected-schema
  {::hs/tns        tns
   ::hs/el-default ::hs/qualified
   ::hs/elems      {[tns "top"] {::hs/type [tns "topType"]}}
   ::hs/types      {[tns "topType"]  {::hs/content [::hs/sequence {::hs/vals [{::hs/type    [tns "baseType"]
                                                                               ::hs/element [tns "base"]
                                                                               ::hs/multi   [1 1]}
                                                                              {::hs/type    [tns "subType"]
                                                                               ::hs/element [tns "sub"]
                                                                               ::hs/multi   [1 1]}]}]}
                    [tns "baseType"] {::hs/content [::hs/sequence {::hs/vals [{::hs/type    [xs/sns "string"]
                                                                               ::hs/element [tns "b"]
                                                                               ::hs/multi   [1 1]}]}]
                                      ::hs/attrs   {[tns "soma"] {::hs/type [xs/sns "string"]}}}
                    [tns "subType"]  {::hs/base    [tns "baseType"]
                                      ::hs/attrs   {[tns "numa"] {::hs/type [xs/sns "integer"]}}
                                      ::hs/content [::hs/sequence {::hs/vals [{::hs/type    [xs/sns "string"]
                                                                               ::hs/element [tns "ugh"]
                                                                               ::hs/multi   [1 1]}]}]}}})

(defn get-schema []
  (with-open [schema-file (io/input-stream "test_resources/schema3.xsd")]
    (cxs/read-schema schema-file)))

(t/deftest schema
  (let [schema (get-schema)]
    (let [[exp act] (map #(get-in % [::hs/types [tns "subType"]]) [expected-schema schema])]
      (t/is (= exp act) "extension type schema representation"))
    (let [[exp act] (map #(get-in % [::hs/types [tns "baseType"]]) [expected-schema schema])]
      (t/is (= exp act) "extension type schema representation"))
    (t/is (= expected-schema schema))))

(t/deftest extension
  (with-open [file (io/input-stream "test_resources/doc3_1.xml")]
    (let [act (cxs/parse (get-schema) file)]
      (t/is (= expected-doc-1 act)))))
