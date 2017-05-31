(defproject dul-authority-tool "0.1.0-SNAPSHOT"
  :description "Tool for maintaining the Authority Registry in Crossref Distributed Logging (DUL) framework."
  :url "https://www.crossref.org"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.auth0/java-jwt "3.0.1"]
                 [com.nimbusds/nimbus-jose-jwt "4.34"]
                 [yogthos/config "0.8"]
                 [clj-time "0.12.2"]
                 [event-data-common "0.1.26"]
                 [com.amazonaws/aws-java-sdk "1.11.61"]]
  :main ^:skip-aot dul-authority-tool.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
