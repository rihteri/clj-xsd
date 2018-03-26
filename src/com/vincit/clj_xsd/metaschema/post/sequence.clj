(ns com.vincit.clj-xsd.metaschema.post.sequence
  (:require [com.vincit.clj-xsd.metaschema.post.common :as sp-common]
            [com.rpl.specter :as sc]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.metaschema :as xs]
            [clojure.set :as set]))

(defmulti fix-val (fn [[kind val]] kind))

(defmethod fix-val :default [original] original)

(defmethod fix-val ::xs/element [[kind val]] val)
(defmethod fix-val ::xs/choice [[kind val]]  val)
(defmethod fix-val ::xs/sequence [[kind val]] val)

(defn fix [[attrs & content]]
  [::hs/sequence (-> attrs
                     sp-common/fix-multi
                     (assoc ::hs/vals (map fix-val content)))])
