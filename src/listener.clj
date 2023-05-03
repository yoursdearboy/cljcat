(ns listener
  (:gen-class
   :name org.cljcat.ServletContextListener
   :implements [javax.servlet.ServletContextListener])
  (:require [leiningen.core.project :as lein]))

(defn -contextInitialized [_ event]
  (let [ctx (.getServletContext event)
        path (.getRealPath ctx "project.clj")
        project (lein/read path)]
    (.setAttribute ctx "project" project)
    (when-let [handler (-> project :cljcat :init)]
      ((requiring-resolve handler) event))))

(defn -contextDestroyed [_ event]
  (let [ctx (.getServletContext event)
        project (.getAttribute ctx "project")]
    (when-let [handler (-> project :cljcat :destroy)]
      ((requiring-resolve handler) event))))
