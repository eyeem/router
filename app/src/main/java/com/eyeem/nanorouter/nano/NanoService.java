package com.eyeem.nanorouter.nano;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.eyeem.nanorouter.Assets;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * Created by vishna on 22/06/16.
 */
public class NanoService extends Service {

   public final static String TAG = NanoService.class.getSimpleName();

   NanoServer server;
   boolean isStarted;

   /**
    * Class for clients to access.  Because we know this service always
    * runs in the same process as its clients, we don't need to deal with
    * IPC.
    */
   public class NanoBinder extends Binder {
      public NanoService getService() {
         return NanoService.this;
      }
   }

   @Override
   public void onCreate() {
      String yamlStr = Assets._from(this, "server.yaml");
      Map<String, Object> routing = (Map<String, Object>) new Yaml().load(yamlStr);
      server = new NanoServer(8080, routing);
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      Log.i("TAG", "Received start id " + startId + ": " + intent);
      return START_NOT_STICKY;
   }

   public void start() {
      if (isStarted) {
         return;
      }

      try {
         server.start();
         isStarted = true;
      } catch (Throwable e) {
         Log.e(TAG, "failed to start server", e);
      }
   }

   public void stop() {
      if (!isStarted) {
         return;
      }

      try {
         server.stop();
         isStarted = false;
      } catch (Throwable e) {
         Log.e(TAG, "failed to start server", e);
      }
   }

   @Override
   public void onDestroy() {
      stop();
   }

   @Override
   public IBinder onBind(Intent intent) {
      return mBinder;
   }

   // This is the object that receives interactions from clients.  See
   // RemoteService for a more complete example.
   private final IBinder mBinder = new NanoBinder();
}
