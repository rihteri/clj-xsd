(ns hipsterprise.parser.attrs
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.parser.utils :as utils]
            [hipsterprise.schema :as hs]))

(defn is-correctly-namespaced? [maybe-attr-def tag]
  (let [form (::hs/form maybe-attr-def)
        _ (println maybe-attr-def form (hx/extract-namespace tag))]
    (or (and (not= ::hs/qualified form) (nil? (hx/extract-namespace tag)))
        (and (= ::hs/qualified form) (hx/extract-namespace tag)))))

(defn parse-attr [opts schema curr-ns attrs-def [tag val]]
  (let [attr-name (hx/extract-tag curr-ns tag)
        attr-def  (get attrs-def attr-name)
        type      (::hs/type attr-def)
        parser    (or (get-in opts [:hipsterprise.core/parsers :hipsterprise.core/simple type])
                      identity)]
    (when (is-correctly-namespaced? attr-def tag)
      [(->> attr-name
            (utils/make-kw opts))
       (parser val)])))

(defn parse-attrs [opts schema attrs-def element]
  (let [curr-ns (hx/extract-namespace (:tag element))]
    (when attrs-def
      (->> (map (partial parse-attr opts schema curr-ns attrs-def) (:attrs element))
           (apply concat)
           (apply hash-map)))))
