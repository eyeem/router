package com.eyeem.nanorouter.moustache.decorator;

import com.eyeem.nanorouter.App;
import com.eyeem.nanorouter.Assets;
import com.eyeem.nanorouter.moustache.MoustacheDecorator;
import com.eyeem.nanorouter.plugins.MoustachePlugin;
import com.eyeem.nanorouter.nano.ResponseWrapper;
import com.eyeem.router.AbstractRouter;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by vishna on 24/06/16.
 */
public class SectionContentDecorator extends MoustacheDecorator implements MoustachePlugin.Config {

   Map<String, Object> sectionsContext;

   @Override public void onGenerateContext(Map<String, Object> context) {
      String templateStr = Assets._from(App.the, "templates/section.html");
      String body = null;

      try {
         Handlebars handlebars = new Handlebars();
         Template template = handlebars.compileInline(templateStr);
         body = template.apply(sectionsContext);
      } catch (IOException e) {}

      context.put("body", body);
   }

   @Override
   public void configFor(AbstractRouter<ResponseWrapper, NanoHTTPD.IHTTPSession>.RouteContext context, Object config) {
      sectionsContext = new HashMap<>();

      // HACK for having the YAML file referenced inside section
      // better solution: some helper method exposed?
      sectionsContext.put("server_yaml", Assets._from(App.the, "server.yaml"));

      ArrayList<HashMap<String, Object>> inSections = (ArrayList<HashMap<String, Object>>) config;
      ArrayList<HashMap<String, Object>> outSections = new ArrayList<>();

      for (HashMap<String, Object> inSection : inSections) {
         HashMap<String, Object> outSection = new HashMap<>();
         outSection.putAll(inSection);
         outSection.put("id", inSection.get("title").toString().toLowerCase().replaceAll(" ", "_"));

         String contentTemplate = Assets._from(App.the, "content/"+ inSection.get("content"));
         String content = null;

         try {
            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(contentTemplate);
            content = template.apply(sectionsContext);
         } catch (IOException e) {}

         outSection.put("content", content);
         outSections.add(outSection);
      }

      sectionsContext.put("sections", outSections);
   }
}
