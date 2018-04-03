(ns com.vincit.clj-xsd.metaschema.post.complex-type
  (:require [clojure.set :as set]
            [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]))

(defn unwrap-extension [type complexity-kind ext-kind]
  (let [base (-> type
                 (get-in [complexity-kind ext-kind ::xs/base]))
        name (::xs/name type)]
    (if base
      (-> type
          complexity-kind
          ext-kind
          (set/rename-keys {::xs/base     ::hs/base})
          (assoc ::xs/name name))
      type)))

(defn fix [type]
  (-> type
      (unwrap-extension ::xs/complex-content ::xs/extension)
      (unwrap-extension ::xs/complex-content ::xs/restriction)
      (unwrap-extension ::xs/simple-content ::xs/extension)
      (unwrap-extension ::xs/simple-content ::xs/restriction)
                                        ; both sequence and choice should not be present
      (set/rename-keys {::xs/form     ::hs/form
                        ::xs/sequence ::hs/content
                        ::xs/choice   ::hs/content})))
