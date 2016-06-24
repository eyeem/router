package com.eyeem.nanorouter.moustache;

import com.eyeem.decorator.annotation.Decorate;

import java.util.Map;

/**
 * Created by vishna on 24/06/16.
 */
@Decorate( // indicate the processor to do this whole class
   decorator = "MoustacheDecorator", // optional rename the classes
   decoratored = "MoustacheEngine",
   decorators = "MoustacheDecorators")
public class BlueprintMoustacheEngine extends BaseMoustacheEngine {

   @Override public String getTemplateSource() { return null; }

   @Override public void onGenerateContext(Map<String, Object> context) {}
}
