(ns listener
  (:gen-class
   :name org.cljcat.ServletContextListener
   :implements [javax.servlet.ServletContextListener])
  (:require [utils :refer [deserialize]]))

(defn -contextInitialized [_ event]
  (let [project (-> event (.getServletContext) (.getAttribute "project") (deserialize))]
    (when-let [handler (-> project :cljcat :init)]
      ((requiring-resolve handler) event))))

(defn -contextDestroyed [_ event]
  (let [project (-> event (.getServletContext) (.getAttribute "project") (deserialize))]
    (when-let [handler (-> project :cljcat :destroy)]
      ((requiring-resolve handler) event))))
