package com.eyeem.nanorouter;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * Created by vishna on 17/02/15.
 */
public class Assets {

//   public static Observable<String> from(final Context context, final String filename) {
//      return Observable.defer(() -> Observable.just(_from(context, filename)));
//   }

   public static String _from(Context context, String filename) {

      BufferedReader in = null;

      try {
         StringBuilder buf = new StringBuilder();
         InputStream is = context.getAssets().open(filename);
         in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
         String str;

         while ((str = in.readLine()) != null) {
            buf.append(str).append("\n");
         }

         in.close();
         in = null;
         return buf.toString();
      } catch (Exception e) {
         return null;
      } finally {
         if (in != null) try {
            in.close();
         } catch (IOException e) {
         }
      }
   }

   public static HashMap loadHashMap(Context context, String filename) {
      Kryo kryo = new Kryo();

      try {
         InputStream is = context.getAssets().open(filename);
         Input input = new Input(is);
         HashMap data = kryo.readObject(input, LinkedHashMap.class);
         return data;
      } catch (Throwable t) {
         Log.e("loadHashMap", "loading map error", t);
      }

      return null;
   }
}

