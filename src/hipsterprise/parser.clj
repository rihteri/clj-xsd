(ns hipsterprise.parser
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.schema :as hs]
            [clojure.string :as str]))

(defn make-kw [opts {ns ::hx/ns elname ::hx/name}]
  (keyword (str (get-in opts [:hipsterprise.core/namespaces ns]))
           elname))

(defn parse-string [{:keys [content]}]
  (->> content
       (filter string?)
       (str/join)
       (str/trim)))

(defn element-is? [type {:keys [tag] :as todo}]
  (= (hx/extract-tag tag)
     type))

(defmulti parse-content (fn [kind opts schema content-def elements] kind))
(defmethod parse-content ::hs/sequence [kind opts schema content-def elements]
  (when (not (empty? elements))
    (let [elements         (filter (complement string?) elements)
          cur-el-def       (-> content-def first)
          type-to-parse    (::hs/element cur-el-def)
          upper-bound      (-> cur-el-def
                               ::hs/multi
                               second)
          is-type?         (partial element-is? type-to-parse)
          elements-of-type (take-while is-type? elements)
          do-parse-next    (partial parse-content
                                    kind
                                    opts
                                    schema
                                    (rest content-def)
                                    (drop-while is-type? elements))
          result           (map parse-string elements-of-type)
          result           (if (or (= :n upper-bound) (> 1 upper-bound))
                             result
                             (first result))
          kw               (make-kw opts type-to-parse)]
      (if (empty? elements-of-type)
        (do-parse-next)
        (into [kw result]
              (when (-> elements empty? not)
                (do-parse-next)))))))

(defn parse-element [opts schema el-def element]
  (let [[kind content-def] (get-in el-def [::hs/content])]
    (apply hash-map (parse-content kind opts schema content-def (:content element)))))

(defn parse [opts schema element]
  (let [curr-el (-> element :tag hx/extract-tag)
        el-type (get-in schema [::hs/elems curr-el ::hs/type])
        el-def  (get-in schema [::hs/types el-type])
        parser  (or (get-in opts [::parsers el-type])
                    (partial parse-element
                             opts
                             schema
                             el-def))
        content (parser element)]
    {(make-kw opts curr-el) content}))
