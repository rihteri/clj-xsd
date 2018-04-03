(ns com.vincit.clj-xsd.core-test.schema-7-test
  (:require [com.vincit.clj-xsd.core :as cxs]
            [clojure.test :as t]
            [clojure.java.io :as io]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema.nss :as xss]))

  (def tns "http://example.org/test-schema-7")

(def exp-sch
  {::hs/tns        tns
   ::hs/el-default ::hs/qualified
   ::hs/elems
   {[tns "top"] {::hs/type [tns "topType"]}}
   ::hs/types
   {[tns "topType"] {::hs/base  [tns "strlist"]
                     ::hs/attrs {[tns "attrib"] {::hs/type [xss/sns "double"]}}}
    [tns "strlist"] {::hs/list-of [xss/sns "string"]}}})

(t/deftest schema-read
  (with-open [sf  (io/input-stream "test_resources/schema7.xsd")]
    (let [act (cxs/read-schema sf)]
        (t/is (= exp-sch act)))))

(def exp {:t/top {:t/attrib 3.14
                  :value    ["aa" "bee" "cee"]}})

(def opts {::cxs/namespaces {tns 't}})

(t/deftest doc-read
  (with-open [df (io/input-stream "test_resources/doc7.xml")]
    (let [doc (cxs/parse opts exp-sch df)]
      (t/is (= exp doc)))))
