package com.paintology.lite.photorealistic.drawing.Activity.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log

class FireBaseBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val dataBundle = intent?.extras
        if (dataBundle != null) {
            val sharedPref: SharedPreferences? =
                context?.getSharedPreferences("shareprefrence", Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            try {
                val title = dataBundle.getString("title")
                val body = dataBundle.getString("body")
                val targetType = dataBundle.getString("target_type")
                val targetName = dataBundle.getString("target_name")
                val targetId = dataBundle.getString("target_id")
                editor?.putString("target_type", targetType)
                editor?.putString("target_id", targetId)
                editor?.putString("target_name", targetName)
                editor?.apply()
                Log.e("onMessaceived", "onReceive: notification received $dataBundle")
                Log.e("onMessaceived", "onReceive: notification received $targetType")
                Log.e("onMessaceived", "onReceive: notification received $targetId")
            } catch (e: Exception) {
                Log.e("Notification", "onReceive: error processing notification", e)
            }
        }
    }
}
