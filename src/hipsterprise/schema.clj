(ns hipsterprise.schema
  "spec for the parsed output"
  (:require [hipsterprise.xml :as hx]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]))


(s/def ::namespace (s/nilable string?))
(s/def ::name string?)

(s/def ::qname
  (s/cat :namespace ::namespace :name ::name))

(s/def ::id string?)

(s/def ::type
  (s/or :qname ::qname
        :anon  keyword?))

(s/def ::element
  (s/keys))

(s/def ::type-def
  (s/keys))

(s/def ::elems
  (s/map-of ::qname ::element))

(s/def ::types
  (s/map-of ::type ::type-def))

(s/def ::attr-value-def string?)

(s/def ::default ::attr-value-def)
(s/def ::fixed ::attr-value-def)

(s/def ::form #{::qualified ::unqualified})

(s/def ::attr
  (s/keys :opt [::type ::default ::fixed ::form ::id]))

(s/def ::attrs
  (s/map-of ::qname ::attr))

(s/def ::schema
  (s/keys :req [::elems ::types]))
