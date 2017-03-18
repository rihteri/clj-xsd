(ns hipsterprise.parser
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.schema :as hs]
            [clojure.string :as str]
            [hipsterprise.metaschema :as xs]
            [hipsterprise.parser.utils :as utils]
            [hipsterprise.parser.default-parsers :as parsers]
            [hipsterprise.parser.attrs :as attrs]))

(declare parse-element)

(defmulti parse-content (fn [kind opts schema content-def elements] kind))
(defmethod parse-content ::hs/sequence [kind opts schema content-def elements]
  (when (not (empty? elements))
    (let [elements         (filter (complement string?) elements)
          cur-el-def       (-> content-def first)
          element-to-parse (::hs/element cur-el-def)
          type-to-parse    (::hs/type cur-el-def)
          upper-bound      (-> cur-el-def
                               ::hs/multi
                               second)
          is-type?         (partial utils/element-is? element-to-parse)
          elements-of-type (take-while is-type? elements)
          do-parse-next    (partial parse-content
                                    kind
                                    opts
                                    schema
                                    (rest content-def)
                                    (drop-while is-type? elements))
          result           (map (partial parse-element
                                         opts
                                         schema
                                         type-to-parse
                                         nil) elements-of-type)
          result           (if (or (= :n upper-bound) (> 1 upper-bound))
                             result
                             (first result))
          kw               (utils/make-kw opts element-to-parse)]
      (if (empty? elements-of-type)
        (do-parse-next)
        (into [kw result]
              (when (-> elements empty? not)
                (do-parse-next)))))))

(defn do-parse-content [opts schema el-type el-type-def element]
  (let [[kind content-def] (get-in el-type-def [::hs/content])
        custom-parser      (or (get-in opts [::parsers ::complex el-type])
                               (utils/make-element-parser
                                (get-in opts [:hipsterprise.core/parsers
                                              :hipsterprise.core/simple
                                              el-type])))]
    (if custom-parser
      (custom-parser element)
      (if kind
        (apply hash-map (parse-content kind opts schema content-def (:content element)))
        (parsers/parse-string (:content element))))))

(defn parse-element [opts schema el-type el-type-def element]
  (let [el-type-def (or el-type-def (get-in schema [::hs/types el-type]))
        attrs-def   (::hs/attrs el-type-def)
        attrs       (attrs/parse-attrs opts schema attrs-def element)
        content     (do-parse-content opts schema el-type el-type-def element)]
    (if attrs
      (merge attrs content)
      content)))

(def default-opts
  {:hipsterprise.core/parsers {:hipsterprise.core/simple {xs/integer parsers/parse-integer}}})

(defn parse [opts schema element]
  (let [opts    (merge default-opts opts) ; TODO does this merge properly?
        curr-el (-> element :tag hx/extract-tag)
        el-type (get-in schema [::hs/elems curr-el ::hs/type])
        content (parse-element opts
                               schema
                               el-type
                               nil ; TODO inline type
                               element)]
    {(utils/make-kw opts curr-el) content}))
