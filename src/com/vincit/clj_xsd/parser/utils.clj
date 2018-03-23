(ns com.vincit.clj-xsd.parser.utils
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.default-parsers :as parsers]
            [com.vincit.clj-xsd.schema :as hs]
            [clojure.data.xml :as xml]
            [camel-snake-kebab.core :as csk]))

(def parsers-key :com.vincit.clj-xsd.core/parsers)
(def simple-parsers-path
  [parsers-key :com.vincit.clj-xsd.core/simple])
(def complex-parsers-path
  [parsers-key :com.vincit.clj-xsd.core/complex])

(defn get-parser [opts type path]
  (-> opts
      (get-in path)
      (get type)))

(defn make-kw [opts [ns elname]]
  (when elname
    (keyword (some-> (get-in opts [:com.vincit.clj-xsd.core/namespaces ns])
                     str)
             (csk/->kebab-case elname))))

(defn make-element-parser [simple-type-parser]
  (when simple-type-parser
    (fn [opts element]
      (->> element
           :content
           (parsers/parse-string opts)
           (simple-type-parser opts)))))

(defn element-is? [type {:keys [tag] :as todo}]
   (= (hx/extract-tag tag)
      type))

(defn is-plural? [element-def]
  (let [upper-bound (-> element-def
                        ::hs/multi
                        second)]
    (and upper-bound
         (or (= :n upper-bound) (> 1 upper-bound)))))

(defn update-ns [opts element]
  (let [curr-ns (-> element :tag hx/extract-namespace)]
    (if curr-ns
      (assoc opts :com.vincit.clj-xsd.parser/curr-ns curr-ns)
      opts)))
