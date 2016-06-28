package com.eyeem.nanorouter.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vishna on 28/06/16.
 */
public class ServerEvent {
   public long id;
   public String title;
   public String subtitle;

   public ServerEvent(String title, String subtitle) {
      this.id = System.currentTimeMillis();
      this.title = title;
      this.subtitle = subtitle;
   }

   public String time() {
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
      Date resultdate = new Date(id);
      return sdf.format(resultdate);
   }
}
