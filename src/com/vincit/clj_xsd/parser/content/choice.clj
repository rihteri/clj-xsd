(ns com.vincit.clj-xsd.parser.content.choice
  (:require [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.content.core :as cp]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.context :as pcont]
            [com.vincit.clj-xsd.parser.element :as pe]))

(defn is-mine? [context choice-def element]
  (utils/element-in? context
                     (->> choice-def
                          ::hs/elems
                          keys)
                     element))

(defn parse-one [context el-def plural? el]
  (let [parsed (pe/parse-element context el-def el)]
    (if plural?
      [parsed]
      parsed)))

(defn split-curr-next [context choice-def elements]
  (let [max-occurs   (utils/get-max-occurs choice-def)
        is-mine?     (partial is-mine? context choice-def)
        [this other] (split-with is-mine? elements)]
    (if (and (not= max-occurs :n)
             (> (count this) max-occurs))
      (split-at max-occurs elements)
      [this other])))

(defn parse-one-kvp [context el-defs plural? el]
  (let [el-name (utils/extract-tag context el)
        kw      (utils/make-kw context el-name)
        el-def  (get el-defs el-name)]
    {kw (parse-one context el-def plural? el)}))

(defn parse-all [context choice-def accum]
  (let [[els-curr els-next] (split-curr-next context choice-def (::cp/elements accum))
        el-defs             (::hs/elems choice-def)
        plural?             (utils/is-plural? choice-def)
        prev-result         (::cp/result accum)]
    (-> accum
        (assoc ::cp/result (merge prev-result
                                  (->> els-curr
                                       (map (partial parse-one-kvp context el-defs plural?))
                                       (apply merge-with concat))))
        (assoc ::cp/elements els-next))))

(defmethod cp/parse-content ::hs/choice [context content-def accum]
  (parse-all context
             (second content-def)
             accum))
