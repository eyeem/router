package com.eyeem.nanorouter.ui;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by vishna on 28/06/16.
 */
public class ServerEventHolder extends RecyclerView.ViewHolder {

   ViewDataBinding viewDataBinding;

   public ServerEventHolder(ViewDataBinding viewDataBinding) {
      super(viewDataBinding.getRoot());
      this.viewDataBinding = viewDataBinding;
   }

   public ViewDataBinding getBinding() {
      return viewDataBinding;
   }
}
