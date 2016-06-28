package com.eyeem.nanorouter.plugins;

import com.eyeem.nanorouter.moustache.MoustacheDecorator;
import com.eyeem.nanorouter.moustache.MoustacheEngine;
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
      super("moustache"); // this is how Nano.PluggableBuilder knows which plugin to call
   }

   @Override public void outputFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config, ResponseWrapper out) {
      if (!(config instanceof ArrayList)) return;
      ArrayList array = (ArrayList) config;

      // we build the MoustacheEngine so that we can render page at later point
      MoustacheEngine engine = new MoustacheEngine();
      MoustacheEngine.Builder engineBuilder = new MoustacheEngine.Builder();

      Map<Class, Object> configs = new HashMap<>();

      // we iterate over config, add appropriate decorators to builder and collect
      // their internal configs so we can pass them later
      for (Object o : array) {
         // parametrized decorator
         if (o instanceof Map && RouterLoader.isTuple(o)) {
            Map.Entry<String, Object> tuple = RouterLoader.tuple(o);
            String className = tuple.getKey();
            if (tuple.getValue() instanceof Map || tuple.getValue() instanceof ArrayList) {
               Class decoratorClass = classForName(DECORATORS_PACKAGE, className);
               // we need to remember each decorator's config so we can pass it later
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

      // finish building the rendering engine with all the decorators
      engine.bind(engineBuilder);

      // now we just need to run through all the configs
      for (Map.Entry<Class, Object> decoratorConfig : configs.entrySet()) {
         MoustacheDecorator decorator = engine.getDecorators().getFirstDecoratorOfType(decoratorConfig.getKey());
         if (decorator instanceof Config) {
            ((Config)decorator).configFor(context, decoratorConfig.getValue());
         }
      }

      // send the message to server
      out.message = engine.generatePage();
   }

   /**
    * Interface for providing decorators with appropriate configs
    */
   public interface Config {
      public void configFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config);
   }
}
