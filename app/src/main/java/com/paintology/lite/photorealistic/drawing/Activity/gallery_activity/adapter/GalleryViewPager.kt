package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.GalleryFreeHandFragment
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.GalleryTutorailsFragment

class GalleryViewPager(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GalleryTutorailsFragment()
            1 -> GalleryFreeHandFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

}
