(ns listener
  (:gen-class
   :name org.cljcat.ServletContextListener
   :implements [javax.servlet.ServletContextListener])
  (:require [leiningen.core.project :as lein]
            [leiningen.core.classpath :as classpath]
            [cemerick.pomegranate :as pomegranate]))

;; See Pomegranate docs on availability of DynamicClassLoader.
;; https://github.com/clj-commons/pomegranate/blob/master/doc/01-user-guide.adoc#modifying-the-classpath-and-jdk-9
;; https://github.com/lambdaisland/kaocha/blob/7fb8134ecc2f282300c797efe83cd9fd105eb8b4/src/kaocha/classpath.clj#L11-L24
(defn ensure-compiler-loader
  "Ensures the clojure.lang.Compiler/LOADER var is bound to a DynamicClassLoader,
  so that we can add to Clojure's classpath dynamically."
  []
  (when-not (bound? Compiler/LOADER)
    (.bindRoot Compiler/LOADER (clojure.lang.DynamicClassLoader. (clojure.lang.RT/baseLoader)))))

;; Leiningen dependencies resolution, but with DynamiClassLoader instead of Context one. See:
;; https://github.com/technomancy/leiningen/blob/24fb93936133bd7fc30c393c127e9e69bb5f2392/leiningen-core/src/leiningen/core/project.clj#LL997C7-L997C11
(defn load-project-dependencies [project]
  (ensure-compiler-loader)
  (let [cl (deref clojure.lang.Compiler/LOADER)]
    (doseq [path (classpath/get-classpath project)]
      (pomegranate/add-classpath path cl))))

(defn -contextInitialized [_ event]
  (let [ctx (.getServletContext event)
        path (.getRealPath ctx "project.clj")
        project (lein/read path)]
    (load-project-dependencies project)
    (.setAttribute ctx "project" project)
    (when-let [handler (-> project :cljcat :init)]
      ((requiring-resolve handler) event))))

(defn -contextDestroyed [_ event]
  (let [ctx (.getServletContext event)
        project (.getAttribute ctx "project")]
    (when-let [handler (-> project :cljcat :destroy)]
      ((requiring-resolve handler) event))))
