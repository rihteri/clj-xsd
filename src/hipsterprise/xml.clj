(ns hipsterprise.xml
  (:require [clojure.string :as str]))

(defn parse-namespace [encoded-ns]
  (-> encoded-ns
      (str/replace #"%3A" ":")
      (str/replace #"%2F" "/")))

(defn extract-tag [tag]
  (when tag
    {::name (name tag)
     ::ns   (-> (namespace tag)
                (str/replace #"^xmlns\." "")
                parse-namespace)}))
