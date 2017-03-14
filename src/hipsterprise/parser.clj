(ns hipsterprise.parser
  (:require [hipsterprise.xml :as hx]
            [hipsterprise.schema :as hs]))

(defn parse [opts schema element]
  (let [parse   (partial parse opts schema)
        curr-el (-> element :tag hx/extract-tag)
        el-sch  (get-in schema [::hs/types curr-el])
        content (map parse (-> element :content))]
       {:content content}))
