package com.eyeem.nanorouter.moustache.decorator;

import com.eyeem.nanorouter.App;
import com.eyeem.nanorouter.Assets;
import com.eyeem.nanorouter.moustache.MoustacheDecorator;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.util.Map;

/**
 * Created by vishna on 14/07/16.
 */
public class GiphyDecorator extends MoustacheDecorator {

   @Override public void onGenerateContext(Map<String, Object> context) {
      String templateStr = Assets._from(App.the, "templates/giphy.html");
      String body = null;

      try {
         Handlebars handlebars = new Handlebars();
         Template template = handlebars.compileInline(templateStr);
         body = template.apply(new Object());
      } catch (IOException e) {}

      context.put("body", body);
   }
}
