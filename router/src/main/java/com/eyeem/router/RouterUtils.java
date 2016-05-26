package com.eyeem.router;

import android.text.TextUtils;

public class RouterUtils {

   public static String classify(String packageName, String className) {
      if (className.startsWith(".")) {
         return packageName + className;
      } else if (className.contains(".")) {
         return className;
      } else {
         return packageName + "." + className;
      }
   }

   public static Class classForName(String packageName, String name) {
      if (TextUtils.isEmpty(name)) return null;

      try {
         return Class.forName(classify(packageName, name));
      } catch (ClassNotFoundException e) {
         return null;
      }
   }

   /**
    * Check if given router url format is a :wildcard: format
    * @param format
    * @return true if format is a wildcard containing format
    */
   public static boolean isWildcard(String format) {
      String routerUrl = cleanUrl(format);
      String[] routerParts = routerUrl.split("/");

      for (String routerPart : routerParts) {
         if (routerPart.length() > 2
            && routerPart.charAt(0) == ':'
            && routerPart.charAt(routerPart.length() - 1) == ':') {
            return true;
         }
      }
      return false;
   }

   /**
    * Clean up url
    *
    * @param url
    * @return cleaned url
    */
   public static String cleanUrl(String url) {
      if (url.startsWith("/")) {
         return url.substring(1, url.length());
      }
      return url;
   }
}