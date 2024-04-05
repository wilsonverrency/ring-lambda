(defproject ring-lambda "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.2"]
                 [cheshire "5.13.0"]
                 [com.amazonaws/aws-lambda-java-runtime-interface-client "2.5.0"]
                 [metosin/reitit "0.6.0"]
                 [ring/ring-codec "1.2.0"]]
  :repl-options {:init-ns ring-lambda.core}
  :profiles {:dev {:dependencies [[ring/ring-mock "0.4.0"]]
                   :resource-paths ["test-resources"]}
             :uberjar {:aot :all}}
  :jar-name "ring-lambda.jar"
  :uberjar-name "ring-lambda-standalone.jar")
