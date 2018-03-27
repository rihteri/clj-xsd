(ns com.vincit.clj-xsd.parser.simple
  (:require [clojure.set :as set]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [clojure.string :as str]))

(defn get-type-def [context type-name]
  (get-in context [::hs/schema ::hs/types type-name]))

(defn get-parser [context type-name]
  (utils/get-parser context type-name utils/simple-parsers-path))

(defmulti parse-simple
  (fn [context type-def content]
    (-> type-def
        (select-keys [::hs/list-of ::hs/restrict ::hs/union-of])
        first
        first)))

(defmethod parse-simple :default
  [_ _ content]
  nil)

(defmethod parse-simple ::hs/list-of
  [context type-def content]
  (let [list-type     (->> type-def
                           ::hs/list-of)
        list-def      (get-type-def context list-type)
        custom-parser (get-parser context list-type)]
    (->> (-> content
             utils/parse-string
             (str/split #"\s*"))
         (filter (complement empty?))
         (map (or (partial custom-parser context)
                  (partial parse-simple context list-def))))))

(defmethod parse-simple ::hs/restrict
  [context type-def content]
  (let [content   (utils/parse-string content)
        base-type (::hs/restrict type-def)
        parser    (get-parser context base-type)]
    (if parser
      (parser context content)
      (recur context (get-type-def context base-type) content))))

(defn get-custom-or-recursive-parser [context type-name]
  (or (partial (get-parser context type-name) context)
      (partial parse-simple context (get-type-def context type-name))))

(defmethod parse-simple ::hs/union-of
  [context type-def content]
  (let [content (utils/parse-string content)
        parsers (->> type-def
                     ::hs/union-of
                     (map (partial get-custom-or-recursive-parser context)))]
    (some #(% content) parsers)))
