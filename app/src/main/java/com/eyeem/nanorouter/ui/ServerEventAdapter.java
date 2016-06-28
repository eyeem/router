package com.eyeem.nanorouter.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eyeem.nanorouter.BR;
import com.eyeem.nanorouter.R;

import static com.eyeem.nanorouter.ui.ServerEventStorage.list;

/**
 * Created by vishna on 28/06/16.
 */
public class ServerEventAdapter extends RecyclerView.Adapter<ServerEventHolder> {

   public ServerEventAdapter() {
      setHasStableIds(true);
   }

   @Override public long getItemId(int position) {
      return list().get(position).id;
   }

   @Override public ServerEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.server_event_row, parent, false);
      return new ServerEventHolder(viewDataBinding);
   }

   @Override public void onBindViewHolder(ServerEventHolder holder, int position) {
      final ServerEvent event = list().get(position);
      holder.getBinding().setVariable(BR.event, event);
      holder.getBinding().executePendingBindings();
   }

   @Override public int getItemCount() {
      return list().size();
   }

   @Override public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
      super.registerAdapterDataObserver(observer);
      list().addOnListChangedCallback(listCallback);
   }

   @Override public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
      list().removeOnListChangedCallback(listCallback);
      super.unregisterAdapterDataObserver(observer);
   }

   ObservableList.OnListChangedCallback<ObservableArrayList<ServerEvent>> listCallback = new ObservableList.OnListChangedCallback<ObservableArrayList<ServerEvent>>() {
      @Override public void onChanged(ObservableArrayList<ServerEvent> sender) {
         notifyDataSetChanged();
      }

      @Override
      public void onItemRangeChanged(ObservableArrayList<ServerEvent> sender, int positionStart, int itemCount) {
         notifyItemRangeChanged(positionStart, itemCount);
      }

      @Override
      public void onItemRangeInserted(ObservableArrayList<ServerEvent> sender, int positionStart, int itemCount) {
         notifyItemRangeInserted(positionStart, itemCount);
      }

      @Override
      public void onItemRangeMoved(ObservableArrayList<ServerEvent> sender, int fromPosition, int toPosition, int itemCount) {
         notifyDataSetChanged(); // FIXME
      }

      @Override
      public void onItemRangeRemoved(ObservableArrayList<ServerEvent> sender, int positionStart, int itemCount) {
         notifyItemRangeRemoved(positionStart, itemCount);
      }
   };
}
