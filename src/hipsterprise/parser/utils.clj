(ns hipsterprise.parser.utils
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.parser.default-parsers :as parsers]))

(defn make-kw [opts {ns ::hx/ns elname ::hx/name}]
  (keyword (str (get-in opts [:hipsterprise.core/namespaces ns]))
           elname))

(defn make-element-parser [simple-type-parser]
  (when simple-type-parser
    (fn [element]
      (-> element :content parsers/parse-string simple-type-parser))))

(defn element-is? [type {:keys [tag] :as todo}]
   (= (hx/extract-tag tag)
      type))