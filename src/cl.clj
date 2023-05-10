(ns cl
  (:require [classlojure.core :refer [with-classloader]]))

;; See Pomegranate docs on availability of DynamicClassLoader.
;; https://github.com/clj-commons/pomegranate/blob/master/doc/01-user-guide.adoc#modifying-the-classpath-and-jdk-9
;; https://github.com/lambdaisland/kaocha/blob/7fb8134ecc2f282300c797efe83cd9fd105eb8b4/src/kaocha/classpath.clj#L11-L24
(defn ensure-compiler-loader
  "Ensures the clojure.lang.Compiler/LOADER var is bound to a DynamicClassLoader,
  so that we can add to Clojure's classpath dynamically."
  []
  (when-not (bound? Compiler/LOADER)
    (.bindRoot Compiler/LOADER (clojure.lang.DynamicClassLoader. (clojure.lang.RT/baseLoader)))))

(defmacro with-compiler-loader [& body]
  `(with-classloader (deref clojure.lang.Compiler/LOADER) ~@body))
