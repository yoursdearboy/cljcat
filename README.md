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
