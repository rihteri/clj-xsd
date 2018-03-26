(ns com.vincit.clj-xsd.metaschema.post.common
  (:require [com.vincit.clj-xsd.metaschema :as xs]
            [com.vincit.clj-xsd.schema :as hs]))

(defn fix-multi [{:keys [::xs/min-occurs ::xs/max-occurs] :as type}]
  (cond-> type
    (or min-occurs max-occurs) (assoc ::hs/multi [(or min-occurs 1)
                                                  (or max-occurs 1)])
    true                       (dissoc ::xs/min-occurs ::xs/max-occurs)))
