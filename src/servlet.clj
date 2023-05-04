(ns servlet
  (:gen-class
   :name org.cljcat.HttpServlet
   :extends javax.servlet.http.HttpServlet)
  (:require [classlojure.core :refer [with-classloader]]
            [ring.util.servlet :refer [defservice]]))

(defservice
  (fn [request]
    (let [project (-> request :servlet-context (.getAttribute "project"))
          handler (-> project :cljcat :handler)]
      (with-classloader (deref clojure.lang.Compiler/LOADER)
        ((requiring-resolve handler) request)))))
