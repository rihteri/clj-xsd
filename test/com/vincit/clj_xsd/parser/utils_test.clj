(ns com.vincit.clj-xsd.parser.utils-test
  (:require [com.vincit.clj-xsd.parser.utils :as sut]
            [clojure.test :as t]))

(t/deftest make-kw
  (t/is (= :ugh (sut/make-kw {} ["discard-ns" "ugh"]))
        "non-namespaced kw when none supplied in opts")
  (t/is (= :some.ns/ugh (sut/make-kw {:com.vincit.clj-xsd.core/namespaces {"use-ns" 'some.ns}} ["use-ns" "ugh"]))
        "namespaced kw when supplied in opts")
  (t/is (= :ugh (sut/make-kw {} [nil "ugh"]))
        "non-namespaced ncname"))
