# Clojure Cat

## Install

In your Tomcat installation directory do the following.

1. Put downloaded jar to the `lib` directory.

2. Add `Listener` tag to the `conf/context.xml`.

```xml
<Context>
    ...
    <Listener className="org.cljcat.ContextConfig"/>
    ...
</Context>
```

## Usage

Put an app (e.g. [demo](./demo) app in the repo) to the `webapps` directory.
