(ns com.vincit.clj-xsd.parser.content.core
  (:require [com.vincit.clj-xsd.parser.default-parsers :as parsers]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]))

(defmulti parse-content (fn [context content-def elements] (first content-def)))

(defn do-parse-content [context el-type-def element]
  (let [content-def (get-in el-type-def [::hs/content])
        elements    (->> element
                         :content
                         (filter (complement string?)))]
    (if content-def
      (parse-content context content-def elements)
      (parsers/parse-string context (:content element)))))
