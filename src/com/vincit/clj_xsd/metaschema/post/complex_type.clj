(ns com.vincit.clj-xsd.metaschema.post.complex-type
  (:require [clojure.set :as set]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]))

(defn unwrap-extension [type]
  (let [base (-> type
                 (get-in [::xs/complex-content ::xs/extension ::xs/base]))
        name (::xs/name type)]
    (if base
      (-> type
          ::xs/complex-content
          ::xs/extension
          (set/rename-keys {::xs/base     ::hs/base})
          (assoc ::xs/name name))
      type)))

(defn fix [type]
  (-> type
      unwrap-extension
                                        ; both sequence and choice should not be present
      (set/rename-keys {::xs/form     ::hs/form
                        ::xs/sequence ::hs/content
                        ::xs/choice   ::hs/content})))
