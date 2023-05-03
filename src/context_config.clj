(ns context-config
  (:gen-class
   :name org.cljcat.ContextConfig
   :state state
   :init init
   :implements [org.apache.catalina.LifecycleListener])
  (:import [org.apache.catalina Lifecycle WebResourceRoot$ResourceSetType]
           [org.apache.catalina.webresources FileResourceSet StandardRoot])
  (:require [clojure.java.io :as io]
            [leiningen.core.project :as lein]))

(defn load-project [host ctx]
  (let [app-base (.getAppBaseFile host)
        doc-base (io/file (.getDocBase ctx))
        base (if (.isAbsolute doc-base) doc-base (io/file app-base doc-base))
        project-file (io/file base "project.clj")]
    (when (.exists project-file) (lein/read (.getAbsolutePath project-file)))))

(defn add-servlet [ctx]
  (-> ctx
      (.getServletContext)
      (.addServlet "cljcat" "org.cljcat.HttpServlet")
      (.addMapping (into-array String ["/*"]))))

(defn add-listener [ctx]
  (-> ctx
      (.addApplicationListener "org.cljcat.ServletContextListener")))

(defn ensure-resources [ctx]
  (if-let [resources (.getResources ctx)] resources
          (let [resources (new StandardRoot ctx)]
            (.setResources ctx resources)
            resources)))

(defn web-resource-set-type [kw]
  (.get (.getField WebResourceRoot$ResourceSetType (.toUpperCase (name kw))) nil))

(defn create-web-resource-set [resources type webAppMount base archivePath internalPath]
  (.createWebResourceSet resources (web-resource-set-type type) webAppMount base archivePath internalPath))

(defn add-myself-resource [resources config]
  (let [file (-> config .getClass .getProtectionDomain .getCodeSource .getLocation io/file)
        path (.getAbsolutePath file)
        mount (str "/WEB-INF/lib/" (.getName file))
        resource (new FileResourceSet resources mount path "/")]
    (.addPreResources resources resource)))

(defn add-project-resources [resources project]
  (dorun (for [path (:source-paths project)
               :let [f (io/file path)]
               :when (.exists f)]
           (create-web-resource-set resources :pre "/WEB-INF/classes" path nil "/")))
  (dorun (for [path (:resource-paths project)
               :let [f (io/file path)]
               :when (.exists f)]
           (create-web-resource-set resources :pre "/WEB-INF/classes" path nil "/"))))

(defn -init []
  [[] (atom {:project nil})])

;; FIXME: Read Leiningen project on Lifecycle/CONFIGURE_START_EVENT and remove state.
;; For some reason it doesn't work because of adding cljcat to resources.
(defn -lifecycleEvent [this event]
  (let [ctx (.getLifecycle event)
        host (.getParent ctx)
        type (.getType event)]
    (when (= type Lifecycle/BEFORE_START_EVENT)
      (when-let [project (load-project host ctx)]
        (swap! (.state this) assoc :project project)
        (add-myself-resource (ensure-resources ctx) this)))
    (when (= type Lifecycle/CONFIGURE_START_EVENT)
      (when-let [project (@(.state this) :project)]
        (add-project-resources (.getResources ctx) project)
        (add-servlet ctx)
        (add-listener ctx)))))
