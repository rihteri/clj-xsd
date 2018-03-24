(ns com.vincit.clj-xsd.schema-parser
  "
  For transforming a parsed schema definition into an internal format
  that is easier to work with.
  "
  (:require [com.vincit.clj-xsd.metaschema :as xs]
            [clojure.set :as set]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.xml :as hx]
            [com.rpl.specter :as sc]))

(defn make-qname [ns name]
  [ns name])

(defn rename-xs [attr]
  (-> attr
      (set/rename-keys {::xs/type ::hs/type
                        ::xs/form ::hs/form})))

(defn group-types [tns types]
  (->> types
       (group-by ::xs/name)
       (sc/transform [sc/MAP-KEYS] (partial make-qname tns))
       (sc/transform [sc/MAP-VALS] (comp #(dissoc % ::xs/name)
                                         first))))

(defn fix-attrs [tns {attrs ::xs/attribute :as type}]
  (->> attrs
       (group-types tns)
       (sc/transform [sc/MAP-VALS] rename-xs)
       (assoc type ::hs/attrs)))

(defn fix-multi [{:keys [::xs/min-occurs ::xs/max-occurs] :as type}]
  (-> type
      (assoc ::hs/multi [(or min-occurs 1) (or max-occurs 1)])
      (dissoc ::xs/min-occurs ::xs/max-occurs)))

(defn fix-one-seq-el [tns {name ::xs/name :as el}]
  (-> el
      (set/rename-keys {::xs/type ::hs/type})
      (dissoc ::xs/name ::xs/element)
      (assoc ::hs/element [tns name])
      fix-multi))

(defn fix-seq-el [tns seq]
  (->> seq
       ::xs/element
       (map (partial fix-one-seq-el tns))))

(defmulti fix-sub-el-clause (fn [_ [type _]] type) )

(defmethod fix-sub-el-clause ::xs/sequence [tns [type seq]]
  (let [vals (fix-seq-el tns seq)]
    [::hs/sequence {::hs/vals vals}]))

(defmethod fix-sub-el-clause ::xs/choice [tns [type seq]]
  (let [vals (fix-seq-el tns seq)]
    [::hs/choice {::hs/elems (->> vals
                                  (group-by ::hs/element)
                                  (sc/transform [sc/MAP-VALS] (comp #(dissoc % ::hs/element)
                                                                    first)))}]))

(defn make-content-clause [tns type]
  (->> (select-keys type [::xs/choice ::xs/sequence])
       (map (partial fix-sub-el-clause tns))
       (filter some?)
       first))

(defn unwrap-extension [type]
  (let [base (-> type
                 (get-in [::xs/complex-content ::xs/extension ::xs/base]))]
    (if base
      (-> type
          ::xs/complex-content
          ::xs/extension
          (set/rename-keys {::xs/base ::hs/base}))
      type)))

(defn fix-content [tns type]
  (-> type
      (assoc ::hs/content (make-content-clause tns type))
      (dissoc ::xs/sequence ::xs/choice)))

(defn fix-type [{attrs ::hs/attrs :as type}]
  (cond-> type
    true           (dissoc ::xs/attribute)
    (empty? attrs) (dissoc ::hs/attrs)))

(defn fix-types [tns kind parsed]
  (->> parsed
       kind
       (group-types tns)
       (sc/transform [sc/MAP-VALS] (comp fix-type
                                         (partial fix-attrs tns)
                                         (partial fix-content tns)
                                         unwrap-extension))))

(defn fix-elems [{:keys [::xs/element] :as schema} tns]
  (-> schema
      (dissoc ::xs/element)
      (assoc ::hs/elems (->> (group-types tns element)
                             (sc/transform [sc/MAP-VALS] rename-xs)))))

(defn schema-to-internal
  "
  Turns a parsed schema document into a more
  consice internal representation"
  [{parsed ::xs/schema}]
  (let [tns               (::xs/target-namespace parsed)
        complex           (fix-types tns ::xs/complex-type parsed)
        simple            (fix-types tns ::xs/simple-type parsed)]
    (-> parsed
        (set/rename-keys {::xs/target-namespace       ::hs/tns
                          ::xs/element-form-default   ::hs/el-default
                          ::xs/attribute-form-default ::hs/attr-default})
        (fix-elems tns)
        (assoc ::hs/types (merge complex
                                 simple))
        (dissoc ::xs/complex-type ::xs/simple-type))))
