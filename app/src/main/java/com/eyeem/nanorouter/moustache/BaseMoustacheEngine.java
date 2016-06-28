package com.eyeem.nanorouter.moustache;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishna on 24/06/16.
 */
public class BaseMoustacheEngine {

   Map<String, Object> context;

   public BaseMoustacheEngine() {
      context = new HashMap<>();
   }

   public String getTemplateSource() { return null; }
   public void onGenerateContext(Map<String, Object> context) {}

   public String generatePage() {
      onGenerateContext(context);
      try {
         Handlebars handlebars = new Handlebars();
         Template template = handlebars.compileInline(getTemplateSource());
         return template.apply(context);
      } catch (IOException e) {
         return null;
      }
   }
}
