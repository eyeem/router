package com.eyeem.nanorouter.moustache.decorator;

import com.eyeem.nanorouter.App;
import com.eyeem.nanorouter.R;
import com.eyeem.nanorouter.moustache.MoustacheDecorator;

import java.util.Map;

/**
 * Created by vishna on 27/06/16.
 */
public class VersionDecorator extends MoustacheDecorator {
   @Override public void onGenerateContext(Map<String, Object> context) {
      try {
         String versionName = App.the.getPackageManager().getPackageInfo(App.the.getPackageName(), 0).versionName;
         context.put("versionName", versionName);
      } catch (Throwable t) {}
      context.put("appName", App.the.getString(R.string.app_name));
   }
}
