package com.eyeem.nanorouter.moustache.decorator;

import com.eyeem.nanorouter.App;
import com.eyeem.nanorouter.Assets;
import com.eyeem.nanorouter.moustache.MoustacheDecorator;
import com.eyeem.nanorouter.moustache.MoustachePlugin;
import com.eyeem.nanorouter.nano.ResponseWrapper;
import com.eyeem.router.AbstractRouter;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 24/06/16.
 */
public class TemplateInstigator extends MoustacheDecorator implements MoustacheDecorator.InstigateGetTemplateSource, MoustachePlugin.Config {

   String templateName;

   @Override public String getTemplateSource() {
      return Assets._from(App.the, "templates/" + templateName);
   }

   @Override
   public void configFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config) {
      if (config instanceof Map) {
         Map<String, Object> map = (Map<String, Object>) config;
         templateName = (String) map.get("name");
      }
   }
}
