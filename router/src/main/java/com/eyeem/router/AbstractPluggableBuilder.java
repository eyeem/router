package com.eyeem.router;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishna on 22/06/16.
 */
public abstract class AbstractPluggableBuilder<O, P> extends AbstractRouter.OutputBuilder<O, P> {

   private final static String TAG = AbstractPluggableBuilder.class.getSimpleName();

   Map<String, Object> params;
   HashMap<String, Plugin<O, P>> plugins;

   public AbstractPluggableBuilder(Serializable params, HashMap<String, Plugin<O, P>> plugins) {
      this.params = params instanceof Map ? (Map<String, Object>) params : null;
      this.plugins = plugins;
   }

   protected abstract O createOutputInstance();

   @Override public O outputFor(AbstractRouter<O, P>.RouteContext context) {
      O o = createOutputInstance();
      HashMap<String, Object> params = (HashMap<String, Object>)AbstractRouterLoader.copy((Serializable) this.params);

      for (Map.Entry<String, Object> param : params.entrySet()) {
         Plugin<O, P> plugin = plugins.get(param.getKey());
         if (plugin == null) {
            Log.v(TAG, "failed to find plugin for: " + param.getKey());
            continue;
         } else {
            Object paramValue = param.getValue();
            if (paramValue instanceof String) { // because strings are immutable
               String formatedParamValue = AbstractRouterLoader.format((String)paramValue, context._params);
               plugin.outputFor(context, formatedParamValue, o);
            } else {
               AbstractRouterLoader.format(param.getValue(), context);
               plugin.outputFor(context, param.getValue(), o);
            }
         }
      }
      return o;
   }

}
