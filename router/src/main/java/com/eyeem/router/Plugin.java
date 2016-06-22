package com.eyeem.router;

/**
 * Created by vishna on 22/06/16.
 */
public abstract class Plugin<O, P> {
   public final String node;
   public Plugin(String node) {
      this.node = node;
   }
   public abstract void outputFor(AbstractRouter<O, P>.RouteContext context, Object config, O o);
}