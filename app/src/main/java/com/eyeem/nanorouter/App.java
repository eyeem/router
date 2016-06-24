package com.eyeem.nanorouter;

import android.app.Application;

/**
 * Created by vishna on 23/06/16.
 */
public class App extends Application {

   public static App the;

   @Override public void onCreate() {
      the = this;
      super.onCreate();
   }
}
