package com.eyeem.samplenav;

import com.eyeem.samplenav.plugins.FilePlugin;
import com.eyeem.samplenav.plugins.HtmlPlugin;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 22/06/16.
 */
public class NanoServer extends NanoHTTPD {

   private NanoRouter router;

   public NanoServer(int port, Map<String, Object> routing) {
      super(port);
      router = NanoRouter.prepare()
         .plugin(new HtmlPlugin())
         .plugin(new FilePlugin())
         .load(routing);
   }

   @Override public Response serve(IHTTPSession session) {
      try {
         return router.outputFor(session.getUri(), session).build();
      } catch (Exception e) {
         return new Response(stackTraceToHtml(e));
      }
   }

   public String stackTraceToHtml(Throwable e) {
      StringBuilder sb = new StringBuilder();
      sb.append("<html><body>");
      for (StackTraceElement element : e.getStackTrace()) {
         sb.append(element.toString());
         sb.append("<br>");
      }
      sb.append("</html></body>");
      return sb.toString();
   }
}
