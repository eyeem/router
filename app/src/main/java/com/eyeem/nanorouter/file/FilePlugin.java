package com.eyeem.nanorouter.file;

import com.eyeem.router.AbstractRouter;
import com.eyeem.nanorouter.nano.NanoRouter;
import com.eyeem.nanorouter.nano.Response;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 23/06/16.
 */
public class FilePlugin extends NanoRouter.P {

   public FilePlugin() {
      super("file");
   }

   @Override public void outputFor(AbstractRouter<Response, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, Response o) {
      Map<String, Object> map = ((Map<String, Object>)config);
      if (map.containsKey("root")) {
         String root = map.get("root").toString();
         o.file = root + context.url();
      } else {
         o.file = context.url();
      }
   }
}
