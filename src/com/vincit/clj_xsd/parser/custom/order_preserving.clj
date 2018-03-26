(ns com.vincit.clj-xsd.parser.custom.order-preserving
  (:require [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.extension :as ep]
            [com.vincit.clj-xsd.parser.attrs :as attrs]
            [com.rpl.specter :as sc]
            [com.vincit.clj-xsd.parser.element :as pe]
            [com.vincit.clj-xsd.parser.utils :as utils]))

(defmulti get-el-type-map (fn [[type def]] type))

(defn is-sub-group? [el-or-sub-def]
  (sequential? el-or-sub-def))

(def is-element? (complement is-sub-group?))

(defmethod get-el-type-map ::hs/choice
  [[_ def]]
  (::hs/elems def))

(defmethod get-el-type-map ::hs/sequence
  [[_ {:keys [::hs/vals]}]]
  (let [el-defs (->> vals
                     (filter is-element?)
                     (group-by ::hs/name)
                     (sc/transform [sc/MAP-VALS] first))
        sub-defs (->> vals
                      (filter is-sub-group?))]
    (merge el-defs
           (get-el-type-map sub-defs))))

(defn parse-one [context el-defs el]
  (if (string? el)
    el
    (let [el-name (utils/extract-tag context el)
          el-def  (get el-defs el-name)
          kw      (utils/make-kw context el-name)]
      [kw (pe/parse-element context el-def el)])))

(defn make-order-preserving-parser
  "Make a parser that returns a structure
  [{:attr map} [:ns/el-name {:element result}] ... str ...]"
  [type]
  (fn [context el]
    (let [type-def (->> (get-in context [::hs/schema ::hs/types type])
                        (ep/unwrap-type context))
          attrs    (attrs/parse-attrs context (::hs/attrs type-def) el)
          el-defs  (get-el-type-map (::hs/content type-def))]
      (into [(or attrs {})]
            (map (partial parse-one context el-defs) (:content el))))))
