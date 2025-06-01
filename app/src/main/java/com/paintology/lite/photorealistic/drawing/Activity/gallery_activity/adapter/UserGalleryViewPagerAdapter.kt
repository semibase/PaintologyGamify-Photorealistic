package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.UserGalleryFreeHandFragment
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.UserGalleryTutorialFragment

class UserGalleryViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments: Array<Fragment?> = arrayOfNulls(2)

    override fun getItemCount(): Int {
        return 2 // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> UserGalleryTutorialFragment()
            1 -> UserGalleryFreeHandFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
        fragments[position] = fragment
        return fragment
    }

    fun getFragment(position: Int): Fragment? {
        return fragments[position]
    }
}
