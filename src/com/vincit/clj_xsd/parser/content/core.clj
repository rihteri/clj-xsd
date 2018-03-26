(ns com.vincit.clj-xsd.parser.content.core
  (:require [com.vincit.clj-xsd.parser.custom.simple-types :as parsers]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]))

(defmulti parse-content (fn [context content-def accum] (first content-def)))

(defn do-parse-content [context el-type-def element]
  (let [content-def (get-in el-type-def [::hs/content])
        elements    (->> element
                         :content
                         (filter (complement string?)))]
    (when content-def
      (::result (parse-content context content-def {::elements elements})))))
