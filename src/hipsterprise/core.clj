(ns hipsterprise.core
  (:require [clojure.data.xml :as xml]
            [hipsterprise.metaschema :as metaschema]
            [hipsterprise.xml :as hx]
            [hipsterprise.schema-parser :as sp]
            [hipsterprise.parser :as parser]
            [clojure.spec.alpha :as s]
            [hipsterprise.schema :as hs]))

(s/fdef parse
        :args (s/cat :opts (s/keys)
                     :schema ::hs/schema
                     :doc (constantly true)))

(defn parse [opts schema doc]
  (let [element (xml/parse doc)]
    (parser/parse opts schema element)))

(s/fdef read-schema
        :ret ::hs/schema)

(defn read-schema [schema-document]
  (->> schema-document
       (parse metaschema/parse-opts
              metaschema/schemaschema)
       sp/schema-to-internal))
