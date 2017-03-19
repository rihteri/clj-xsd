(ns hipsterprise.core-test.schema-1
  (:require [clojure.test :as t]
            [hipsterprise.xml :as hx]
            [clojure.java.io :as io]
            [hipsterprise.schema :as hs]
            [hipsterprise.core :as hipsterprise]
            [hipsterprise.metaschema :as xs]
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
  {::hs/tns   ex-ns
   ::hs/elems {(ex "top") {::hs/type (ex "topType")}}
   ::hs/types {(ex "topType") {::hs/attrs   {(ex "soma") {::hs/type xs/string
                                                          ::hs/form ::hs/qualified}
                                             (ex "numa") {::hs/type xs/integer}}
                               ::hs/content [::hs/sequence {::hs/vals [{::hs/element (ex "a")
                                                                        ::hs/multi   [0 1]
                                                                        ::hs/type    (ex "subType")}
                                                                       {::hs/element (ex "b")
                                                                        ::hs/multi   [1 1]
                                                                        ::hs/type    xs/string}
                                                                       {::hs/element (ex "c")
                                                                        ::hs/multi   [0 :n]
                                                                        ::hs/type    xs/string}]}]}
               (ex "subType") {::hs/content [::hs/sequence {::hs/vals  [{::hs/element (ex "ugh")
                                                                         ::hs/multi   [1 1]
                                                                         ::hs/type    xs/string}
                                                                        {::hs/element (ex "argh")
                                                                         ::hs/multi   [1 1]
                                                                         ::hs/type    xs/integer}]}]}}})
(def opts
  {::hipsterprise/namespaces {ex-ns *ns*}})

(t/deftest parsing-doc
  (t/is (= {::a    {::ugh  "jabada"
                    ::argh 1}
            ::b    "asdf"
            ::c    ["123" "!!!"]
            ::soma "jau"
            ::numa 42}
           (with-open [file (io/input-stream data-1)]
             (::top (hipsterprise/parse opts
                                        expected-schema
                                        file))))))

#_(t/deftest reading-schema
  (t/is (= expected-schema
           (do-read-schema))))

(t/deftest parsing-schema
  (t/is (= {::xs/target-namespace     ex-ns
            ::xs/element-form-default ::xs/qualified
            ::xs/element              [{::xs/name "top"
                                        ::xs/type "topType"}]
            ::xs/complex-type         [{::xs/name    "topType"
                                        ::xs/sequence {::xs/element [{::xs/name       "a"
                                                                      ::xs/type       "subType"
                                                                      ::xs/min-occurs 0
                                                                      ::xs/max-occurs 1}
                                                                     {;TODO etc
                                                                      }]}}]}
           (with-open [sch (io/input-stream schema-1)]
             (-> (hipsterprise/parse xs/parse-opts
                                     xs/schemaschema
                                     sch)
                 ::xs/schema)))))

