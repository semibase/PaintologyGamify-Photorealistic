package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity

interface OnBackPressedListener {
    /**
     * If you return true, the back press will not be handled by the activity.
     * If you return false, the back press will be handled by the activity.
     */
    fun onBackPressed(): Boolean
}
