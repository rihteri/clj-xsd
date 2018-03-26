(ns com.vincit.clj-xsd.metaschema.post.element
  (:require [com.vincit.clj-xsd.metaschema.post.common :as sp-common]
            [clojure.set :as set]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema :as xs]))

(defn fix [el]
  (-> el
      sp-common/fix-multi
      (set/rename-keys {::xs/type         ::hs/type
                        ::xs/complex-type ::hs/type-def
                        ::xs/simple-type  ::hs/type-def})))


