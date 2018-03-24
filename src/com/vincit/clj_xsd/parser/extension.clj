(ns com.vincit.clj-xsd.parser.extension
  (:require [com.vincit.clj-xsd.schema :as hs]))

(defmulti merge-content
  "
  Merge xs:extension content from base to extension, dispatch
  on types of content in a tuple, e.g. [::sequence ::sequence],
  [::choice ::sequence] [::choice ::choice].

  TODO not sure how the standard want's us to treat exotic combinations
  "
  (fn [base-content content]
    (if base-content
      [(first base-content)
       (first content)]
      :none)))

; no base content
(defmethod merge-content :none [_ content] content)

(defmethod merge-content [::hs/sequence ::hs/sequence]
  [base-content content]
  [::hs/sequence (-> content
                     second
                     (update ::hs/vals (partial concat (-> base-content
                                                           second
                                                           ::hs/vals))))])

(defn merge-base [base type]
  (-> type
      (update ::hs/attrs (partial merge (::hs/attrs base)))
      (update ::hs/content (partial merge-content (::hs/content base)))))

(defn unwrap-type [schema {:keys [::hs/base] :as type}]
  (-> (some->> (get-in schema [::hs/types base])
              (unwrap-type schema))
      (merge-base type)))

