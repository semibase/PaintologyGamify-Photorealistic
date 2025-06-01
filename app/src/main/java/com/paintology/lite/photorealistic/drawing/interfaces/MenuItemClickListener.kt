package com.paintology.lite.photorealistic.drawing.interfaces

import android.view.View
import com.paintology.lite.photorealistic.drawing.painting.PaintItem

interface MenuItemClickListener {
    fun onEditClick(item: PaintItem?, position: Int)
    fun onDeleteClick(item: PaintItem?, position: Int)
    fun onShareClick(item: PaintItem?, position: Int)
    fun onPostClick(item: PaintItem?, position: Int)
    fun onSubMenuClick(view: View, item: PaintItem?, position: Int)
}