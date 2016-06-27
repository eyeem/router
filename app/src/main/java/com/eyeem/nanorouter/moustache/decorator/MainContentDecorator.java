package com.eyeem.nanorouter.moustache.decorator;

import com.eyeem.nanorouter.App;
import com.eyeem.nanorouter.Assets;
import com.eyeem.nanorouter.moustache.MoustacheDecorator;

import java.util.Map;

/**
 * Created by vishna on 24/06/16.
 */
public class MainContentDecorator extends MoustacheDecorator {
   @Override public void onGenerateContext(Map<String, Object> context) {
      String body = Assets._from(App.the, "templates/main.html");
      context.put("body", body);
   }
}
