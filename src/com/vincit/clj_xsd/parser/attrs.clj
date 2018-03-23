(ns com.vincit.clj-xsd.parser.attrs
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.schema :as hs]
            [com.rpl.specter :as sc]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as s]))

(defn is-correctly-namespaced? [maybe-attr-def tag]
  (let [form (::hs/form maybe-attr-def)]
    (or (and (not= ::hs/qualified form) (nil? (hx/extract-namespace tag)))
        (and (= ::hs/qualified form) (hx/extract-namespace tag)))))

(defn get-parser [opts type]
  (let [parser (utils/get-parser opts type utils/simple-parsers-path)]
    (or (when parser (partial parser opts))
        (fn [val] val))))

(defn parse-attr [opts schema attrs-def [tag val]]
  (let [curr-ns   (:com.vincit.clj-xsd.parser/curr-ns opts)
        attr-name (hx/extract-tag curr-ns tag)
        attr-def  (get attrs-def attr-name)
        type      (::hs/type attr-def)
        parser    (get-parser opts type)]
    (when (is-correctly-namespaced? attr-def tag)
      [(->> attr-name
            (utils/make-kw opts))
       (parser val)])))

(defn has-default? [[_ attr-def]]
  (contains? attr-def ::hs/default))

(defn parse-default [opts {:keys [::hs/default ::hs/type]}]
  (let [parser (get-parser opts type)]
    (parser default)))

(defn get-defaults [opts attrs-def]
  (->> (filter has-default? attrs-def)
       (sc/transform [sc/ALL sc/FIRST] (partial utils/make-kw opts))
       (sc/transform [sc/ALL sc/LAST] (partial parse-default opts))
       (into {})))

(s/fdef parse-attrs
        :params (s/cat :opts (constantly true)
                       :schema ::hs/schema
                       :attrs-def ::hs/attrs
                       :element (s/keys)))

(defn parse-attrs [opts schema attrs-def element]
  (let [attrs         (:attrs element)
        defaults      (get-defaults opts attrs-def)
        attrs-present (->> (map (partial parse-attr opts schema attrs-def) (:attrs element))
                           (apply concat)
                           (apply hash-map))]
    (when attrs-def
      (merge defaults attrs-present))))
