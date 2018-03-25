(ns com.vincit.clj-xsd.xml
  (:require [clojure.string :as str]
            [clojure.data.xml :as xml]))

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
  [form curr-ns tag]
  (when tag
    (let [actual-ns (or (extract-namespace tag)
                        (when (not= :com.vincit.clj-xsd.schema/qualified form)
                          curr-ns))]
      [actual-ns (name tag)])))

(defn extract-namespace-mappings [top-level-element]
  (-> top-level-element
      meta
      ::xml/nss))
