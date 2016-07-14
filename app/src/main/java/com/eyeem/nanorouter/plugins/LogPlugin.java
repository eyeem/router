package com.eyeem.nanorouter.plugins;

import com.eyeem.nanorouter.nano.NanoRouter;
import com.eyeem.nanorouter.nano.ResponseWrapper;
import com.eyeem.router.AbstractRouter;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.eyeem.nanorouter.ui.ServerEventStorage.log;

/**
 * Created by vishna on 28/06/16.
 */
public class LogPlugin extends NanoRouter.Plugin {
   public LogPlugin() {
      super("log");
   }

   @Override
   public void outputFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, ResponseWrapper o) {
      try {
         NanoHTTPD.IHTTPSession session = context.getExtras();
         StringBuilder sb = new StringBuilder();
         sb.append(session.getMethod().toString()).append(" ").append(session.getUri().toString()).append("\n");

         Map<String, String> headers = session.getHeaders();

         for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("\n").append(entry.getKey()).append(" = ").append(entry.getValue());
         }

         log("CONNECTION", sb.toString());
      } catch (Exception e) {}
   }
}
