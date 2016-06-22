package com.eyeem.router;

/*
    Routable for Android

    Copyright (c) 2013 Turboprop, Inc. <clay@usepropeller.com> http://usepropeller.com
    Copyright (c) 2016 EyeEm Mobile GmbH

    Licensed under the MIT License.

    Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

import android.net.Uri;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.eyeem.router.RouterUtils.cleanUrl;
import static com.eyeem.router.RouterUtils.isWildcard;

/**
 *
 * @param <O> the Output class
 * @param <P> the Params class
 */
public abstract class AbstractRouter<O,P> {

   /**
    * Default params shared across all router paths
    */
   private HashMap<String, Object> globalParams = new HashMap<>();

   /**
    * The class used when you want to map a function (given in `run`)
    * to a Router URL.
    */
   public static abstract class OutputBuilder<O, P> {
      public abstract O outputFor(AbstractRouter<O, P>.RouteContext context);
   }

   /**
    * The class supplied to custom callbacks to describe the route route
    */
   public class RouteContext {
      Map<String, String> _params;
      P _extras;
      String _url;

      public RouteContext(Map<String, String> params, P extras, String url) {
         _params = params;
         _extras = extras;
         _url = url;
      }

      /**
       * Returns the route parameters as specified by the configured route
       */
      public Map<String, String> getParams() {
         return _params;
      }

      /**
       * Returns the extras supplied with the route
       */
      public P getExtras() {
         return _extras;
      }

      /**
       * Returns the url that is being resolved by the router
       */
      public String url() {
         return _url;
      }
   }

   /**
    * The class used to determine behavior when opening a URL.
    * If you want to extend Routable to handle things like transition
    * animations or fragments, this class should be augmented.
    */
   public static class RouterOptions<O, P> {
      OutputBuilder<O, P> _outputBuilder;
      Map<String, String> _defaultParams;

      public RouterOptions() {}

      public RouterOptions(Map<String, String> defaultParams) {
         this.setDefaultParams(defaultParams);
      }

      public OutputBuilder<O, P> getOutputBuilder() {
         return this._outputBuilder;
      }

      public void setOutputBuilder(OutputBuilder<O, P> callback) {
         this._outputBuilder = callback;
      }

      public void setDefaultParams(Map<String, String> defaultParams) {
         this._defaultParams = defaultParams;
      }

      public Map<String, String> getDefaultParams() {
         return this._defaultParams;
      }
   }

   private static class RouterParams<O, P> {
      public RouterOptions<O, P> routerOptions;
      public Map<String, String> openParams;
   }

   private final Map<String, RouterOptions> _routes = new LinkedHashMap<>();
   private final Map<String, RouterOptions> _wildcardRoutes = new LinkedHashMap<>();
   private String _rootUrl = null;
   private final Map<String, RouterParams> _cachedRoutes = new HashMap<>();

   /**
    * Creates a new Router
    *
    */
   public AbstractRouter() {}

   /**
    * Map a URL to a callback
    *
    * @param format   The URL being mapped; for example, "users/:id" or "groups/:id/topics/:topic_id"
    * @param callback {@link OutputBuilder<O>} instance which contains the code to execute when the URL is opened
    */
   public void map(String format, OutputBuilder<O, P> callback) {
      RouterOptions options = new RouterOptions();
      options.setOutputBuilder(callback);
      this.map(format, options);
   }

   /**
    * Map a URL to {@link RouterOptions}
    *
    * @param format  The URL being mapped; for example, "users/:id" or "groups/:id/topics/:topic_id"
    * @param options The {@link RouterOptions} to be used for more granular and customized options for when the URL is opened
    */
   public void map(String format, RouterOptions options) {
      if (options == null) {
         options = new RouterOptions();
      }

      if (isWildcard(format)) {
         this._wildcardRoutes.put(format, options);
      } else {
         this._routes.put(format, options);
      }
   }

   /**
    * Set the root url; used when opening an activity or callback via RouterActivity
    *
    * @param rootUrl The URL format to use as the root
    */
   public void setRootUrl(String rootUrl) {
      this._rootUrl = rootUrl;
   }

   /**
    * @return The router's root URL, or null.
    */
   public String getRootUrl() {
      return this._rootUrl;
   }

   /**
    * Open a map'd URL set using {@link #map(String, Class)} or {@link #map(String, OutputBuilder)}
    *
    * @param url The URL; for example, "users/16" or "groups/5/topics/20"
    */
   public O outputFor(String url) {
      return this.outputFor(url, null);
   }

   /**
    * Open a map'd URL set using {@link #map(String, Class)} or {@link #map(String, BundleBuilder)}
    *
    * @param url     The URL; for example, "users/16" or "groups/5/topics/20"
    * @param extras  The {@link P} which contains the extra params to be assigned to the generated {@link O}
    */
   public O outputFor(String url, P extras) {
      RouterParams<O, P> params = this.paramsForUrl(url);
      RouterOptions<O, P> options = params.routerOptions;
      if (options.getOutputBuilder() != null) {

         Map openParams = (Map) AbstractRouterLoader.copy((Serializable) params.openParams);

         // add global params to path specific params
         for (Entry<String, Object> entry : globalParams.entrySet()) {
            if (!openParams.containsKey(entry.getKey())) { // do not override locally set keys
               openParams.put(entry.getKey(), entry.getValue());
            }
         }

         RouteContext routeContext = new RouteContext(openParams, extras, url);

         return options.getOutputBuilder().outputFor(routeContext);
      }

      return null;
   }

