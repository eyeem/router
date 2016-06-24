package com.eyeem.nanorouter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.eyeem.router.Plugin;
import com.eyeem.router.Router;
import com.eyeem.router.RouterLoader;
import com.eyeem.nanorouter.nano.NanoService;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

   CoordinatorLayout root;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      //com.eyeem.router.RouterConstants rc = new com.eyeem.router.RouterConstants();
      root = (CoordinatorLayout) findViewById(R.id.root);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            nanoService.start();
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//               .setAction("Action", test).show();
         }
      });

      doBindService();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      doUnbindService();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   public static View.OnClickListener test = new View.OnClickListener() {
      @Override public void onClick(View v) {

         String yamlStr = Assets._from(v.getContext(), "map.yaml");

         Map<String, Object> routerMap = (Map<String, Object>) new Yaml().load(yamlStr);

         Router r = RouterLoader
            .prepare()
            .plugin(new RequestPlugin())
            .plugin(new DecoratorsPlugin())
            .load(routerMap);

         String id = "me";

         Bundle bundle = r.outputFor("item/" + System.currentTimeMillis() + "/a/very/long/custom/path/1/2/3/4?color=234213");
      }
   };

   public static class DecoratorsPlugin extends Plugin<Bundle, Bundle> {
      public DecoratorsPlugin() { super("decorators"); }

      @Override public void outputFor(Router.RouteContext context, Object config, Bundle bundle) {
         Log.d(DecoratorsPlugin.class.getSimpleName(), config.toString());
      }
   }

   public static class RequestPlugin extends Plugin<Bundle, Bundle> {
      public RequestPlugin() { super("request"); }

      @Override public void outputFor(Router.RouteContext context, Object config, Bundle bundle) {
         Log.d(RequestPlugin.class.getSimpleName(), config.toString());
      }
   }

   private NanoService nanoService;
   private boolean isBound;

   private ServiceConnection nanoConnection = new ServiceConnection() {
      public void onServiceConnected(ComponentName className, IBinder service) {
         // This is called when the connection with the service has been
         // established, giving us the service object we can use to
         // interact with the service.  Because we have bound to a explicit
         // service that we know is running in our own process, we can
         // cast its IBinder to a concrete class and directly access it.
         nanoService = ((NanoService.NanoBinder)service).getService();

         // Tell the user about this for our demo.
         Snackbar.make(root, "Service connected", Snackbar.LENGTH_LONG);
      }

      public void onServiceDisconnected(ComponentName className) {
         // This is called when the connection with the service has been
         // unexpectedly disconnected -- that is, its process crashed.
         // Because it is running in our same process, we should never
         // see this happen.
         nanoService = null;
         Snackbar.make(root, "Service disconnected", Snackbar.LENGTH_LONG);
      }
   };

   void doBindService() {
      // Establish a connection with the service.  We use an explicit
      // class name because we want a specific service implementation that
      // we know will be running in our own process (and thus won't be
      // supporting component replacement by other applications).
      bindService(new Intent(this, NanoService.class), nanoConnection, Context.BIND_AUTO_CREATE);
      isBound = true;
   }

   void doUnbindService() {
      if (isBound) {
         // Detach our existing connection.
         unbindService(nanoConnection);
         isBound = false;
      }
   }
}
