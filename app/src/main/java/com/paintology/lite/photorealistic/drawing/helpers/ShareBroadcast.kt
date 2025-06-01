package com.paintology.lite.photorealistic.drawing.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils

class ShareBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("TAGG", "OnReceived Called")
        try {
            for (key in intent.extras!!.keySet()) {
                Log.e(javaClass.simpleName, " " + intent.extras!![key])
                val _app_name = " " + intent.extras!![key]
                //                    Log.e("TAGGG", " " + intent.getExtras().get(key));
                var shareFileVia = ""
                shareFileVia =
                    if (_app_name.contains("skype")) "skype" else if (_app_name.contains("apps.photos")) "photos" else if (_app_name.contains(
                            "android.gm"
                        )
                    ) "gmail" else if (_app_name.contains("apps.docs")) "drive" else if (_app_name.contains(
                            "messaging"
                        )
                    ) "messages" else if (_app_name.contains("android.talk")) "hangout" else if (_app_name.contains(
                            "xender"
                        )
                    ) "xender" else if (_app_name.contains("instagram")) "instagram" else if (_app_name.contains(
                            "youtube"
                        )
                    ) "youtube" else if (_app_name.contains("maps")) "maps" else if (_app_name.contains(
                            "bluetooth"
                        )
                    ) "bluetooth" else if (_app_name.contains("facebook")) "facebook" else if (_app_name.contains(
                            "whatsapp"
                        )
                    ) "whatsapp" else if (_app_name.contains("com.facebook.orca")) "facebook_messager" else if (_app_name.contains(
                            "linkedin"
                        )
                    ) "linkedin" else if (_app_name.contains("sketch")) "sketchapp" else _app_name
                Log.e("TAGGG", "share_movie_via $shareFileVia ")
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "share_via_$shareFileVia", Toast.LENGTH_SHORT).show()
                }
                FirebaseUtils.logEvents(context, "share_via_$shareFileVia")
            }
        } catch (e: Exception) {
            Log.e("TAGGG", "Exception while share image " + e.message, e)
        }
    }
}