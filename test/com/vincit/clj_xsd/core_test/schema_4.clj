(ns com.vincit.clj-xsd.core-test.schema-4
  (:require [clojure.test :as t]
            [clojure.java.io :as io]
            [com.vincit.clj-xsd.core :as cxs]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.rpl.specter :as sc]))

(def tns "http://example.org/test-schema-4")

(def expected-1
  {:top {:att "asdf"
         :ugh {:c ["123" "!!!"]}}})

(def expected-schema
  {::hs/tns   tns
   ::hs/elems {[tns "top"]
               {::hs/type-def
                {::hs/attrs
                 {[tns "att"] {::hs/type xs/string}}
                 ::hs/content
                 [::hs/sequence
                  {::hs/vals [{::hs/element [tns "ugh"]
                               ::hs/multi   [1 1]
                               ::hs/type-def
                               {::hs/content
                                [::hs/sequence
                                 {::hs/vals [{::hs/element [tns "c"]
                                              ::hs/type    xs/string
                                              ::hs/multi   [0 :n]}]}]}}]}]}}}})

(defn read-schema []
  (with-open [file (io/input-stream "test_resources/schema4.xsd")]
    (cxs/read-schema file)))

(t/deftest anon-type-schema
  (let [exp expected-schema
        act (read-schema)]
    (let [[exp act] (->> [exp act]
                         (map #(get-in % [::hs/elems
                                          [tns "top"]
                                          ::hs/type-def]))
                         (map #(dissoc % ::hs/content)))]
      (t/is (= exp act)))
    (let [[exp act] (->> [exp act]
                         (map #(sc/select [::hs/elems
                                           (sc/keypath [tns "top"])
                                           ::hs/type-def
                                           ::hs/content
                                           sc/LAST
                                           ::hs/vals]
                                          %)))]
      (t/is (= exp act)))
    (let [[exp act] (map #(get-in % [::hs/elems [tns "top"]]) [exp act])]
      (t/is (= exp act)))
    (t/is (= exp act))))

(t/deftest anon-type-read
  (with-open [file (io/input-stream "test_resources/doc4_1.xml")]
    (t/is (= expected-1 (cxs/parse expected-schema file)))))
