(ns hipsterprise.metaschema-test
  (:require [hipsterprise.metaschema :as sut]
            [clojure.test :as t]
            [clojure.spec.alpha :as s]
            [hipsterprise.schema :as hs]))

(t/deftest metaschema-spec
  (t/is (s/valid? ::hs/schema sut/schemaschema)
        (s/explain-str ::hs/schema sut/schemaschema)))
