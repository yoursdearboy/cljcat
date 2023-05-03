(ns servlet
  (:gen-class
   :name org.cljcat.HttpServlet
   :extends javax.servlet.http.HttpServlet)
  (:require [ring.util.servlet :refer [defservice]]))

(defservice
  (fn [request]
    (let [project (-> request :servlet-context (.getAttribute "project"))
          handler (-> project :cljcat :handler)]
      ((requiring-resolve handler) request))))
