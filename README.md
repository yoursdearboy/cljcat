# Clojure Cat

## Install

In your Tomcat installation directory do the following.

1. Put downloaded jar to the `lib` directory.

2. Modify `conf/context.xml`.

```xml
<Context>
    ...

    <!-- If you want to reload app on `project.clj` changes. -->
    <WatchedResource>project.clj</WatchedResource>

    <Listener className="org.cljcat.ContextConfig"/>

    ...
</Context>
```

## Usage

Put an app (e.g. [demo](./demo) app in the repo) to the `webapps` directory.

The demo app contains [resources/config.edn](./demo/resources/config.edn) file.
It has `context` key which value must be changed, if the app deployed in folder different to `demo`.
The file can be used to store application configuration and added to git ignore list.

Another source of configuration is `project` attribute of servlet's context, which is a loaded Leiningen project.

```clj

(def project (atom nil))

(defn init [event]
 (reset! project
    (-> event
        (.getServletContext)
        (.getAttribute ctx "project"))))
```
