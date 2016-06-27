package com.eyeem.nanorouter.moustache;

import com.eyeem.router.AbstractRouter;
import com.eyeem.nanorouter.nano.NanoRouter;
import com.eyeem.nanorouter.nano.ResponseWrapper;
import com.eyeem.router.RouterLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.eyeem.router.RouterUtils.classForName;

/**
 * Created by vishna on 22/06/16.
 */
public class MoustachePlugin extends NanoRouter.P {

   public final static String DECORATORS_PACKAGE = "com.eyeem.nanorouter.moustache.decorator";

   public MoustachePlugin() {
      super("moustache");
   }

   @Override public void outputFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, ResponseWrapper out) {
      if (!(config instanceof ArrayList)) return;
      ArrayList array = (ArrayList) config;

      MoustacheEngine engine = new MoustacheEngine();
      MoustacheEngine.Builder engineBuilder = new MoustacheEngine.Builder();

      Map<Class, Object> configs = new HashMap<>();

      for (Object o : array) {
         // parametrized decorator
         if (o instanceof Map && RouterLoader.isTuple(o)) {
            Map.Entry<String, Object> tuple = RouterLoader.tuple(o);
            String className = tuple.getKey();
            if (tuple.getValue() instanceof Map || tuple.getValue() instanceof ArrayList) {
               Class decoratorClass = classForName(DECORATORS_PACKAGE, className);
               configs.put(decoratorClass, tuple.getValue());
               engineBuilder.addDecorator(decoratorClass);
            }
            continue;
         }
         // garbage
         else if (!(o instanceof String)) {
            continue;
         }
         // plain decorator
         else {
            engineBuilder.addDecorator(classForName(DECORATORS_PACKAGE, (String) o));
         }
      }

      engine.bind(engineBuilder);

      // run configs
      for (Map.Entry<Class, Object> decoratorConfig : configs.entrySet()) {
         MoustacheDecorator decorator = engine.getDecorators().getFirstDecoratorOfType(decoratorConfig.getKey());
         if (decorator instanceof Config) {
            ((Config)decorator).configFor(context, decoratorConfig.getValue());
         }
      }

      out.message = engine.generatePage();
   }

   public static class HtmlData {
      public String title;
      public String data;

      public HtmlData(String title, String data) {
         this.title = title;
         this.data = data;
      }
   }

   public interface Config {
      public void configFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config);
   }
}
