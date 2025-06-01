package com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete

import android.view.View

var rowWidth: Int = 0

internal fun clearOptions(options: ModelOptions<*>?) {
    options?.pendingDelete = false
    options?.posX = 0f
}

internal fun getRowWidth(view: View?) {
    rowWidth = view?.measuredWidth ?: 0
    view?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> rowWidth = right - left }
}

