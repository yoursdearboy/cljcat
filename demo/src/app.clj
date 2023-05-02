(ns app)

(def context (atom nil))

(defn init [event]
  (reset! context event))

(defn handler [request]
  {:body (pr-str @context)})
