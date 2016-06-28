package com.eyeem.nanorouter.ui;

import android.databinding.ObservableArrayList;
import android.os.Handler;

/**
 * Created by vishna on 28/06/16.
 */
public class ServerEventStorage {
   private static final ObservableArrayList<ServerEvent> sList = new ObservableArrayList<>();
   private static Handler uiHandler;

   public static void setUiHandler(Handler h) {
      uiHandler = h;
   }

   public static ObservableArrayList<ServerEvent> list () {
      return sList;
   }

   public static void push(final ServerEvent event) {
      if (uiHandler != null) {
         uiHandler.post(new Runnable() {
            @Override public void run() {
               sList.add(event);
            }
         });
      }
   }

   public static void log(String tag, String description) {
      push(new ServerEvent(tag, description));
   }
}
