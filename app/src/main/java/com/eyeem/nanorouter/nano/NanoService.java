package com.eyeem.nanorouter.nano;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;

import com.eyeem.nanorouter.Assets;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;

import static com.eyeem.nanorouter.ui.ServerEventStorage.log;

/**
 * Created by vishna on 22/06/16.
 */
public class NanoService extends Service {

   public final static String TAG = NanoService.class.getSimpleName();

   NanoServer server;
   boolean isStarted;

   public boolean isStarted() { return isStarted; }

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
      // this is slow
//      String yamlStr = Assets._from(this, "server.yaml");
//      Map<String, Object> routing = (Map<String, Object>) new Yaml().load(yamlStr);
      // this is faster
      Map<String, Object> routing = Assets.loadHashMap(this, "server.yaml.kryo");
      server = new NanoServer(8080, routing);
      log("NanoService", "CREATED");
      listeners.add(new Listener() {
         @Override public void onStatusChanged(boolean isStarted) {
            log("Server", isStarted ? "STARTED" : "STOPPED");
         }
      });
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      log("NanoService", "Received start id " + startId + " : " + intent);
      return START_NOT_STICKY;
   }

   public void start() {
      if (isStarted) {
         return;
      }

      try {
         server.start();
         isStarted = true;
         onStatusChanged();
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
         onStatusChanged();
      } catch (Throwable e) {
         Log.e(TAG, "failed to start server", e);
      }
   }

   public String getServerAddressPretty() {
      try {
         return getLocalIpAddress() + ":" + server.getListeningPort();
      } catch (Exception e) {
         return null;
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

   // region listener
   public final HashSet<Listener> listeners = new HashSet<>();

   void onStatusChanged() {
      for (Listener listener : listeners) {
         listener.onStatusChanged(isStarted);
      }
   }

   public interface Listener {
      public void onStatusChanged(boolean isStarted);
   }
   // endregion

   private String getLocalIpAddress() throws Exception {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
         NetworkInterface intf = en.nextElement();
         for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
            InetAddress inetAddress = enumIpAddr.nextElement();
            if (!inetAddress.isLoopbackAddress()) {
               String ip = Formatter.formatIpAddress(inetAddress.hashCode());
               return ip;
            }
         }
      }
      return null;
   }
}
