package com.paintology.lite.photorealistic.drawing.photoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityPhotoeditorBinding

class PhotoEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoeditorBinding

    companion object {
        @JvmField
        var activity: AppCompatActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoeditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this@PhotoEditorActivity

        val mainFragment = PhotoEditorFiltersFragment()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit()

    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is PhotoEditorFiltersFragment) {
            PhotoEditorFiltersFragment.ManageBackPress()
        } else {
            super.onBackPressed()
        }
    }
}