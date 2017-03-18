(ns hipsterprise.parser.attrs
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.parser.utils :as utils]
            [hipsterprise.schema :as hs]))

(defn parse-attr [opts schema attrs-def [tag val]]
  (let [attr-name (hx/extract-tag tag)
        type      (get-in attrs-def [attr-name ::hs/type])
        parser    (or (get-in opts [:hipsterprise.core/parsers :hipsterprise.core/simple type])
                      identity)]
    [(->> attr-name
          (utils/make-kw opts))
     (parser val)]))

(defn parse-attrs [opts schema attrs-def element]
  (when attrs-def
    (->> (map (partial parse-attr opts schema attrs-def) (:attrs element))
         (apply concat)
         (apply hash-map))))
