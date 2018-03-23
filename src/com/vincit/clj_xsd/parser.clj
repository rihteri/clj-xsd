(ns com.vincit.clj-xsd.parser
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.schema :as hs]
            [clojure.string :as str]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.parser.default-parsers :as parsers]
            [com.vincit.clj-xsd.parser.attrs :as attrs]
            [clojure.data.xml :as xml]))

(declare parse-element)

(defmulti parse-content (fn [kind opts schema content-def elements] kind))

(defmethod parse-content ::hs/choice [kind opts schema content-def elements]
  (let [el            (first elements)
        el-name       (hx/extract-tag (:tag el))
        el-def        (get-in content-def [::hs/elems el-name])
        type-to-parse (::hs/type el-def)
        kw            (utils/make-kw opts el-name)
        parsed        (parse-element opts schema type-to-parse nil el)
        my-result     {kw (if (or (utils/is-plural? content-def)
                                  (utils/is-plural? el-def))
                            [parsed]
                            parsed)}
        more-elems    (when (not (empty? (rest elements)))
                        (parse-content ::hs/choice
                                       opts
                                       schema
                                       content-def
                                       (rest elements)))]
    (if (empty? more-elems)
      my-result
      (merge-with concat my-result more-elems))))

(defmethod parse-content ::hs/sequence [kind opts schema content-def elements]
  (when (and (not (empty? elements))
             (not (empty? (-> content-def ::hs/vals))))
    (let [cur-el-def       (-> content-def ::hs/vals first)
          element-to-parse (::hs/element cur-el-def)
          type-to-parse    (::hs/type cur-el-def)
          is-type?         (partial utils/element-is? element-to-parse)
          elements-of-type (take-while is-type? elements)
          do-parse-next    (partial parse-content
                                    kind
                                    opts
                                    schema
                                    (assoc content-def ::hs/vals (rest (::hs/vals content-def)))
                                    (drop-while is-type? elements))
          result           (map (partial parse-element
                                         opts
                                         schema
                                         type-to-parse
                                         nil) elements-of-type)
          result           (if (utils/is-plural? cur-el-def)
                             result
                             (first result))
          kw               (utils/make-kw opts element-to-parse)]
      (if (empty? elements-of-type)
        (do-parse-next)
        (merge {kw result}
               (when (-> elements empty? not)
                 (do-parse-next)))))))

(defn do-parse-content [opts schema el-type el-type-def element]
  (let [[kind content-def] (get-in el-type-def [::hs/content])
        get-parser         (partial utils/get-parser opts el-type)
        custom-parser      (or (get-parser utils/complex-parsers-path)
                               (utils/make-element-parser
                                (get-parser utils/simple-parsers-path)))
        elements           (->> element
                                :content
                                (filter (complement string?)))]
    (if custom-parser
      (custom-parser opts element)
      (if kind
        (parse-content kind opts schema content-def elements)
        (parsers/parse-string opts (:content element))))))

(defn parse-element [opts schema el-type el-type-def element]
  (let [opts        (utils/update-ns opts element)
        el-type-def (or el-type-def (get-in schema [::hs/types el-type]))
        attrs-def   (::hs/attrs el-type-def)
        attrs       (attrs/parse-attrs opts schema attrs-def element)
        content     (do-parse-content opts schema el-type el-type-def element)]
    (if attrs
      (merge attrs content)
      content)))

(def default-simple-parsers
  {xs/integer parsers/parse-integer
   xs/qname   parsers/parse-qname})

(defn parse [opts schema element]
  (let [namespaces (hx/extract-namespace-mappings element)
        opts       (-> opts
                       (update-in utils/simple-parsers-path
                                  #(merge default-simple-parsers %))
                       (assoc ::xml/nss namespaces))
        curr-el    (-> element :tag hx/extract-tag)
        el-type    (get-in schema [::hs/elems curr-el ::hs/type])
        content    (parse-element opts
                                  schema
                                  el-type
                                  nil ; TODO inline type
                                  element)]
    {(utils/make-kw opts curr-el) content}))
