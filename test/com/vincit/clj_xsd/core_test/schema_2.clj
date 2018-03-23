(ns com.vincit.clj-xsd.core-test.schema-2
  (:require [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.core :as cxs]
            [clojure.test :as t]
            [clojure.java.io :as io]))

(def schema-1 "test_resources/schema2.xsd")
(def ex-ns "http://example.org/test-schema-2")

(defn do-read-schema-xml []
  (io/input-stream schema-1))

(defn do-read-schema []
  (with-open [sch (do-read-schema-xml)]
    (cxs/read-schema sch)))

(def expected-schema
  {::hs/tns        ex-ns
   ::hs/el-default ::hs/qualified
   ::hs/elems      {[ex-ns "top"] {::hs/type [ex-ns "topType"]}}
   ::hs/types      {[ex-ns "topType"] {::hs/content [::hs/choice {::hs/elems {[ex-ns "a"] {::hs/multi [0 1]
                                                                                           ::hs/type  [ex-ns "subType"]}
                                                                              [ex-ns "b"] {::hs/multi [1 1]
                                                                                           ::hs/type  xs/string}}}]}
                    [ex-ns "subType"] {::hs/content [::hs/sequence {::hs/vals [{::hs/element [ex-ns "ugh"]
                                                                                ::hs/multi   [1 1]
                                                                                ::hs/type    xs/string}]}]}}})

(t/deftest schema-read
  (let [exp expected-schema
        act (do-read-schema)]
    (let [get-content (fn [m] (-> m
                                  (get-in [::hs/types [ex-ns "topType"] ::hs/content])
                                  second))
          [exp act] (map get-content [exp act])]
      (t/is (= exp act)))
    (t/is (= exp act))))

(def expected-doc-1
  {:top {:a {:ugh "jabada"}}})

(t/deftest doc-read-1
  (with-open [doc (io/input-stream "test_resources/doc2_1.xml")]
    (t/is (= expected-doc-1 (cxs/parse expected-schema doc)))))

(def expected-doc-2
  {:top {:b "jabada"}})

(t/deftest doc-read-2
  (with-open [doc (io/input-stream "test_resources/doc2_2.xml")]
    (t/is (= expected-doc-2 (cxs/parse expected-schema doc)))))
