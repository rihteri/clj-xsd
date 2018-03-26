(ns com.vincit.clj-xsd.core
  (:require [clojure.data.xml :as xml]
            [com.vincit.clj-xsd.metaschema :as metaschema]
            [com.vincit.clj-xsd.metaschema.options :as metaschema-opts]
            [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser :as parser]
            [clojure.spec.alpha :as s]
            [com.vincit.clj-xsd.schema :as hs]))

(s/fdef parse
        :args (s/cat :opts (s/* (s/keys))
                     :schema ::hs/schema
                     :doc (constantly true)))

(defn parse
  "
  Parse an xml file.

  opts is a map that can contain

  [:com.vincit.clj-xsd.core/parsers :com.vincit.clj-xsd.core/simple]
    -> a map from type keyword (see ::namespaces) to a parser function
       [context str-content] -> any-type-you-wish, where context is this
       same opts map and some other stuff and content is whatever is returned
       by clojure.data.xml

  [:com.vincit.clj-xsd.core/parsers :com.vincit.clj-xsd.core/complex]
    -> a map from type keyword to a parser function [context el] -> new-result

  :com.vincit.clj-xsd.core/namespaces
    -> a map from xml namespace (string) to a clojure namespace to be used
       for keys of that type. If a namespace is missing, non-namespaced
       keywords are returned

  :com.vincit.clj-xsd.core/post
    -> postprocessing functions [parse-result] -> new-result
  "
  ([schema doc] (parse {} schema doc))
  ([opts schema doc]
   (let [element (xml/parse doc)]
     (parser/parse opts schema element))))

(s/fdef read-schema
        :ret ::hs/schema)

(defn read-schema
  "
  Read a schema description file (.xsd) and produce
  a schema representation for clj-xsd. The representation
  can then be given to parse.

  schema-document is a file stream opened with
  clojure.java.io/input-stream or something else that
  clojure.data.xml/parse accepts.
  "
  [schema-document]
  (->> schema-document
       (parse metaschema-opts/parse-opts
              metaschema/schemaschema)
       ::metaschema/schema))
