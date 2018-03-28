(ns com.vincit.clj-xsd.parser.attrs
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.schema :as hs]
            [com.rpl.specter :as sc]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as s]
            [com.vincit.clj-xsd.parser.context :as pcont]
            [com.vincit.clj-xsd.parser.simple :as simplep]))

(defn is-correctly-namespaced? [maybe-attr-def tag]
  (let [form (::hs/form maybe-attr-def)]
    (or (and (not= ::hs/qualified form) (nil? (hx/extract-namespace tag)))
        (and (= ::hs/qualified form) (hx/extract-namespace tag)))))

(defn parse [context type val]
  (let [type-def (get-in context [::hs/schema ::hs/types type])
        parser   (utils/get-parser context type utils/simple-parsers-path)]
    (or (if parser
          (parser context val)
          (simplep/parse-simple context type-def val))
        val)))

(defn parse-attr [context attrs-def [tag val]]
  (let [curr-ns   (::pcont/curr-ns context)
        form      (-> context ::hs/schema ::hs/attr-default)
        attr-name (hx/extract-tag form curr-ns tag)
        attr-def  (get attrs-def attr-name)
        attr-type (::hs/type attr-def)]
    (when (is-correctly-namespaced? attr-def tag)
      [(->> attr-name
            (utils/make-kw context))
       (parse context attr-type val)])))

(defn has-default? [[_ attr-def]]
  (contains? attr-def ::hs/default))

(defn parse-default [context {:keys [::hs/default ::hs/type]}]
  (parse context type default))

(defn get-defaults [context attrs-def]
  (->> (filter has-default? attrs-def)
       (sc/transform [sc/ALL sc/FIRST] (partial utils/make-kw context))
       (sc/transform [sc/ALL sc/LAST] (partial parse-default context))
       (into {})))

(s/fdef parse-attrs
        :params (s/cat :context (constantly true)
                       :attrs-def ::hs/attrs
                       :element (s/keys)))

(defn parse-attrs [context attrs-def element]
  (let [attrs         (:attrs element)
        defaults      (get-defaults context attrs-def)
        attrs-present (->> (map (partial parse-attr context attrs-def) (:attrs element))
                           (apply concat)
                           (apply hash-map))]
    (when attrs-def
      (merge defaults attrs-present))))
