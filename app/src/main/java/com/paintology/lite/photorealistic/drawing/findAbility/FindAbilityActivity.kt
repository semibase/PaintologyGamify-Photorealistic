package com.paintology.lite.photorealistic.drawing.findAbility

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.paintology.lite.photorealistic.drawing.databinding.ActivityFindAbilityBinding

class FindAbilityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindAbilityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindAbilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewPager(binding.tabViewpager)

        // If we don't use setupWithViewPager() method then
        // tabs are not used or shown when activity opened
        binding.tabLayout.setupWithViewPager(binding.tabViewpager)


    }

    private fun setupViewPager(viewpager: ViewPager) {
        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(LevelFragment(), "Level")
        adapter.addFragment(ChallengesFragment(), "Challenges")

        // setting adapter to view pager.
        viewpager.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}