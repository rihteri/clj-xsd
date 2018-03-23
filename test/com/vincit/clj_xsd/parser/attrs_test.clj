(ns com.vincit.clj-xsd.parser.attrs-test
  (:require [com.vincit.clj-xsd.parser.attrs :as sut]
            [clojure.test :as t]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.metaschema :as xs]
            [clojure.spec.test.alpha :as st]
            [clojure.data.xml :as xml]
            [com.vincit.clj-xsd.metaschema :as metaschema]
            [com.vincit.clj-xsd.parser :as parser]))

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

(defn schema [top-type-attrs]
  {::hs/tns        tns
   ::hs/el-default ::hs/qualified
   ::hs/elems      {[tns "top"] {::hs/type [tns "topType"]}}
   ::hs/types      {[tns "topType"] {::hs/attrs top-type-attrs}}})

(t/deftest get-defaults-test
  (t/is (= {:soma "ugh"
            :ana  "sadf"}
           (sut/get-defaults {} attrs-def))))

(def xml
  "<n:top xmlns:n=\"some-ns\" n:ana=\"another attr\" />")

(def xml-map (-> xml xml/parse-str))

(def parse-opts
  {:com.vincit.clj-xsd.core/namespaces {tns *ns*}})

(t/deftest parse-attrs-optionals
  (let [expected {::soma "ugh"         ; default
                  ::ana  "another attr" } ; not default
        parsed   (sut/parse-attrs parse-opts (schema attrs-def) attrs-def xml-map)]
    (t/is (= expected parsed) "attr defaults respected")))


(t/deftest parse-attrs-types
  (let [attr-def {[tns "numa"] {::hs/type    xs/integer
                                ::hs/default "42"
                                ::hs/form    ::hs/unqualified}}
        expected {:numa 42}
        parse-opts (assoc-in {} parser/simple-parsers-path parser/default-simple-parsers)
        parsed (sut/parse-attrs parse-opts (schema attr-def) attr-def xml-map)]
    (t/is (= expected parsed))))
