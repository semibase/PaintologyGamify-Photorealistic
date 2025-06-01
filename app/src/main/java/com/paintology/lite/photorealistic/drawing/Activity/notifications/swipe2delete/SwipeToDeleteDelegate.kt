package com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete

import android.os.Handler
import android.os.Looper
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ISwipeToDeleteAdapter
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ISwipeToDeleteHolder
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.IUndoClickListener
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ItemSwipeListener

class SwipeToDeleteDelegate<K, in V, H : ISwipeToDeleteHolder<K>>(
    var items: MutableList<@UnsafeVariance V>,
    val swipeToDeleteAdapter: ISwipeToDeleteAdapter<K, V, H>
) : ItemSwipeListener<K>, IUndoClickListener<K> {

    val handler = Handler(Looper.getMainLooper())
    val itemTouchCallBack = ContactItemTouchCallback(this)

    private val holders = HashMap<String, H>()
    private val modelOptionsMap = HashMap<String, ModelOptions<K>>()

    var pending: Boolean = false
    private var knownWidth = false

    fun onBindViewHolder(holder: H, key: String, position: Int) {
        try {
            if (!knownWidth) {
                getRowWidth(holder.topContainer)
                knownWidth = true
            }
            holder.key = key
            if (!modelOptionsMap.containsKey(key))
                modelOptionsMap[key] = ModelOptions(key)

            val item = items[position]

            if (item == null) {
                items.removeAt(position)
                swipeToDeleteAdapter.notifyItemRemoved(position)
            } else {
                holders[key] = holder
                holder.pendingDelete = modelOptionsMap[key]!!.pendingDelete

                if (modelOptionsMap[key]!!.pendingDelete)
                    onBindPendingContact(
                    holder,
                    key,
                    item,
                    position
                )
                else
                {
                    onBindCommonContact(holder, key, item, position)
                }
            }
        } catch (exc: IndexOutOfBoundsException) {
            exc.printStackTrace()
        }
    }

    override fun clearView(viewHolder: ISwipeToDeleteHolder<K>) {
        modelOptionsMap[viewHolder.key]?.viewActive = true
    }

    override fun onItemSwiped(viewHolder: ISwipeToDeleteHolder<K>, swipeDir: Int) {
        val key = viewHolder.key
        if (pending) {
            val modelOption = modelOptionsMap[key]
            if (modelOption?.pendingDelete == true) {
                removeItemByKey(key)
            } else {
                modelOption?.pendingDelete = true
                modelOption?.setDirection(swipeDir)
                swipeToDeleteAdapter.notifyItemChanged(
                    swipeToDeleteAdapter.findItemPositionByKey(
                        key
                    )
                )
            }
        } else {
            removeItem(key)
        }
    }

    override fun onUndo(key: String) {
        val modelOption = modelOptionsMap[key]
        if (modelOption?.viewActive == true) {
            val position = swipeToDeleteAdapter.findItemPositionByKey(key)
            modelOption.pendingDelete = false
            swipeToDeleteAdapter.notifyItemChanged(position)
            modelOption.viewActive = false
        }
    }

    fun onBindCommonContact(holder: H, key: String, item: V, position: Int) =
        swipeToDeleteAdapter.onBindCommonItem(holder, key, item, position)

    fun onBindPendingContact(
        holder: H,
        key: String,
        item: V,
        position: Int
    ) {
        swipeToDeleteAdapter.onBindPendingItem(holder, key, item, position)
    }

    private fun removeItemByKey(key: String) = swipeToDeleteAdapter.removeItem(key)

    fun removeItem(key: String) {
        val position = swipeToDeleteAdapter.findItemPositionByKey(key)
        removeItemFromList(key, items.removeAt(position), position)
    }

    private fun removeItemFromList(key: String, item: V, position: Int) {
        items.remove(item)
        holders.remove(key)
        modelOptionsMap.remove(key)
        swipeToDeleteAdapter.notifyItemRemoved(position)
        clearOptions(modelOptionsMap[key])
        swipeToDeleteAdapter.onItemDeleted(item)
    }
}