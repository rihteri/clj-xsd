(ns hipsterprise.core
  (:require [clojure.data.xml :as xml]
            [hipsterprise.metaschema :as metaschema]
            [hipsterprise.xml :as hx]
            [hipsterprise.schema :as hs]
            [hipsterprise.parser :as parser]))

(defn parse [opts schema doc]
  (let [element (xml/parse doc)]
    (parser/parse opts schema element)))

(defn read-schema [schema]
  (parse metaschema/parse-opts
         metaschema/schemaschema
         schema))
