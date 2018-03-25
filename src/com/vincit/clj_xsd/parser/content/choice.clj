(ns com.vincit.clj-xsd.parser.content.choice
  (:require [com.vincit.clj-xsd.schema :as hs]
            [com.vincit.clj-xsd.parser.content.core :as cp]
            [com.vincit.clj-xsd.parser.utils :as utils]
            [com.vincit.clj-xsd.xml :as hx]
            [com.vincit.clj-xsd.parser.context :as pcont]
            [com.vincit.clj-xsd.parser.element :as pe]))

(defmethod cp/parse-content ::hs/choice [opts content-def elements]
  (let [choice-def (second content-def)
        el         (first elements)
        is-mine?   (constantly true) #_ (partial utils/element-in?
                                                 opts
                                                 (->> choice-def
                                                      ::hs/elems
                                                      (map ::hs/element)))
        curr-ns    (::pcont/curr-ns opts)
        el-name    (hx/extract-tag (-> opts ::hs/schema ::hs/el-default) curr-ns (:tag el))
        el-def     (get-in choice-def [::hs/elems el-name])
        kw         (utils/make-kw opts el-name)
        parsed     (when (is-mine? el)
                     (pe/parse-element opts el-def el))
        me-plural? (utils/is-plural? choice-def)
        my-result  (when (is-mine? el)
                     {kw (if (or me-plural?
                                 (utils/is-plural? el-def))
                           [parsed]
                           parsed)})
        more-elems (when (and me-plural?
                              (not (empty? (take-while is-mine? (rest elements)))))
                     (cp/parse-content opts
                                       content-def
                                       (rest elements)))]
    (if (empty? more-elems)
      my-result
      (merge-with concat my-result more-elems))))
