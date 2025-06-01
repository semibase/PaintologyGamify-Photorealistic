package com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ISwipeToDeleteHolder
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ItemSwipeListener

class ContactItemTouchCallback<K>(private val listener: ItemSwipeListener<K>?) : ItemTouchHelper.Callback() {

    private val swipeDirs = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.END or
            ItemTouchHelper.START

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
            ItemTouchHelper.Callback.makeMovementFlags(0, swipeDirs)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
        listener?.onItemSwiped(viewHolder as ISwipeToDeleteHolder<K>, swipeDir)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        getDefaultUIUtil().clearView((viewHolder as ISwipeToDeleteHolder<K>).topContainer)
        listener?.clearView(viewHolder)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            getDefaultUIUtil().onSelected((viewHolder as ISwipeToDeleteHolder<*>).topContainer)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        getDefaultUIUtil().onDraw(c, recyclerView,
                (viewHolder as ISwipeToDeleteHolder<*>).topContainer, dX, dY,
                actionState, isCurrentlyActive)
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        getDefaultUIUtil().onDrawOver(c, recyclerView,
                (viewHolder as ISwipeToDeleteHolder<*>).topContainer, dX, dY,
                actionState, isCurrentlyActive)
    }
}
