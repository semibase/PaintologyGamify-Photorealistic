package com.paintology.lite.photorealistic.drawing.Activity.search_activity;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutManagerWrapper extends GridLayoutManager {

  public GridLayoutManagerWrapper(Context context, int spanCount) {
    super(context, spanCount);
  }

  public GridLayoutManagerWrapper(Context context, int spanCount, int orientation, boolean reverseLayout) {
    super(context, spanCount, orientation, reverseLayout);
  }

  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    try {
      super.onLayoutChildren(recycler, state);
    } catch (IndexOutOfBoundsException e) {
      Log.e("TAG", "meet a IOOBE in RecyclerView");
    }
  }

  @Override
  public boolean supportsPredictiveItemAnimations() {
    return false;
  }
}