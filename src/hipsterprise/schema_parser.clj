(ns hipsterprise.schema-parser
  (:require [hipsterprise.metaschema :as xs]
            [clojure.set :as set]
            [hipsterprise.schema :as hs]
            [hipsterprise.xml :as hx]
            [com.rpl.specter :as sc]))

(defn make-qname [ns name]
  {::hx/name name
   ::hx/ns   ns})

(defn fix-attrs [tns {attrs ::xs/attribute :as type}]
  (->> (assoc type ::hs/attrs (group-by ::xs/name attrs))
       (sc/transform [::hs/attrs sc/MAP-KEYS] (partial make-qname tns))))

(defn fix-content [tns {seq    ::xs/sequence
                        choice ::xs/choice
                        :as    type}]
  (assoc type ::hs/content [::hs/sequence seq]))

(defn fix-type [{attrs ::hs/attrs :as type}]
  (cond-> type
    true           (dissoc ::xs/name ::xs/attribute ::xs/sequence)
    (empty? attrs) (dissoc ::hs/attrs)))

(defn group-types [tns kind parsed]
  (->> parsed
       kind
       (group-by ::xs/name)
       (sc/transform [sc/MAP-KEYS] (partial make-qname tns))
       (sc/transform [sc/MAP-VALS] (comp fix-type
                                         (partial fix-attrs tns)
                                         (partial fix-content tns)
                                         first))))

(defn schema-to-internal [{parsed ::xs/schema}]
  (let [tns     (::xs/target-namespace parsed)
        complex (group-types tns ::xs/complex-type parsed)
        simple  (group-types tns ::xs/simple-type parsed)]
    (-> parsed
        (set/rename-keys {::xs/target-namespace     ::hs/tns
                          ::xs/element              ::hs/elems
                          ::xs/element-form-default ::hs/el-default})
        (assoc ::hs/types (merge complex
                                 simple))
        (dissoc ::xs/complex-type ::xs/simple-type))))
