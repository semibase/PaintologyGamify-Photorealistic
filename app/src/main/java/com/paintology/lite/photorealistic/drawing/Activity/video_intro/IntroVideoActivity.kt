package com.paintology.lite.photorealistic.drawing.Activity.video_intro

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityIntroVideoBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class IntroVideoActivity : AppCompatActivity() {

    private val constants = StringConstants()

    private var event_name = ""
    private var videoTitle = ""
    private var videoCategory = ""
    private var videoId = ""
    private var video_id = ""
    private val binding by lazy {
        ActivityIntroVideoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = intent
        if (intent != null && intent.hasExtra("video_id")) {
            video_id = intent.getStringExtra("video_id")!!
        }

        if (intent != null && intent.hasExtra("event_name")) {
            event_name = intent.getStringExtra("event_name").toString();
        }

        if (event_name != "") {
            val bundle = Bundle()
            bundle.putString("video_id", video_id)
            bundle.putString("screen", "install")
            sendUserEventWithParam(
                StringConstants.intro_video_watch, bundle
            )
        }

        if (video_id.isNotEmpty()) {
            val isVideoShown = constants.getString(StringConstants.isVideoShown, this)
            constants.putString(
                StringConstants.isVideoShown,
                isVideoShown.plus("_" + video_id + "_"),
                this
            )
            initData()
        }
    }

    private fun addViewCount() {
        FirebaseFirestoreApi.fetchIntroVideoById(video_id)
            .addOnCompleteListener {
            }
    }

    private fun fetchVideo() {

        FirebaseFirestoreApi.fetchIntroVideos(1, "id:=" + video_id, "")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        val data = task.getResult().getData() as HashMap<*, *>
                        val mlist = data["data"] as List<*>
                        if (mlist.isNotEmpty()) {
                            for (tlist in mlist) {
                                val item = tlist as HashMap<*, *>
                                videoTitle = (item["title"] ?: "").toString()
                                videoCategory = (item["category"] ?: "").toString()
                                if (item.containsKey("links")) {
                                    val links = item["links"] as HashMap<*, *>
                                    if (links.containsKey("youtube")) {
                                        videoId = links["youtube"].toString().replace("/", "")
                                        playVideo()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.toString()
                    }
                } else {
                    Log.e("TAGRR", task.exception.toString())
                }
            }
    }

    private lateinit var closeLayout: RelativeLayout
    private lateinit var youTubePlayer: YouTubePlayer

    private fun initData() {

        binding.btnClose.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("guide_id", video_id)
            bundle.putString("category", videoCategory)
            bundle.putString("title", videoTitle)
            sendUserEventWithParam(
                StringConstants.video_guides_close, bundle
            )
            finish()
        }
        lifecycle.addObserver(binding.videoIntro)

        addViewCount()
        fetchVideo()
    }

    private fun playVideo() {
        binding.videoIntro.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onStateChange(
                @NonNull youTubePlayer: YouTubePlayer,
                @NonNull state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)

            }

            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                this@IntroVideoActivity.youTubePlayer = youTubePlayer
                playIntroVideo()
            }

            override fun onError(
                @NonNull youTubePlayer: YouTubePlayer,
                @NonNull error: PlayerConstants.PlayerError
            ) {
                super.onError(youTubePlayer, error)
                Toast.makeText(
                    this@IntroVideoActivity,
                    getString(R.string.s_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun playIntroVideo() {
        try {
            binding.introText.text = videoTitle
            youTubePlayer.loadVideo(videoId, 0f)
        } catch (exception: IllegalStateException) {
            Toast.makeText(this, getString(R.string.s_wrong), Toast.LENGTH_SHORT)
                .show()
        }
    }
}