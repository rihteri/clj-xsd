(ns com.vincit.clj-xsd.metaschema.post.choice
  (:require [com.vincit.clj-xsd.metaschema.post.common :as sp-common]
            [com.vincit.clj-xsd.schema :as hs]
            [clojure.set :as set]
            [com.rpl.specter :as sc]
            [com.vincit.clj-xsd.metaschema :as xs]))

(defn fix [el]
  [::hs/choice (->> (-> el
                        sp-common/fix-multi
                        (set/rename-keys {::xs/element ::hs/elems}))
                    (sc/transform [::hs/elems] (partial group-by ::xs/name))
                    (sc/transform [::hs/elems sc/MAP-VALS] (comp #(dissoc % ::xs/name)
                                                                 first)))])
