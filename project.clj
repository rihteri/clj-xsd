(defproject com.vincit/clj-xsd "0.1.0-SNAPSHOT"
  :description "Easy XML handling for you favourite language!"
  :url ""
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.xml "0.2.0-alpha2"]
                 [camel-snake-kebab "0.4.0"]
                 [com.rpl/specter "1.1.0"]]
  :plugins [[lein-ancient "0.6.15"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
