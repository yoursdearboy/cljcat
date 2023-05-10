(ns listener
  (:gen-class
   :name org.cljcat.ServletContextListener
   :implements [javax.servlet.ServletContextListener])
  (:require [leiningen.core.project :as lein]
            [cl]))

(defn -contextInitialized [_ event]
  (cl/ensure-compiler-loader)
  (cl/with-compiler-loader
    (let [ctx (.getServletContext event)
          path (.getRealPath ctx "project.clj")
          project (lein/read path)]
      (lein/init-lein-classpath (assoc project :eval-in :leiningen))
      (.setAttribute ctx "project" project)
      (when-let [handler (-> project :cljcat :init)]
        ((requiring-resolve handler) event)))))

(defn -contextDestroyed [_ event]
  (let [ctx (.getServletContext event)
        project (.getAttribute ctx "project")]
    (cl/with-compiler-loader
      (when-let [handler (-> project :cljcat :destroy)]
        ((requiring-resolve handler) event)))))
