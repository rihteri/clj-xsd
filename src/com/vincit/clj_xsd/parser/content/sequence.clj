(ns com.vincit.clj-xsd.parser.content.sequence
  (:require [com.vincit.clj-xsd.parser.content.core :as cp]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.parser.element :as pe]))

(defn make-kw [context el-def]
  (->> el-def
       ::hs/element
       (utils/make-kw context)))

(defn split-curr-next
  "split elements to those that should be deserialized as el-def and the rest"
  [context el-def elements]
  (let [max-occurs   (utils/get-max-occurs el-def)
        is-type?     (->> el-def
                          ::hs/element
                          (partial utils/element-is? context))
        [this other] (split-with is-type? elements)]
    (if (and (not= max-occurs :n)
             (> (count this) max-occurs))
      (split-at max-occurs elements)
      [this other])))

(defn sub-content?
  "is this a sub-clause like another <sequence> or <choice>?"
  [el-def]
  (sequential? el-def))

(defn run-parser [el-def parser elements]
  (if (utils/is-plural? el-def)
    (map parser elements)
    (some-> elements first parser)))

(defn assoc-valid [accum kw result]
  (if (and (some? kw) (some? result))
    (assoc-in accum [::cp/result kw] result)
    accum))

(defn parse-one [accum context el-def els next-els]
  (let [kw            (make-kw context el-def)
        parse-element (partial pe/parse-element context el-def)]
    (-> (->> els
             (run-parser el-def parse-element)
             (assoc-valid accum kw))
        (assoc ::cp/elements next-els))))

(defn parse-elements [context accum [el-def & next-defs]]
  (let [elements            (::cp/elements accum)
        [curr-els next-els] (split-curr-next context el-def elements)
        accum               (if (sub-content? el-def)
                              (cp/parse-content context el-def accum)
                              (parse-one accum context el-def curr-els next-els))]
    (if (or (empty? next-els)
            (empty? next-defs))
      accum
      (parse-elements context accum next-defs))))

(defmethod cp/parse-content ::hs/sequence [context [kind content-def] accum]
  (parse-elements context accum (::hs/vals content-def)))
