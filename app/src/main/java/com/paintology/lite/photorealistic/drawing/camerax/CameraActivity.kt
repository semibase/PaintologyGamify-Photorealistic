package com.paintology.lite.photorealistic.drawing.camerax

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityCameraBinding
import java.io.File

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L
//const val ACTION_NEW_PICTURE = "com.paintology.lite.action.NEW_PICTURE"

/**
 *
 *
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 08/08/21.
</ClientName> */
class CameraActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityCameraBinding

    private val imageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val savedUri = intent.data
            Log.d(CameraActivity.javaClass.name, "Photo Uri: $savedUri")
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val intentFilter = IntentFilter(ACTION_NEW_PICTURE)
        registerReceiver(imageReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
        // be trying to set app to immersive mode before it's ready and the flags do not stick
        activityMainBinding.fragmentContainer.postDelayed({
            hideSystemUI()
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    /** When key down event is triggered, relay it via local broadcast so fragments can handle it */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(imageReceiver)
        super.onDestroy()
    }

    companion object {

        //        val ACTION_NEW_PICTURE: String?
        val ACTION_NEW_PICTURE = BuildConfig.APPLICATION_ID + ".action.NEW_PICTURE"

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.externalMediaDirs.firstOrNull()?.let {
                    File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
                }
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            activityMainBinding.fragmentContainer
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}