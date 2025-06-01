package com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments.BlogPostFragment
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments.UserPostsFragment
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments.TutorialsFragment

class SearchViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3 // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TutorialsFragment()
            1 -> UserPostsFragment()
            2 -> BlogPostFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

}