   /*
    * Takes a url (i.e. "/users/16/hello") and breaks it into a {@link RouterParams} instance where
    * each of the parameters (like ":id") has been parsed.
    */
   private RouterParams<O, P> paramsForUrl(String url) {
      final String cleanedUrl = cleanUrl(url);

      Uri parsedUri = Uri.parse("http://tempuri.org/" + cleanedUrl);

      String urlPath = parsedUri.getPath().substring(1);

      if (this._cachedRoutes.get(cleanedUrl) != null) {
         return this._cachedRoutes.get(cleanedUrl);
      }

      String[] givenParts = urlPath.split("/");

      // first check for matching non wildcard routes just to avoid being shadowed
      // by more generic wildcard routes
      RouterParams routerParams = checkRouteSet(this._routes.entrySet(), givenParts, false);

      // still null, try matching to any wildcard routes
      if (routerParams == null) {
         routerParams = checkRouteSet(this._wildcardRoutes.entrySet(), givenParts, true);
      }

      if (routerParams == null) {
         throw new RouteNotFoundException("No route found for url " + url);
      }

      for (String parameterName : parsedUri.getQueryParameterNames()) {
         String parameterValue = parsedUri.getQueryParameter(parameterName);
         routerParams.openParams.put(parameterName, parameterValue);
      }

      this._cachedRoutes.put(cleanedUrl, routerParams);
      return routerParams;
   }

   private static RouterParams checkRouteSet(Set<Entry<String, RouterOptions>> routeSet, String[] givenParts, boolean isWildcard) {
      RouterParams routerParams = null;

      for (Entry<String, RouterOptions> entry : routeSet) {
         String routerUrl = cleanUrl(entry.getKey());
         RouterOptions routerOptions = entry.getValue();
         String[] routerParts = routerUrl.split("/");

         if (!isWildcard && (routerParts.length != givenParts.length)) {
            continue;
         }

         Map<String, String> givenParams = urlToParamsMap(givenParts, routerParts, isWildcard);
         if (givenParams == null) {
            continue;
         }

         routerParams = new RouterParams();
         routerParams.openParams = givenParams;
         routerParams.routerOptions = routerOptions;
         break;
      }

      return routerParams;
   }

   /**
    * @param givenUrlSegments  An array representing the URL path attempting to be opened (i.e. ["users", "42"])
    * @param routerUrlSegments An array representing a possible URL match for the router (i.e. ["users", ":id"])
    * @param hasWildcard       Tells whether there is a :wildcard: param or not
    * @return A map of URL parameters if it's a match (i.e. {"id" => "42"}) or null if there is no match
    */
   private static Map<String, String> urlToParamsMap(String[] givenUrlSegments, String[] routerUrlSegments, boolean hasWildcard) {
      Map<String, String> formatParams = new HashMap<>();
      for (
         int routerIndex = 0, givenIndex = 0;
         routerIndex < routerUrlSegments.length && givenIndex < givenUrlSegments.length;
         routerIndex++
         ) {
         String routerPart = routerUrlSegments[routerIndex];
         String givenPart = givenUrlSegments[givenIndex];

         if (routerPart.length() > 0 && routerPart.charAt(0) == ':') {
            String key = routerPart.substring(1, routerPart.length());

            // (1) region standard router behavior
            if (!hasWildcard) {
               formatParams.put(key, givenPart);
               givenIndex++;
               continue;
            }
            // endregion

            // region wildcard

            // (2) first we check if param is indeed a wildcard param
            boolean isWildcard = false;
            if (key.charAt(key.length() - 1) == ':') {
               key = key.substring(0, key.length() - 1);
               isWildcard = true;
            }

            // (3) if it's not, just do standard processing --> (1)
            if (!isWildcard) {
               formatParams.put(key, givenPart);
               givenIndex++;
               continue;
            }

            // (4) check remaining segments before consuming wildcard parameter
            String nextRouterPart = routerIndex + 1 < routerUrlSegments.length ? routerUrlSegments[routerIndex + 1] : null;

            // we need to eat everything up till next recognizable path
            // e.g. :whatever:/:id should be forbidden thus the following check
            if (!TextUtils.isEmpty(nextRouterPart) && nextRouterPart.charAt(0) == ':') {
               throw new IllegalStateException(
                  String.format(Locale.US, "Wildcard parameter %1$s cannot be directly followed by a parameter %2$s", routerPart, nextRouterPart));
            }

            // (5) all is good, it's time to eat some segments
            ArrayList<String> segments = new ArrayList<>();
            for (int i = givenIndex; i < givenUrlSegments.length; i++) {
               String tmpPart = givenUrlSegments[i];
               if (tmpPart.equals(nextRouterPart)) {
                  break;
               } else {
                  segments.add(tmpPart);
               }
            }

            // (6) put it all assembled as a wildcard param
            formatParams.put(key, TextUtils.join("/", segments));
            givenIndex += segments.size();
            continue;
            // endregion
         }

         if (!routerPart.equals(givenPart)) {
            return null;
         }
         givenIndex++; // casual increment
      }

      return formatParams;
   }

   public AbstractRouter globalParam(String key, Object object) {
      globalParams.put(key, object);
      return this;
   }



   /**
    * Thrown if a given route is not found.
    */
   public static class RouteNotFoundException extends RuntimeException {
      private static final long serialVersionUID = -2278644339983544651L;

      public RouteNotFoundException(String message) {
         super(message);
      }
   }

   public void clearCache() {
      _cachedRoutes.clear();
   }
}