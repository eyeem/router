package eyeem.com.router;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * This will load whole configuration from the YAML file
 *
 * Created by vishna on 23/11/15.
 */
public class RouterLoader {

   public final static String VARIABLE_PREFIX = "%{";
   public final static String VARIABLE_SUFFIX = "}";

   private Context context;
   private HashMap<String, Plugin> plugins = new HashMap<>();

   private RouterLoader() {}

   public static RouterLoader with(Context context) {
      RouterLoader routerLoader = new RouterLoader();
      routerLoader.context = context;
      return routerLoader;
   }

   public RouterLoader plugin(Plugin plugin) {
      plugins.put(plugin.node, plugin);
      return this;
   }

   public Router load(String yamlConfig) {
      Router r = new Router(context);

      Yaml yaml = new Yaml();
      Map<String, Object> routerMap = (Map<String, Object>) yaml.load(yamlConfig);

      for (Map.Entry<String, Object> entry : routerMap.entrySet()) {
         r.map(entry.getKey(), new PluggableBuilder((Serializable) entry.getValue(), plugins));
      }

      return r;
   }

   public static class PluggableBuilder extends Router.BundleBuilder {

      Map<String, Object> params;
      HashMap<String, Plugin> plugins;

      PluggableBuilder(Serializable params, HashMap<String, Plugin> plugins) {
         this.params = params instanceof Map ? (Map<String, Object>) params : null;
         this.plugins = plugins;
      }

      @Override public Bundle bundleFor(Router.RouteContext context) {
         Bundle b = new Bundle();
         HashMap<String, Object> params = (HashMap<String, Object>)copy((Serializable) this.params);

         for (Map.Entry<String, Object> param : params.entrySet()) {
            Plugin plugin = plugins.get(param.getKey());
            if (plugin == null) {
               Log.d(PluggableBuilder.class.getSimpleName(), "failed to find plugin for: " + param.getKey());
               continue;
            } else {
               format(param.getValue(), context);
               plugin.bundleFor(context, param.getValue(), b);
            }
         }
         return b;
      }
   }

   public static abstract class Plugin {
      public final String node;
      public Plugin(String node) {
         this.node = node;
      }
      public abstract void bundleFor(Router.RouteContext context, Object config, Bundle bundle);
   }

   static void format(Object param, Router.RouteContext context) {
      if (param instanceof ArrayList) {
         formatArray((ArrayList) param, context);
      }
      else if (param instanceof Map) {
         Map<String, Object> map = (Map) param;
         for (Map.Entry<String, Object> entry : map.entrySet()) {

            if (isArrayOrMap(entry.getValue())) {
               format(entry.getValue(), context);
            }
            else if (entry.getValue() instanceof String) {
               String newValue = format((String)entry.getValue(), context._params);
               entry.setValue(newValue);
            }
         }
      }
   }

   static void formatArray(ArrayList array, Router.RouteContext context) {
      for (int i = 0, n = array.size(); i < n; i++) {
         Object o = array.get(i);

         if (isArrayOrMap(o)) {
            format(o, context);
         }
         else if (o instanceof String) {
            o = format((String)o, context._params);
            array.set(i, o);
         }
      }
   }

   static boolean isTuple(Object object) {
      return object instanceof Map && ((Map) object).entrySet().size() == 1;
   }

   static boolean isArrayOrMap(Object object) {
      return object instanceof Map || object instanceof Array;
   }

   static Map.Entry<String, Object> tuple(Object object) {
      return (Map.Entry<String, Object>) ((Map) object).entrySet().iterator().next();
   }

   static String format(String format, Map<String, String> values) {
      if (TextUtils.isEmpty(format) || !format.contains(VARIABLE_PREFIX)) return format;
      StringBuilder convFormat = new StringBuilder(format);

      Iterator<String> keys = values.keySet().iterator();
      ArrayList<String> valueList = new ArrayList();

      int currentPos = 1;
      while (keys.hasNext()) {
         String key = keys.next(),
            formatKey = VARIABLE_PREFIX + key + VARIABLE_SUFFIX,
            formatPos = "%" + Integer.toString(currentPos) + "$s";
         int index = -1;
         boolean replaced = false;
         while ((index = convFormat.indexOf(formatKey, index)) != -1) {
            convFormat.replace(index, index + formatKey.length(), formatPos);
            index += formatPos.length();
            replaced = true;
         }
         if (replaced) {
            valueList.add(values.get(key));
            ++currentPos;
         }
      }
      return String.format(Locale.US, convFormat.toString(), valueList.toArray());
   }

   static Serializable copy(Serializable original) {
      Serializable copy = null;

      try {
         ByteArrayOutputStream cnfe = new ByteArrayOutputStream();
         ObjectOutputStream out = new ObjectOutputStream(cnfe);
         out.writeObject(original);
         out.flush();
         out.close();
         ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(cnfe.toByteArray()));
         copy = (Serializable)in.readObject();
      } catch (Exception e) {}

      return copy;
   }

   public static Boolean optBoolean(Map map, String key, Boolean defValue) {
      if (map.get(key) instanceof String) {
         try {
            String value = map.get(key).toString();
            if ("true".equals(value)) return Boolean.TRUE;
            if ("false".equals(value)) return Boolean.FALSE;
         } catch (Exception e) {
            return defValue;
         }
      }

      return map.get(key) instanceof Boolean ? (Boolean) map.get(key) : defValue;
   }

   public static int getResIDByName(Context context, String typeName) {
      String[] a = typeName.split("\\.");
      return getResIDByName(context, a[0], a[1]);
   }

   public static int getResIDByName(Context context, String type, String name) {
      String packageName = context.getPackageName();
      int resId = context.getResources().getIdentifier(name, type, packageName);
      return resId;
   }
}
