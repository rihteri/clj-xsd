(ns com.vincit.clj-xsd.parser.utils-test
  (:require [com.vincit.clj-xsd.parser.utils :as sut]
            [clojure.test :as t]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser :as parser]
            [com.vincit.clj-xsd.parser.context :as pcont]))

(t/deftest make-kw
  (t/is (= :ugh (sut/make-kw {} ["discard-ns" "ugh"]))
        "non-namespaced kw when none supplied in opts")
  (t/is (= :some.ns/ugh (sut/make-kw {:com.vincit.clj-xsd.core/namespaces {"use-ns" 'some.ns}} ["use-ns" "ugh"]))
        "namespaced kw when supplied in opts")
  (t/is (= :ugh (sut/make-kw {} [nil "ugh"]))
        "non-namespaced ncname"))

(t/deftest is-plural
  (t/is (sut/is-plural? {::hs/multi [0 :n]}))
  (t/is (sut/is-plural? {::hs/multi [0 10]}))
  (t/is (not (sut/is-plural? {})))
  (t/is (not (sut/is-plural? {::hs/multi [0 1]}))))

(t/deftest element-is?
  (t/is (sut/element-is? {} ["asdf" "a"] {:tag :asdf/a}))
  (t/is (sut/element-is? {::pcont/curr-ns "asdf"
                          ::hs/schema      {::hs/el-default ::hs/unqualified}}
                         ["asdf" "a"]
                         {:tag :a})))

(t/deftest element-in?
  (t/is (sut/element-in? {} [["asdf" "a"]
                             ["asdf" "b"]] {:tag :asdf/a}))
  (t/is (not (sut/element-in? {} [["asdf" "a"]
                                  ["asdf" "b"]] {:tag :asdf/c}))))
