package com.paintology.lite.photorealistic.drawing.points

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.UserPointActivity
import com.paintology.lite.photorealistic.drawing.databinding.ActivityPointsBinding
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity

class PointsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f

        // set click listener for testing screen
        binding.imgTest.onSingleClick {
            openActivity(UserPointActivity::class.java)
        }

        getUserRewardPoints()
    }

    private fun getUserRewardPoints() {

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