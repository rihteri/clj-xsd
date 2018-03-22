(ns com.vincit.clj-xsd.parser.attrs-test
  (:require [com.vincit.clj-xsd.parser.attrs :as sut]
            [clojure.test :as t]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.metaschema :as xs]
            [clojure.spec.test.alpha :as st]
            [clojure.data.xml :as xml]
            [com.vincit.clj-xsd.metaschema :as metaschema]))

(st/instrument (st/enumerate-namespace 'com.vincit.clj-xsd.parser.attrs))

(def tns "some-ns")

(def attrs-def
  {[tns "soma"] {::hs/type    xs/string
                 ::hs/form    ::hs/qualified
                 ::hs/default "ugh"
                 ::hs/use     ::hs/optional}
   [tns "ana"]  {::hs/type    xs/string
                 ::hs/form    ::hs/qualified
                 ::hs/default "sadf"}
   [tns "numa"] {::hs/type xs/integer
                 ::hs/form ::hs/unqualified}})

(def schema
  {::hs/tns        tns
   ::hs/el-default ::hs/qualified
   ::hs/elems      {[tns "top"] {::hs/type [tns "topType"]}}
   ::hs/types      {[tns "topType"] {::hs/attrs attrs-def}}})

(t/deftest get-defaults-test
  (t/is (= {:soma "ugh"
            :ana  "sadf"}
           (sut/get-defaults {} attrs-def))))

(def xml
  "<n:top xmlns:n=\"some-ns\" n:ana=\"another attr\" />")

(def parse-opts
  {:com.vincit.clj-xsd.core/namespaces {tns *ns*}})

(t/deftest parse-attrs-optionals
  (let [expected {::soma "ugh"         ; default
                  ::ana  "another attr" } ; not default
        xml-map  (-> xml xml/parse-str)
        parsed   (sut/parse-attrs parse-opts schema attrs-def xml-map)]
    (t/is (= expected parsed) "attr defaults respected")))
