# Router 2.0

This project is based off [routable-android](https://github.com/clayallsopp/routable-android) as it seemed a good starting point. It's definitely worth checking out.

*NOTE: At the time of the writing this document, the library is still in development, API is subject to changes, but already used in production.*

## Differences from 1.0 (routable-android)

- drop Activity/Intent dependency
- drop any launching Activity responsibility
- leverage Android’s Bundle as an output of the router
- leverage dynamic configuration (e.g. YAML file) over static Java code mappings
- delegate Bundle creation to configurable developer written set of plugins
- parametrized but logicless configuration
- router-validator as an optional tool to aid development

## Usage

### Gradle

```groovy
repositories {
    maven {
        url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
}

dependencies {
    compile 'com.eyeem.router:router:0.0.2-SNAPSHOT'
}
```

### Configuration file

It’s best to start with describing what paths your app will have and what kind of bundle they produce respectively. In this example we’ll use YAML to do so but you could use any other format as __router__ expects parsed java object and does not directly depend on any format.

```yaml
---
'home' :
  type: 'home'
  decorators :
    - TrackingDecorator
    - ViewPagerDecorator :
        tabNumber : '%{tabNumber}'
        pages :
          - 'discover'
          - 'feed/follow'
          - 'news'
          - 'user/me/photos'
'user/:id/photos' :
  request :
    path : '/v2/users/%{id}/photos'
    jsonpath : 'photos.items'
  type : 'userPhotos'
  decorators :
    - CoordinatorLayoutInstigator
    - RecyclerViewDecorator etc...
```

As you can see, there are 2 paths: `home` and `user/:id/photos`. Let’s skip the explanation of inner parts of the YAML file for now and create a router instance.

```java
Yaml yaml = new Yaml();  // using snake yaml parser for android
Map<String, Object> routerMap = (Map<String, Object>) yaml.load(Assets.loadAssetTextAsString(this, "navigation.yaml"));

Router router = RouterLoader.with(getContext()).load();
```

Given the path, you can obtain the bundle now, e.g.:

```java
Bundle bundle = router.bundleFor("user/16/photos");
```

What you do with this bundle now it’s up to you. You can pass it along with intent somewhere, set as an argument of the fragment ...pretty much everything. This is outside of scope of this document though.

### Router Plugins

Going back to configuration file, you can observe that router paths have 0 indentation level. Indentation level 1 have all the plugins. As router matches certain path, it will then delegate bundle creation to the plugins it has knowledge of. If certain plugin is missing, router will just do nothing. For now we’re generating an empty bundle, as we haven’t registered any plugins so let’s change that.

```java
Router router = RouterLoader
  .with(getContext())
  .plugin(new RequestPlugin())
  .load(routerMap);
```

A plugin implementation can look something like this:

```java
public class RequestPlugin extends RouterLoader.Plugin {

   public final static String KEY_REQUEST_BUILDER = "request_builder";

   // this is how router knows where to delegate bundle creation
   public RequestPlugin() { super("request"); }

   @Override public void bundleFor(
      Router.RouteContext context, // access local & global params like :id
      Object config, // plugin node data to handle
      Bundle bundle // output bundle that will be produced by Router
   )
   {
      Map map = (Map) config;
      String path = (String) map.get("path");

      RequestBuilder requestBuilder = EyeEm.path(path);

      if (map.containsKey("jsonpath")) {
         requestBuilder.jsonpath((String)map.get("jsonpath"));
      }

      // put request builder into output bundle
      bundle.putSerializable(KEY_REQUEST_BUILDER, requestBuilder);
   }
}
```

### Parametrization

#### Path params (a.k.a. local params)

Given path from our sample:

```java
Bundle bundle = router.bundleFor("user/16/photos?showNSFW=false")
```

Then having obtained router context

```java
Router.RouteContext routeContext = /*...*/ ;
context.getParams().get("id");       // 16
context.getParams().get("showNSFW"); // false
```

#### Global params

You can set up params that will be always available globally in the RouteContext, e.g.

```java
Router router = RouterLoader
  .with(getContext())
  .plugin(new RequestPlugin())
  .load(routerMap)
  .globalParam("isTablet", true)
  .globalParam("isPhone", false)
```

Then having obtained router context anywhere:

```java
Router.RouteContext routeContext = /*...*/ ;
context.getParams().get("isTablet"); // true
context.getParams().get("isPhone");  // false
```

#### Extra bundle

You can pass an extra bundle to the path, e.g.:

```java
Bundle extra = new Bundle();
extra.putSerializable("something", "extra");
Bundle bundle = router.bundleFor("user/16/photos?showNSFW=false", extra);
```

Then having obtained router context:

```java
Router.RouteContext routeContext = /*...*/ ;
routeContext.getExtras().getString("something"); // extra
```

#### Node parametrization

At the path resolving time, router will scan the map and replace any params with values, so, given following mapping:

```yaml
'user/:id/photos' :
  request :
    path : '/v2/users/%{id}/photos'
    jsonpath : 'photos.items'
```

By the time we reach the appropriate plugin, the value of the path will be already computed:

```java
Bundle bundle = router.bundleFor("user/16/photos");

// inside a plugin
void bundleFor(Router.RouteContext context, Object config, Bundle bundle) {
  Map map = (Map) config;
  String path = (String) map.get("path"); // /v2/users/16/photos
}
```

### Validation

_NOTE: This part is optional and supports only YAML format._

Moving all the router configuration outside of Java code created a peril of typos. For this very reason we’ve wrote a small validator in form of gradle plugin, that will generate java class containing statically typed paths, resources and similar. In case there is a typo, there will be an error yielded during compilation time.

Gradle setup
```groovy
buildscript {
    repositories {
        maven {
            url 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }

    dependencies {
        classpath 'com.eyeem.router:router-validator:0.0.2-SNAPSHOT'
    }
}
```

```groovy
apply plugin: 'com.eyeem.routervalidator'

router {
    path = "src/main/assets/navigation.yaml"
    packageName = "com.eyeem.router"
    decoratorsPackageName = "com.eyeem.decorator"
    holdersPackageName = "com.eyeem.holders"
    resourcePackageName = "com.baseapp.eyeem"
}
```

Java Usage:

```
Bundle bundle = router.bundleFor(RouterConstants.PATH_USER(id));
```

### Key Advantages

#### Single responsibility principle

Router paths describe and define navigation points of the app. Path nodes describe what these paths are made of.

#### High Level Organization + Easy Learning Curve

Single and simple configuration file allows newcomers and veterans quickly grasp understanding of relations between parts of the app.

#### Dynamic yet Strict

Freedom of writing custom plugins is moderated by parametrized & logicless approach to configuration parsing. If you feel like you need an ‘if’ clause somewhere you are probably designing something wrong.

#### Complex URL handling

A bonus you get for free. If your company has a website, you can now easily route people to the equivalent parts of the Android app. If your company is sending push notifications, you can now send a path, as a place to go and never code again any push notification logic in the app.

#### Painless migration

Let’s say you want to migrate from volley requests to retrofit. Since your requests are defined in a YAML file anyway, all you need is to change underlying router plugin & network layer implementation.

Actually nothing stops you from migrating straight to iOS since YAML contains no java code.

#### A/B Testing + Live Override

Once you have established your router configuration file, you can have many permutations of it and sideload them from app’s asset folder or over the air.
You can sideload parts of configuration, e.g. change mapping of a single path.

### Real life example

<table style="width:100%">
<tr>
<td>Before:</td>
<td>After:</td>
</tr>
<tr>
<td>
<pre lang="yaml">
'home' :
  type: 'home'
  decorators :
    - ViewPagerDecorator :
        tabNumber : '%{tabNumber}'
        pages :
          - 'discover'
          - 'feed/follow'
          - 'news'
          - 'user/me/photos'
</pre>
</td>
<td>
<pre lang="yaml">
'home' :
  type: 'home'
  decorators :
    - ViewPagerDecorator :
        tabNumber : '%{tabNumber}'
        pages :
          - 'discover'
          - 'feed/follow'
          - 'news'
          - 'user/me/photos'
          - 'missions' # the extra page
</pre>
</td>
</tr>
<tr>
<td><img src="https://cloud.githubusercontent.com/assets/121164/15412366/c528159e-1ddb-11e6-9890-6edf4e412523.png" width=320></td>
<td><img src="https://cloud.githubusercontent.com/assets/121164/15412356/b2c58490-1ddb-11e6-9858-3374d9404abd.png" width=320></td>
</tr>
</table>