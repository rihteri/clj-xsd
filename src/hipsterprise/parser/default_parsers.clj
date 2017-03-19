(ns hipsterprise.parser.default-parsers
  (:require [clojure.string :as str]
            [hipsterprise.xml :as hx]))

(defn parse-string [content]
  (->> content
       (filter string?)
       (str/join)
       (str/trim)))

(defn parse-integer [value]
  (-> value
      Integer/parseInt))

(defn parse-ncname [value]
  ; TODO pick real ns instead of alias
  (let [[namesp name] (str/split value #":")]
    {::hx/name name
     ::hx/ns namesp}))
