(ns com.vincit.clj-xsd.parser.element
  (:require [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.parser.default-parsers :as parsers]
            [com.vincit.clj-xsd.parser.extension :as ep]
            [com.vincit.clj-xsd.parser.attrs :as attrs]
            [com.vincit.clj-xsd.parser.content.core :as cp]
            [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.context :as pcont]))

(def xsi-type-kw
  :xmlns.http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema-instance/type)

(defn get-xsi-type [context element]
  (some->> element
           :attrs
           xsi-type-kw
           (parsers/parse-qname context)))

(defn update-ns [context element]
  (let [curr-ns (-> element :tag hx/extract-namespace)]
    (if curr-ns
      (assoc context ::pcont/curr-ns curr-ns)
      context)))

(defn get-custom-parser [context el-type]
  (let [get-parser (partial utils/get-parser context el-type)]
    (or (get-parser utils/complex-parsers-path)
        (utils/make-element-parser
         (get-parser utils/simple-parsers-path)))))

(defn parse-element [context el-def element]
  (let [context       (update-ns context element)
        xsi-type      (get-xsi-type context element)
        el-type       (or xsi-type (::hs/type el-def))
        el-type-def   (->> (or (when (nil? xsi-type) (::hs/type-def el-def))
                               (get-in context [::hs/schema ::hs/types el-type]))
                           (ep/unwrap-type context))
        attrs-def     (::hs/attrs el-type-def)
        attrs         (attrs/parse-attrs context attrs-def element)
        custom-parser (get-custom-parser context el-type)
        content       (if custom-parser
                        (custom-parser context element)
                        (cp/do-parse-content context el-type-def element))]
    (if attrs
      (merge attrs content)
      content)))
