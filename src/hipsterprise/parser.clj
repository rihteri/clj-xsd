(ns hipsterprise.parser
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.schema :as hs]
            [clojure.string :as str]
            [hipsterprise.metaschema :as metaschema]))

(defn make-kw [opts {ns ::hx/ns elname ::hx/name}]
  (keyword (str (get-in opts [:hipsterprise.core/namespaces ns]))
           elname))

(defn parse-string [content]
  (->> content
       (filter string?)
       (str/join)
       (str/trim)))

(defn parse-integer [content]
  (-> content
      parse-string
      Integer/parseInt))

(defn element-is? [type {:keys [tag] :as todo}]
  (= (hx/extract-tag tag)
     type))

(declare parse-element)

(defmulti parse-content (fn [kind opts schema content-def elements] kind))
(defmethod parse-content ::hs/sequence [kind opts schema content-def elements]
  (when (not (empty? elements))
    (let [elements         (filter (complement string?) elements)
          cur-el-def       (-> content-def first)
          element-to-parse (::hs/element cur-el-def)
          type-to-parse (::hs/type cur-el-def)
          upper-bound      (-> cur-el-def
                               ::hs/multi
                               second)
          is-type?         (partial element-is? element-to-parse)
          elements-of-type (take-while is-type? elements)
          do-parse-next    (partial parse-content
                                    kind
                                    opts
                                    schema
                                    (rest content-def)
                                    (drop-while is-type? elements))
          result           (map (partial parse-element
                                         opts
                                         schema
                                         type-to-parse
                                         nil) elements-of-type)
          result           (if (or (= :n upper-bound) (> 1 upper-bound))
                             result
                             (first result))
          kw               (make-kw opts element-to-parse)]
      (if (empty? elements-of-type)
        (do-parse-next)
        (into [kw result]
              (when (-> elements empty? not)
                (do-parse-next)))))))

(defn make-element-parser [simple-type-parser]
  (when simple-type-parser
    (fn [element]
      (-> element :content simple-type-parser))))

(defn parse-element [opts schema el-type el-type-def element]
  (let [el-type-def        (or el-type-def (get-in schema [::hs/types el-type]))
        [kind content-def] (get-in el-type-def [::hs/content])
        custom-parser      (or (get-in opts [::parsers ::complex el-type])
                               (make-element-parser (get-in opts [::parsers ::simple el-type])))]
    (if custom-parser
      (custom-parser element)
      (if kind
        (apply hash-map (parse-content kind opts schema content-def (:content element)))
        (parse-string (:content element))))))

(def default-opts
  {::parsers {::simple {metaschema/integer parse-integer}}})

(defn parse [opts schema element]
  (let [opts    (merge default-opts opts)
        curr-el (-> element :tag hx/extract-tag)
        el-type (get-in schema [::hs/elems curr-el ::hs/type])
        content (parse-element opts
                               schema
                               el-type
                               nil
                               element)]
    {(make-kw opts curr-el) content}))
