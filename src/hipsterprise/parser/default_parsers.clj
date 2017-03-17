(ns hipsterprise.parser.default-parsers
  (:require [clojure.string :as str]))

(defn parse-string [content]
  (->> content
       (filter string?)
       (str/join)
       (str/trim)))

(defn parse-integer [content]
  (-> content
      parse-string
      Integer/parseInt))
