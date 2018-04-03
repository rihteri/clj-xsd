(ns com.vincit.clj-xsd.parser.custom.simple-types-test
  (:require [com.vincit.clj-xsd.parser.custom.simple-types :as sut]
            [clojure.test :as t]))

(t/deftest parse-double
  (t/is (= 3.3 (sut/parse-double {} "3.3")))
  (t/is (nil? (sut/parse-double {} "kolkytkolme"))))
