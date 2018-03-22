(ns com.vincit.clj-xsd.metaschema-test
  (:require [com.vincit.clj-xsd.metaschema :as sut]
            [clojure.test :as t]
            [clojure.spec.alpha :as s]
            [com.vincit.clj-xsd.schema :as hs]))

(t/deftest metaschema-spec
  (t/is (s/valid? ::hs/schema sut/schemaschema)
        (s/explain-str ::hs/schema sut/schemaschema)))
