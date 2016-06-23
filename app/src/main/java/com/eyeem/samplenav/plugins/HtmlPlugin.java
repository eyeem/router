package com.eyeem.samplenav.plugins;

import com.eyeem.router.AbstractRouter;
import com.eyeem.samplenav.App;
import com.eyeem.samplenav.Assets;
import com.eyeem.samplenav.NanoRouter;
import com.eyeem.samplenav.Response;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 22/06/16.
 */
public class HtmlPlugin extends NanoRouter.P {
   public HtmlPlugin() {
      super("html");
   }

   @Override public void outputFor(AbstractRouter<Response, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, Response o) {
      final String url = context.url();
      final Map<String, Object> map;
      if (config instanceof Map) {
         map = (Map<String, Object>) config;
      } else {
         map = null;
      }

      String msg = null;
      try {
         String templateSource = Assets._from(App.the, "templates/" + map.get("template"));
         Handlebars handlebars = new Handlebars();
         Template template = handlebars.compileInline(templateSource);
         msg = template.apply(config);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }

      o.message = msg;
   }

   public static class HtmlData {
      public String title;
      public String data;

      public HtmlData(String title, String data) {
         this.title = title;
         this.data = data;
      }
   }
}
