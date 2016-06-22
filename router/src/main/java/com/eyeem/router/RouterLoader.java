package com.eyeem.router;

import android.os.Bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishna on 22/06/16.
 */
public class RouterLoader extends AbstractRouterLoader<Bundle, Bundle> {

   public static RouterLoader prepare() {
      return new RouterLoader();
   }

   @Override public AbstractPluggableBuilder<Bundle, Bundle> createPluggableBuilder(
      Serializable params,
      HashMap<String, Plugin<Bundle, Bundle>> plugins)
   {
      return new PluggableBuilder(params, plugins);
   }

   @Override public AbstractRouter<Bundle, Bundle> createRouter() {
      return new Router();
   }

   @Override public RouterLoader plugin(Plugin<Bundle, Bundle> plugin) {
      return (RouterLoader) super.plugin(plugin);
   }

   @Override public Router load(Map<String, Object> routerMap) {
      return (Router) super.load(routerMap);
   }

   public static class PluggableBuilder extends AbstractPluggableBuilder<Bundle, Bundle> {
      PluggableBuilder(Serializable params, HashMap<String, Plugin<Bundle, Bundle>> plugins) {
         super(params, plugins);
      }

      @Override protected Bundle createOutputInstance() {
         return new Bundle();
      }
   }
}
