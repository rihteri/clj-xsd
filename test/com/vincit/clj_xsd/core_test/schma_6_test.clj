(ns com.vincit.clj-xsd.core-test.schma-6-test
  (:require [clojure.test :as t]
            [com.vincit.clj-xsd.core :as cxs]
            [clojure.java.io :as io]
            [com.vincit.clj-xsd.metaschema :as metaschema]
            [com.vincit.clj-xsd.metaschema.options :as metaschema-opts]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema.nss :as xs-nss]))

(def xs-string [xs-nss/sns "string"])
(def xs-integer [xs-nss/sns "integer"])

(def tns "testlist")

(def exp-1 {:l/test ["a" "b" "c"]})

(def opts {::cxs/namespaces {"testlist" 'l}})

(defn read-schema [filename]
  (with-open [schema-file (io/input-stream filename)]
    (cxs/read-schema schema-file)))

(defn make-schema [simple-def]
  {::hs/tns "testlist"
   ::hs/elems {["testlist" "test"]
               {::hs/type ["testlist" "strlist"]}}
   ::hs/types {["testlist" "strlist"]
               simple-def}})

(t/deftest test-list
  (with-open [doc1-file (io/input-stream "test_resources/doc6_1.xml")]
    (let [sch      (make-schema {::hs/list-of xs-string})
          sch-read (read-schema "test_resources/schema6.xsd")
          doc-1    (cxs/parse opts sch doc1-file)]
      (t/is (= exp-1 doc-1)))))

(t/deftest test-restriction
  (with-open [doc1-file (io/input-stream "test_resources/doc6_1.xml")]
    (let [sch      (make-schema {::hs/base xs-string})
          sch-read (read-schema "test_resources/schema6_1.xsd")
          doc-1    (cxs/parse opts sch doc1-file)]
      (t/is (= {:l/test "a b c"} doc-1))
      (t/is (= sch sch-read)))))

(t/deftest test-union
  (with-open [doc1-file (io/input-stream "test_resources/doc6_1.xml")]
    (let [sch (make-schema {::hs/union-of #{xs-string xs-integer}})

          sch-read (read-schema "test_resources/schema6_2.xsd")
          doc-1    (cxs/parse opts sch doc1-file)]
      (t/is (= {:l/test "a b c"} doc-1))
      (t/is (= sch sch-read)))))
