(ns com.vincit.clj-xsd.metaschema.post.simple-type
  (:require [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.utils :as utils]))

(defn fix [type]
  (let [item-type (get-in type [::xs/list ::xs/item-type])
        base-type (get-in type [::xs/restriction ::xs/base])]
    (-> type
        (utils/assoc-if ::hs/list-of item-type)
        (utils/assoc-if ::hs/base base-type)
        (utils/assoc-if ::hs/union-of
                        (when-let [union-types (get-in type [::xs/union ::xs/member-types])]
                          (apply hash-set union-types)))
        (dissoc ::xs/list ::xs/union ::xs/restriction))))
