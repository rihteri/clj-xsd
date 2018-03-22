(ns hipsterprise.parser.default-parsers
  (:require [clojure.string :as str]
            [hipsterprise.xml :as hx]
            [clojure.data.xml :as xml]))

(defn parse-string [opts content]
  (->> content
       (filter string?)
       (str/join)
       (str/trim)))

(defn parse-integer [opts value]
  (-> value
      Integer/parseInt))

(defn parse-qname [opts value]
  (let [res    (str/split value #":")
        had-ns (= 2 (count res))
        name   (if had-ns (second res) (first res))
        namesp (if had-ns
                 (get-in opts [::xml/nss (first res)])
                 (get-in opts [::xml/nss :xmlns]))]
    [namesp name]))
