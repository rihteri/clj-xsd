(ns com.vincit.clj-xsd.schema-parser-test
  (:require [com.vincit.clj-xsd.schema-parser :as sut]
            [clojure.test :as t]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]))

(def tns "http://example.org/test-schema-1")

(def content-in
  {::xs/sequence {::xs/element [{::xs/name       "a",
                                 ::xs/max-occurs 1,
                                 ::xs/type       [tns "subType"]
                                 ::xs/min-occurs 0}
                                {::xs/name       "b"
                                 ::xs/max-occurs 1
                                 ::xs/type       [xs/sns "string"]
                                 ::xs/min-occurs 1}
                                {::xs/name       "c"
                                 ::xs/max-occurs :n
                                 ::xs/type       [xs/sns "string"]
                                 ::xs/min-occurs 0}]}})

(def content-out
  {::hs/content [::hs/sequence {::hs/vals [{::hs/element [tns "a"]
                                            ::hs/multi   [0 1]
                                            ::hs/type    [tns "subType"]}
                                           {::hs/element [tns "b"]
                                            ::hs/multi   [1 1]
                                            ::hs/type    xs/string}
                                           {::hs/element [tns "c"]
                                            ::hs/multi   [0 :n]
                                            ::hs/type    xs/string}]}]})

(t/deftest fix-seq
  (let [result    (sut/fix-content tns content-in)
        [exp act] (map second [content-out result])]
    (t/is (= exp act))))
