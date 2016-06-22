package com.eyeem.samplenav.plugins;

import com.eyeem.router.AbstractRouter;
import com.eyeem.samplenav.NanoRouter;
import com.eyeem.samplenav.Response;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 22/06/16.
 */
public class HtmlPlugin extends NanoRouter.P {
   public HtmlPlugin(String node) {
      super(node);
   }

   @Override public void outputFor(AbstractRouter<Response, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, Response o) {
      String url = context.url();

      String msg = "<html>";

      Map<String, Object> map = null;
      if (config instanceof Map) {
         map = (Map<String, Object>) config;
      }

      if (map != null && map.containsKey("title")) {
         msg += "<head><title>" + map.get("title") + "</title></head>";
      }

      msg += "<body><h1>Hello server</h1>\n" +
         "<p>We serve " + url + " !</p></body></html>";

      o.message = msg;
   }
}
