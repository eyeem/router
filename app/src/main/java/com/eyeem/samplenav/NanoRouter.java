package com.eyeem.samplenav;

import com.eyeem.router.AbstractPluggableBuilder;
import com.eyeem.router.AbstractRouter;
import com.eyeem.router.AbstractRouterLoader;
import com.eyeem.router.Plugin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 22/06/16.
 */
public class NanoRouter extends AbstractRouter<Response, NanoHTTPD.IHTTPSession> {

   public static Loader prepare() { return new Loader(); }

   public static class Loader extends AbstractRouterLoader<Response, NanoHTTPD.IHTTPSession> {

      @Override
      public AbstractPluggableBuilder<Response, NanoHTTPD.IHTTPSession> createPluggableBuilder(Serializable params, HashMap<String, Plugin<Response, NanoHTTPD.IHTTPSession>> plugins) {
         return new PluggableBuilder(params, plugins);
      }

      @Override public AbstractRouter<Response, NanoHTTPD.IHTTPSession> createRouter() {
         return new NanoRouter();
      }

      @Override
      public Loader plugin(Plugin<Response, NanoHTTPD.IHTTPSession> plugin) {
         return (Loader) super.plugin(plugin);
      }

      @Override
      public NanoRouter load(Map<String, Object> routerMap) {
         return (NanoRouter) super.load(routerMap);
      }
   }

   public static class PluggableBuilder extends AbstractPluggableBuilder<Response, NanoHTTPD.IHTTPSession> {

      PluggableBuilder(Serializable params, HashMap<String, Plugin<Response, NanoHTTPD.IHTTPSession>> plugins) {
         super(params, plugins);
      }

      @Override protected Response createOutputInstance() {
         return new Response();
      }
   }

   public static abstract class P extends Plugin<Response, NanoHTTPD.IHTTPSession> {
      public P(String node) {
         super(node);
      }
   }
}
