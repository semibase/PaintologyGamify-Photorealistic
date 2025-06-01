package com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces


interface ISwipeToDeleteAdapter<in K, in V, in H> {

    fun notifyItemRemoved(position: Int)

    fun notifyItemChanged(position: Int)

    fun findItemPositionByKey(key: String): Int

    fun onBindCommonItem(holder: H, key: String, item: V, position: Int)

    fun onBindPendingItem(holder: H, key: String, item: V, position: Int) {}

    fun onItemDeleted(item: V) {}

    fun removeItem(key: String)
}