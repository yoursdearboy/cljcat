(defproject cljcat-demo "0.1.0"
  :dependencies [[org.clojure/java.jdbc "0.7.12"]
                 [org.xerial/sqlite-jdbc "3.41.2.1"]
                 [com.layerware/hugsql "0.5.3"]
                 [compojure "1.7.0"]]
  :cljcat {:handler app/handler
           :init app/init})
