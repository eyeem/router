package com.eyeem.nanorouter.plugins;

import com.eyeem.router.AbstractRouter;
import com.eyeem.nanorouter.nano.NanoRouter;
import com.eyeem.nanorouter.nano.ResponseWrapper;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 23/06/16.
 */
public class FilePlugin extends NanoRouter.Plugin {

   public static String ASSETS_PREFIX = "assets://";

   public FilePlugin() {
      super("file");
   }

   @Override public void outputFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, ResponseWrapper o) {
      Map<String, Object> map = ((Map<String, Object>)config);
      String path = map.get("path").toString();

      if (path.startsWith(ASSETS_PREFIX)) {
         o.asset = path.substring(ASSETS_PREFIX.length(), path.length());
      }
      // ..etc
   }
}
