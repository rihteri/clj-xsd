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

(defn ex [name]
  {::hx/name name
   ::hx/ns   ex-ns})

(defn do-read-schema []
  (with-open [sch (io/input-stream schema-1)]
    (hipsterprise/read-schema sch)))

(def expected-schema
  {::hs/elems {(ex "top") {::hs/type (ex "topType")}}
   ::hs/types {(ex "topType") {::hs/content [::hs/sequence [{::hs/element (ex "a")
                                                             ::hs/multi   [0 1]
                                                             ::hs/type    metaschema/string}
                                                            {::hs/element (ex "b")
                                                             ::hs/multi   [1 1]
                                                             ::hs/type    metaschema/string}
                                                            {::hs/element (ex "c")
                                                             ::hs/multi   [0 :n]
                                                             ::hs/type    metaschema/string}]]}}})
(def opts
  {::hipsterprise/namespaces {ex-ns *ns*}})

(t/deftest parsing-doc
  (t/is (= {::top {::b "asdf"
                   ::c ["123" "!!!"]}}
           (with-open [file (io/input-stream data-1)]
             (hipsterprise/parse opts
                                 expected-schema
                                 file)))))

#_(t/deftest reading-schema
  (t/is (= expected-schema
           (do-read-schema))))

#_(t/deftest parsing-schema
  (t/is (= {::metaschema/target-namespace ex-ns
            ; TODO missing
            }
           (with-open [sch (io/input-stream schema-1)]
             (hipsterprise/parse metaschema/parse-opts
                                 metaschema/schemaschema
                                 sch)))))
