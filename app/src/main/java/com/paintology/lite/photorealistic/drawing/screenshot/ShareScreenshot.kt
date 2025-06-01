package com.paintology.lite.photorealistic.drawing.screenshot

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityShareScreenshotBinding
import com.paintology.lite.photorealistic.drawing.gallery.MyPaintingsActivity.BroadcastTest
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity

class ShareScreenshot : AppCompatActivity() {

    private lateinit var binding: ActivityShareScreenshotBinding
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareScreenshotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uri = intent.getParcelableExtra("uri")

        binding.ivImage.setImageURI(uri)

        setListener()
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

    private fun setListener() {
        binding.btnShare.setOnClickListener {
            uri?.let { it1 -> doSocialShare(it1) }
        }
    }

    fun doSocialShare(photoURI: Uri) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            //            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
//            Uri uri = Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID +"/drawable/google_play_with_paintology");
            var text = resources.getString(R.string.default_msg_while_share)
            text = """
            $text
            
            https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
            """.trimIndent()
            val files = ArrayList<Uri>()
            files.add(photoURI)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
            shareIntent.type = "*/*"
            val receiver = Intent(
                this,
                BroadcastTest::class.java
            )
            val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    receiver,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    receiver,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            val chooser: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent.createChooser(shareIntent, "Share To", pendingIntent.intentSender)
            } else {
                Intent.createChooser(shareIntent, "Share To")
            }
            startActivity(chooser)
        } catch (e: Exception) {
            Log.e(PaintActivity::class.java.name, e.message!!)
        }
    }

//    class BroadcastForMovies : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            Log.e("TAGG", "OnReceived Called")
//            try {
//                for (key in intent.extras!!.keySet()) {
//                    Log.e(javaClass.simpleName, " " + intent.extras!![key])
//                    val _app_name = " " + intent.extras!![key]
//                    //                    Log.e("TAGGG", " " + intent.getExtras().get(key));
//                    var shareFileVia = ""
//                    shareFileVia =
//                        if (_app_name.contains("skype")) "skype" else if (_app_name.contains("apps.photos")) "photos" else if (_app_name.contains(
//                                "android.gm"
//                            )
//                        ) "gmail" else if (_app_name.contains("apps.docs")) "drive" else if (_app_name.contains(
//                                "messaging"
//                            )
//                        ) "messages" else if (_app_name.contains("android.talk")) "hangout" else if (_app_name.contains(
//                                "xender"
//                            )
//                        ) "xender" else if (_app_name.contains("instagram")) "instagram" else if (_app_name.contains(
//                                "youtube"
//                            )
//                        ) "youtube" else if (_app_name.contains("maps")) "maps" else if (_app_name.contains(
//                                "bluetooth"
//                            )
//                        ) "bluetooth" else if (_app_name.contains("facebook")) "facebook" else if (_app_name.contains(
//                                "whatsapp"
//                            )
//                        ) "whatsapp" else if (_app_name.contains("com.facebook.orca")) "facebook_messager" else if (_app_name.contains(
//                                "linkedin"
//                            )
//                        ) "linkedin" else if (_app_name.contains("sketch")) "sketchapp" else _app_name
//                    Log.e("TAGGG", "share_movie_via $shareFileVia ")
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(
//                            context,
//                            "share_movies_via_$shareFileVia",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    FirebaseUtils.logEvents(context, "share_movies_via_$shareFileVia")
//                }
//            } catch (e: Exception) {
//                Log.e("TAGGG", "Exception while share image " + e.message, e)
//            }
//        }
//    }
}