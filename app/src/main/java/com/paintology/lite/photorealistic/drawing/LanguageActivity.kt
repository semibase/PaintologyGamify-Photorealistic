package com.paintology.lite.photorealistic.drawing

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.databinding.ActivityLanguageBinding
import com.paintology.lite.photorealistic.drawing.onboarding.LanguageFragment

class LanguageActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Select Language"

        val bundle = Bundle()
        bundle.putFloat("elevation", 0f)
        bundle.putBoolean("hide_heading", false)

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.fragment_container_view, LanguageFragment::class.java, bundle)
            .commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}