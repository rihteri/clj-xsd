(ns hipsterprise.parser.attrs
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.parser.utils :as utils]
            [hipsterprise.schema :as hs]
            [com.rpl.specter :as sc]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as s]))

(defn is-correctly-namespaced? [maybe-attr-def tag]
  (let [form (::hs/form maybe-attr-def)]
    (or (and (not= ::hs/qualified form) (nil? (hx/extract-namespace tag)))
        (and (= ::hs/qualified form) (hx/extract-namespace tag)))))

(defn parse-attr [opts schema attrs-def [tag val]]
  (let [curr-ns   (:hipsterprise.parser/curr-ns opts)
        attr-name (hx/extract-tag curr-ns tag)
        attr-def  (get attrs-def attr-name)
        type      (::hs/type attr-def)
        parser    (or (get-in opts [:hipsterprise.core/parsers :hipsterprise.core/simple type])
                      (fn [_ val] val))]
    (when (is-correctly-namespaced? attr-def tag)
      [(->> attr-name
            (utils/make-kw opts))
       (parser opts val)])))

(defn has-default? [[_ attr-def]]
  (contains? attr-def ::hs/default))

(defn get-defaults [opts attrs-def]
  (->> (filter has-default? attrs-def)
       (sc/transform [sc/ALL sc/FIRST] (partial utils/make-kw opts))
       (sc/transform [sc/ALL sc/LAST] ::hs/default)
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
