(ns app
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [hugsql.core :as hugsql]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [context defroutes GET]]
            [hiccup.core :refer [html]]))

(hugsql/def-db-fns "query.sql")

(def config (edn/read-string (slurp (io/resource "config.edn"))))

(def db (config :database-url))

(defn init [event]
  (jdbc/execute! db "CREATE TABLE IF NOT EXISTS log (event VARCHAR);")
  (insert-event! db {:event (pr-str event)}))

(defn show-log [_]
  (let [log (select-events db)]
    (html [:ul
           (for [entry log]
             [:li [:pre (entry :event)]])])))

(defroutes routes
  (context (config :context) []
    (GET "/" [] show-log)))

(def handler (-> #'routes
                 (wrap-reload {:dirs (.getPath (io/resource "/"))})))
