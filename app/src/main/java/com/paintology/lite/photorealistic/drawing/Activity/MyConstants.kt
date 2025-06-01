package com.paintology.lite.photorealistic.drawing.Activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.paintology.lite.photorealistic.drawing.util.setSharedNo

fun Activity.checkForIntroVideo(videoId: String) {
   /* val constants = StringConstants.getInstance()
    if (constants.getBoolean(
            StringConstants.isNewUser,
            this
        ) && !constants.getString(
            StringConstants.isVideoShown,
            this
        ).contains(videoId)
    ) {
        FireUtils.openIntroVideoScreen(
            this,
            videoId,
            videoId
        )
    }*/
}

fun Activity.commonMenuClick(item: MenuItem, videoId: String): Boolean {
    if (item.itemId == android.R.id.home) {
        this.onBackPressed()
        return true;
    } else if (item.itemId == R.id.action_feedback) {
        val bundle = Bundle()
        bundle.putString("video_id", videoId)
        bundle.putString("screen", "feedback")
        sendUserEventWithParam(StringConstants.intro_video_watch, bundle)
        FireUtils.showFeedbackDialog(this)
        return true;
    } else if (item.itemId == R.id.action_post_gallery) {
        sendUserEvent("gallery_3dot_post")
        val mPrefBackgroundColor = -1
        setSharedNo()
        val lIntent1 = Intent(this, PaintActivity::class.java)
        lIntent1.setAction("New Paint")
        StringConstants.constants.putInt("background_color", mPrefBackgroundColor, this)
        lIntent1.putExtra("background_color", mPrefBackgroundColor)
        lIntent1.putExtra("isFromGallery", true)
        startActivity(lIntent1)
        return true;
    } else if (item.itemId == R.id.action_watch_intro_video) {
        val bundle = Bundle()
        bundle.putString("video_id", videoId)
        bundle.putString("screen", "video")
        sendUserEventWithParam(StringConstants.intro_video_watch, bundle)
        FireUtils.openIntroVideoScreen(this, videoId, "")
        return true
    } else if (item.itemId == R.id.action_rateUS) {

        try {
            val url =
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
            Log.e("Community", e.message!!)
        }
        /*val reviewManager by lazy {
            ReviewManagerFactory.create(this)
        }

        inAppReview(reviewManager)*/
        return true
    }
    return onOptionsItemSelected(item)
}

private fun Activity.inAppReview(reviewManager: ReviewManager) {
    reviewManager.requestReviewFlow().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            launchFlow(task.result, reviewManager)
        } else {
            val message = task.exception?.message
            Log.d(ContentValues.TAG, "initFlow: $message")
        }
    }
}

private fun Activity.launchFlow(reviewInfo: ReviewInfo, reviewManager: ReviewManager) {
    val flow = reviewManager.launchReviewFlow(this, reviewInfo)
    flow.addOnCompleteListener {
        if (it.isSuccessful) {
            try {
                val url =
                    "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            } catch (e: Exception) {
                Log.e("Community", e.message!!)
            }
        } else {
            val message = it.exception?.message
            Log.d(ContentValues.TAG, "initFlow: $message")
        }
    }
}