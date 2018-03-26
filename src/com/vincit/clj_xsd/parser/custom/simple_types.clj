(ns com.vincit.clj-xsd.parser.custom.simple-types
  (:require [clojure.string :as str]
            [com.vincit.clj-xsd.xml :as hx]
            [clojure.data.xml :as xml]))

(defn parse-integer [opts value]
  (when (not (empty? value))
    (Integer/parseInt value)))

(defn parse-qname [opts value]
  (let [res    (str/split value #":")
        had-ns (= 2 (count res))
        name   (if had-ns (second res) (first res))
        namesp (if had-ns
                 (get-in opts [::xml/nss (first res)])
                 (get-in opts [::xml/nss :xmlns]))]
    [namesp name]))
