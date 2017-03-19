(ns hipsterprise.xml
  (:require [clojure.string :as str]))

(defn parse-namespace [encoded-ns]
  (-> encoded-ns
      (str/replace #"%3A" ":")
      (str/replace #"%2F" "/")))

(defn extract-namespace [tag]
  (let [namespace-encoded (namespace tag)]
    (when namespace-encoded
      (-> namespace-encoded
          (str/replace #"^xmlns\." "")
          parse-namespace))))

(defn extract-tag
  ([tag]
   (extract-tag nil tag))
  ([curr-ns tag]
   (when tag
     (let [actual-ns (or (extract-namespace tag) curr-ns)]
       {::name (name tag)
        ::ns   actual-ns}))))
