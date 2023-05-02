(defproject cljcat "0.1.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [leiningen/leiningen "2.10.0"]
                 [org.apache.tomcat/tomcat-catalina "9.0.73"]
                 [ring/ring "1.9.6"]]
  :aot [context-config listener servlet]
  :aliases {"bundle" ["run" "-m" "bundle"]})
