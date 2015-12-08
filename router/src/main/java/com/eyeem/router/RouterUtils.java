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
}