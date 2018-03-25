(ns com.vincit.clj-xsd.parser.content.sequence
  (:require [com.vincit.clj-xsd.parser.content.core :as cp]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.parser.element :as pe]))

(defmethod cp/parse-content ::hs/sequence [context [kind content-def] elements]
  (when (and (not (empty? elements))
             (not (empty? (-> content-def ::hs/vals))))
    (let [cur-el-def       (-> content-def ::hs/vals first)
          element-to-parse (::hs/element cur-el-def)
          is-type?         (partial utils/element-is? context element-to-parse)
          elements-of-type (take-while is-type? elements)
          do-parse-next    (partial cp/parse-content
                                    context
                                    [kind (assoc content-def ::hs/vals (rest (::hs/vals content-def)))]
                                    (drop-while is-type? elements))
          result           (map (partial pe/parse-element
                                         context
                                         cur-el-def) elements-of-type)
          result           (if (utils/is-plural? cur-el-def)
                             result
                             (first result))
          kw               (utils/make-kw context element-to-parse)]
      (cond (sequential? cur-el-def) (cp/parse-content context
                                                       cur-el-def
                                                       elements)
            ;TODO continue with :rest
            (empty? elements-of-type) (do-parse-next)
            ; TODO remaining elements?
            :else                      (merge {kw result}
                                              (when (-> elements empty? not)
                                                      (do-parse-next)))))))
