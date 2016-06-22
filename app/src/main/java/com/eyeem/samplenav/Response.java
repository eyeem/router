package com.eyeem.samplenav;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 22/06/16.
 */
public class Response {

   public String message;

   public NanoHTTPD.Response build() {
      return new NanoHTTPD.Response(message);
   }
}
