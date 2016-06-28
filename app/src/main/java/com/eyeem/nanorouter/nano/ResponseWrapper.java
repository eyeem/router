package com.eyeem.nanorouter.nano;

import android.webkit.MimeTypeMap;

import com.eyeem.nanorouter.App;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

import static com.eyeem.router.RouterUtils.cleanUrl;

/**
 * Created by vishna on 22/06/16.
 */
public class ResponseWrapper {

   public String message;
   public String asset;

   public NanoHTTPD.Response build() {
      if (asset != null) {
         return buildAsset();
      }
      return new NanoHTTPD.Response(message);
   }

   public NanoHTTPD.Response buildAsset() {
      try {
         InputStream is = App.the.getAssets().open(cleanUrl(asset));
         return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, getMimeType(asset), is);
      } catch (IOException e) {
         return null;
      }
   }

   public static String getMimeType(String url) {
      String type = null;
      String extension = MimeTypeMap.getFileExtensionFromUrl(url);
      if (extension != null) {
         type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
      }
      return type;
   }
}
