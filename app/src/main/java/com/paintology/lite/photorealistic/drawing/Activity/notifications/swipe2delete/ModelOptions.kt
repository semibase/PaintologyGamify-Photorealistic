package com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete

import androidx.recyclerview.widget.ItemTouchHelper


class ModelOptions<K>(var key: String) {

    var direction: Int? = 0
    var posX = 0f

    var pendingDelete = false
    var viewActive = false

    internal fun setDirection(swipeDir: Int) =
            if (ItemTouchHelper.LEFT == swipeDir || ItemTouchHelper.START == swipeDir) direction = LEFT
            else direction = RIGHT

    companion object {
        const val LEFT = -1
        const val RIGHT = 1
    }
}