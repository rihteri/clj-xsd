(ns com.vincit.clj-xsd.metaschema.post.schema
  "
  For transforming a parsed schema definition into an internal format
  that is easier to work with.
  "
  (:require [clojure.set :as set]
            [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.xml :as hx]
            [com.rpl.specter :as sc]
            [com.vincit.clj-xsd.metaschema :as xs]))

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
  (cond-> type
    (some? attrs) (assoc ::hs/attrs (->> attrs
                                         (group-types tns)
                                         (sc/transform [sc/MAP-VALS] rename-xs)))

    true (dissoc ::xs/attribute)))

(def sub-group? sequential?)

(defmulti fix-content
  (fn [tns content-clause]
    (if (sub-group? content-clause)
      (first content-clause)
      :element)))

(declare fix-one-type)

(defmethod fix-content :element [tns def]
  (let [type-def (some->> def
                          ::hs/type-def
                          (fix-one-type tns))]
    (cond-> (-> def
                (assoc ::hs/element [tns (::xs/name def)])
                (dissoc ::xs/name))
      (some? type-def) (assoc ::hs/type-def type-def))))

(defmethod fix-content ::hs/sequence [tns [kind def]]
  [kind (->> def
             (sc/transform [::hs/vals sc/ALL] (partial fix-content tns)))])

(defmethod fix-content ::hs/choice [tns [kind def]]
  [kind (->> def
             (sc/transform [::hs/elems sc/MAP-KEYS] (fn [name] [tns name])))])

(defn fix-one-type [tns type]
  (->> type
       (sc/transform [::hs/content] (partial fix-content tns))
       (fix-attrs tns)))

(defn fix-types [tns kind parsed]
  (->> parsed
       kind
       (group-types tns)))

(defn fix-type-def
  "turn anonymous type definition to internal format"
  [tns type]
  (if (::hs/type-def type)
    (update type ::hs/type-def (partial fix-one-type tns))
    type))

(defn fix-elems [{:keys [::xs/element] :as schema} tns]
  (-> schema
      (dissoc ::xs/element)
      (assoc ::hs/elems (->> (group-types tns element)
                             (sc/transform [sc/MAP-VALS] (comp rename-xs
                                                               (partial fix-type-def tns)))))))

(defn schema-to-internal
  "
  Turns a parsed schema document into a more
  consice internal representation"
  [parsed]
  (let [tns       (::xs/target-namespace parsed)
        complex   (->> (fix-types tns ::xs/complex-type parsed)
                       (sc/transform [sc/MAP-VALS] (partial fix-one-type tns)))
        simple    (fix-types tns ::xs/simple-type parsed)
        all-types (merge complex simple)]
    (cond-> (-> parsed
                (set/rename-keys {::xs/target-namespace       ::hs/tns
                                  ::xs/element-form-default   ::hs/el-default
                                  ::xs/attribute-form-default ::hs/attr-default})
                (fix-elems tns)
                (dissoc ::xs/complex-type ::xs/simple-type))
      (not (empty? all-types)) (assoc ::hs/types all-types))))
