(ns hipsterprise.schema-parser-test
  (:require [hipsterprise.schema-parser :as sut]
            [clojure.test :as t]
            [hipsterprise.metaschema :as xs]
            [hipsterprise.schema :as hs]))

(def tns "http://example.org/test-schema-1")

(def content-in
  [{::xs/element [{::xs/name "a",
                    ::xs/max-occurs 1,
                    ::xs/type [tns "subType"]
                   ::xs/min-occurs 0}
                  {::xs/name "b"
                   ::xs/max-occurs 1
                   ::xs/type [xs/sns "string"]
                   ::xs/min-occurs 1}
                  {::xs/name "c"
                   ::xs/max-occurs :n
                   ::xs/type [xs/sns "string"]
                   ::xs/min-occurs 0}]}])

(def content-out
  [::hs/sequence {::hs/vals [{::hs/element [tns "a"]
                              ::hs/multi   [0 1]
                              ::hs/type    [tns "subType"]}
                             {::hs/element [tns "b"]
                              ::hs/multi   [1 1]
                              ::hs/type    xs/string}
                             {::hs/element [tns "c"]
                              ::hs/multi   [0 :n]
                              ::hs/type    xs/string}]}])

(t/deftest fix-seq
  (let [[exp act] (map second [content-out
                               (sut/fix-seq tns content-in)])]
    (t/is (= exp act))))
