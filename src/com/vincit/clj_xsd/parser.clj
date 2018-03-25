(ns com.vincit.clj-xsd.parser
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.schema :as hs]
            [clojure.string :as str]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.parser.default-parsers :as parsers]
            [com.vincit.clj-xsd.parser.attrs :as attrs]
            [com.vincit.clj-xsd.parser.extension :as ep]
            [com.vincit.clj-xsd.parser.element :as pe]
            [com.vincit.clj-xsd.parser.context :as pcont]
            [com.vincit.clj-xsd.parser.content.sequence]
            [com.vincit.clj-xsd.parser.content.choice]
            [clojure.data.xml :as xml]))

(def default-simple-parsers
  {xs/integer parsers/parse-integer
   xs/qname   parsers/parse-qname})

(defn parse [opts schema element]
  (let [namespaces (hx/extract-namespace-mappings element)
        opts       (-> opts
                       (update-in utils/simple-parsers-path
                                  #(merge default-simple-parsers %))
                       (assoc ::xml/nss namespaces)
                       (assoc ::hs/schema schema))
        el-default (::hs/el-default schema)
        curr-ns    (::pcont/curr-ns opts)
        curr-el    (->> element :tag (hx/extract-tag el-default curr-ns))
        el-def     (get-in schema [::hs/elems curr-el])
        content    (pe/parse-element opts el-def element)]
    {(utils/make-kw opts curr-el) content}))
