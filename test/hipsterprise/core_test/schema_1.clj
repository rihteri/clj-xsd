(ns hipsterprise.core-test.schema-1
  (:require [clojure.test :as t]
            [hipsterprise.xml :as hx]
            [clojure.java.io :as io]
            [hipsterprise.schema :as hs]
            [hipsterprise.core :as hipsterprise]
            [hipsterprise.metaschema :as metaschema]
            [clojure.data.xml :as xml]))

(def schema-1 "test_resources/schema1.xsd")
(def data-1 "test_resources/doc1.xml")
(def ex-ns "http://example.org/test-schema-1")

(def orderperson
  {::hx/name "orderperson"
   ::hx/ns   ex-ns})

(defn do-read-schema []
  (with-open [sch (io/input-stream schema-1)]
    (hipsterprise/read-schema sch)))

(t/deftest reading-schema
  (t/is (= {::hs/elems {orderperson {::hs/type metaschema/string}}}
           (do-read-schema))))

(t/deftest parsing-schema
  (t/is (= {::metaschema/target-namespace ex-ns
            ::metaschema/schema-top       [{ ; TODO type element
                                            ::metaschema/name {::hx/name "orderperson"
                                                               ::hs/ns   ex-ns}
                                            ::metaschema/type metaschema/string}]}
           (with-open [sch (io/input-stream schema-1)]
             (hipsterprise/parse metaschema/parse-opts
                                 metaschema/schemaschema
                                 sch)))))

(t/deftest parsing-doc
  (t/is (= {::orderperson "jabadaba"}
           (with-open [file (io/input-stream data-1)]
             (hipsterprise/parse {}
                                 (do-read-schema)
                                 file)))))
