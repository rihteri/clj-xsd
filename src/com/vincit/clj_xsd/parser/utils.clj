(ns com.vincit.clj-xsd.parser.utils
  (:require [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.schema :as hs]
            [clojure.data.xml :as xml]
            [camel-snake-kebab.core :as csk]
            [com.vincit.clj-xsd.parser.context :as pcont]
            [clojure.string :as str]))

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

(defn parse-string [content]
  (->> content
       (filter string?)
       (str/join)
       (str/trim)))

(defn make-element-parser [simple-type-parser]
  (when simple-type-parser
    (fn [opts element]
      (->> element
           :content
           parse-string
           (simple-type-parser opts)))))

(defn element-is? [{{:keys [::hs/el-default]} ::hs/schema
                    curr-ns                   ::pcont/curr-ns}
                   el-name
                   {:keys [tag]}]
   (= (hx/extract-tag el-default curr-ns tag)
      el-name))

(defn element-in? [opts el-names el]
  (->> el-names
       (some #(element-is? opts % el))
       some?))

(defn is-plural? [element-def]
  (let [upper-bound (-> element-def
                        ::hs/multi
                        second)]
    (and upper-bound
         (or (= :n upper-bound) (> upper-bound 1)))))

(defn get-max-occurs [el-def]
  (or (->> el-def
           ::hs/multi
           last)
      1))

(defn extract-tag [context el]
  (let [curr-ns    (::pcont/curr-ns context)
        el-default (-> context ::hs/schema ::hs/el-default)]
    (->> el
         :tag
         (hx/extract-tag el-default curr-ns))))

