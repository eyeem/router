package com.eyeem.nanorouter;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import com.eyeem.nanorouter.nano.NanoService;
import com.eyeem.nanorouter.ui.ServerEventStorage;

import static com.eyeem.nanorouter.ui.ServerEventStorage.log;

/**
 * Created by vishna on 23/06/16.
 */
public class App extends Application {

   public static App the;

   @Override public void onCreate() {
      the = this;
      ServerEventStorage.setUiHandler(new Handler());
      super.onCreate();
      log("App STARTED", "");
      startService(new Intent(this, NanoService.class));
   }
}
