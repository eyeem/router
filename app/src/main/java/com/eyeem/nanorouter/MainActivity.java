package com.eyeem.nanorouter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.eyeem.nanorouter.ui.ServerEventAdapter;
import com.eyeem.nanorouter.nano.NanoService;

import static com.eyeem.nanorouter.ui.ServerEventStorage.list;
import static com.eyeem.nanorouter.ui.ServerEventStorage.log;

public class MainActivity extends AppCompatActivity implements NanoService.Listener {

   CoordinatorLayout root;
   FloatingActionButton fab;
   RecyclerView recycler;
   ServerEventAdapter adapter;

   RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
         LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler.getLayoutManager();
         int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
         int lastPosition = list().size() - 1;
         if (lastPosition - lastVisiblePosition > 2) {
            return;
         }
         recycler.scrollToPosition(lastPosition);
      }
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setSubtitle("something something");

      //com.eyeem.router.RouterConstants rc = new com.eyeem.router.RouterConstants();
      root = (CoordinatorLayout) findViewById(R.id.root);
      recycler = (RecyclerView) findViewById(R.id.recycler);
      recycler.setLayoutManager(new LinearLayoutManager(this));
      recycler.setAdapter(adapter = new ServerEventAdapter());
      adapter.registerAdapterDataObserver(observer);

      fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (nanoService.isStarted()) {
               nanoService.stop();
            } else {
               nanoService.start();
            }
         }
      });

      doBindService();

      if (savedInstanceState == null) {
         log("MainActivity OPENED", "Press `PLAY` to launch server.");
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      adapter.unregisterAdapterDataObserver(observer);
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

   @Override protected void onResume() {
      super.onResume();
      if (nanoService != null) {
         onStatusChanged(nanoService.isStarted());
      }
   }

   @Override public void onStatusChanged(boolean isStarted) {
      if (fab != null) {
         fab.setImageResource(isStarted ? R.drawable.ic_pause_32dp : R.drawable.ic_play_arrow_32dp);
      }

      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setSubtitle(isStarted ? nanoService.getServerAddressPretty() : null);
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
         if (nanoService != null) {
            nanoService.listeners.add(MainActivity.this);
            onStatusChanged(nanoService.isStarted());
         }
      }

      public void onServiceDisconnected(ComponentName className) {
         // This is called when the connection with the service has been
         // unexpectedly disconnected -- that is, its process crashed.
         // Because it is running in our same process, we should never
         // see this happen.
         if (nanoService != null) {
            nanoService.listeners.remove(MainActivity.this);
            nanoService = null;
         }

         ActionBar actionBar = getSupportActionBar();
         if (actionBar != null) {
            actionBar.setSubtitle(null);
         }
      }
   };

   void doBindService() {
      // Establish a connection with the service.  We use an explicit
      // class name because we want a specific service implementation that
      // we know will be running in our own process (and thus won't be
      // supporting component replacement by other applications).
      if (isBound) return;
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
