package com.paintology.lite.photorealistic.drawing.interfaces

import com.paintology.lite.photorealistic.drawing.Enums.SearchResultType

interface SearchItemClickListener {
    fun selectItem(pos: Int, type: SearchResultType)
}