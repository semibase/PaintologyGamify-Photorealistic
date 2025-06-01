package com.paintology.lite.photorealistic.drawing.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration




class SpecingDecoration(val verticalSpaceHight: Int,space: Int) : ItemDecoration() {
    var space=space
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = verticalSpaceHight
        val position = parent.getChildAdapterPosition(view!!)
        val isLast = position == state.itemCount - 1
        if (isLast) {
            outRect.bottom = space
            outRect.top = 0 //don't forget about recycling...
        }
        if (position == 0) {
            outRect.top = 0
            // don't recycle bottom if first item is also last
            // should keep bottom padding set above
            if (!isLast) outRect.bottom = 0
        }
    }


}


class SpecingDecorationHorizontal(val verticalSpaceHight: Int,space: Int) : ItemDecoration() {
    var space=space
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = verticalSpaceHight
        val position = parent.getChildAdapterPosition(view!!)
        val isLast = position == state.itemCount - 1
        val isLast2 = position == state.itemCount - 2
        if (isLast) {
            outRect.bottom = space
            outRect.top = 0 //don't forget about recycling...
        }
        if (isLast2) {
            outRect.bottom = space
            outRect.top = 0
        }
    }


}