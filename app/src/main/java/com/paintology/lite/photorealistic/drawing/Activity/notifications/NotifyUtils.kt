package com.paintology.lite.photorealistic.drawing.Activity.notifications

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.gson.GsonBuilder
import com.paintology.lite.photorealistic.drawing.Activity.big_points.BigPointActivity
import com.paintology.lite.photorealistic.drawing.Activity.favourite.FavActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity
import com.paintology.lite.photorealistic.drawing.Activity.notifications.ui.activities.NotificationActivity
import com.paintology.lite.photorealistic.drawing.Activity.profile.UserProfileActivity
import com.paintology.lite.photorealistic.drawing.Activity.settings.SettingActivity
import com.paintology.lite.photorealistic.drawing.Activity.support.SupportActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.ProgressActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.UserPointActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.DrawingUtils
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Activity.video_intro.IntroVideoActivity
import com.paintology.lite.photorealistic.drawing.Activity.video_intro.IntroVideoListActivity
import com.paintology.lite.photorealistic.drawing.Chat.ChatActivity
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList
import com.paintology.lite.photorealistic.drawing.Community.Community
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity
import com.paintology.lite.photorealistic.drawing.Model.firebase.Post
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard
import com.paintology.lite.photorealistic.drawing.gallery.MyPaintingsActivity
import com.paintology.lite.photorealistic.drawing.minipaint.MyResources
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.StringConstants.constants
import com.paintology.lite.photorealistic.drawing.util.showStoreDialog

private fun Context.goToBlogPost(post: Post) {
    post.ref?.let {
        KGlobal.openInBrowser(this, it)
    }
}

fun Activity.checkNotify(
    target_type: String = "",
    target_name: String = "",
    target_id: String = ""
) {

    when (target_type) {

        "feedback" -> {
            FireUtils.showFeedbackDialog(this)
        }

        "big-points" -> {
            if (this is NotificationActivity) {
                startActivity(
                    Intent(this, BigPointActivity::class.java)
                )
            }
        }

        "drawer-menu" -> {
            if (this is BigPointActivity) {
                constants.putBoolean(StringConstants.isMenuShow, true, this)
                finish()
            }
        }

        "app-support" -> {
            startActivity(
                Intent(this, SupportActivity::class.java)
            )
        }

        "notifications" -> {
            if (this is BigPointActivity) {
                startActivity(
                    Intent(this, NotificationActivity::class.java)
                )
            }
        }

        "tutorials" -> {
            if (target_id.isNotEmpty()) {
                FireUtils.showProgressDialog(this, getString(R.string.please_wait))
                TutorialUtils(this).parseTutorial(target_id)
            } else {
                if (target_name.isEmpty()) {
                    startActivity(
                        Intent(this, CategoryActivity::class.java)
                    )
                } else {
                    startActivity(
                        Intent(this, CategoryActivity::class.java).putExtra(
                            "cate_id",
                            target_name
                        )
                    )
                }

            }
        }

        "webview" -> {
            if (target_id.isNotEmpty()) {
                KGlobal.openInBrowser(this, target_id)
            } else {
                showToast(getString(R.string.ss_something_went_wrong_try_again_later))
            }
        }

        "gallery" -> {
            if (target_id.isNotEmpty()) {
                FireUtils.showProgressDialog(this, getString(R.string.please_wait))
                DrawingUtils(this).fetchGalleryPost(target_id)
            } else {
                startActivity(
                    Intent(this, GalleryActivity::class.java)
                )
            }
        }

        "community-posts" -> {
            if (target_id.isNotEmpty()) {
                startActivity(
                    Intent(this, CommunityDetail::class.java)
                        .putExtra("post_id", target_id)
                )
            } else {
                startActivity(
                    Intent(this, Community::class.java)
                )
            }
        }

        "posts" -> {
            if (target_id.isNotEmpty()) {
                FireUtils.showProgressDialog(this, getString(R.string.please_wait))
                FirebaseFirestoreApi
                    .getPostDetail(target_id)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            FireUtils.hideProgressDialog()
                            GalleryDashboard.longLog("${it.result.data}")
                            if (it.result.data != null) {
                                val responseData = it.result.data as Map<String, Any>
                                val gson = GsonBuilder().create()
                                val json = gson.toJson(responseData)

                                val post = gson.fromJson(
                                    json,
                                    Post::class.java
                                )
                                goToBlogPost(post)
                            }
                        } else {
                            FireUtils.hideProgressDialog()
                        }
                    }
            } else {
                showToast(getString(R.string.ss_something_went_wrong_try_again_later))
            }
        }

        "draw" -> {
            startActivity(
                Intent(this, DrawNowActivity::class.java).putExtra("target_name", target_name)
            )
        }

        "chat" -> {
            if (target_id.isNotEmpty()) {
                startActivity(
                    Intent(this, ChatActivity::class.java).putExtra("room_id", target_id)
                )
            } else {
                startActivity(
                    Intent(this, ChatUserList::class.java)
                )
            }
        }

        "video-guides" -> {
            if (target_id.isNotEmpty()) {
                startActivity(
                    Intent(this, IntroVideoActivity::class.java).putExtra("video_id", target_id)
                )
            } else {
                startActivity(
                    Intent(this, IntroVideoListActivity::class.java)
                )
            }
        }

        "leaderboards" -> {
            startActivity(
                Intent(this, LeaderBoardActivity::class.java).putExtra(
                    "target_name",
                    target_name
                ).putExtra("target_id", target_id)
            )
        }

        "my-paintings" -> {
            startActivity(
                Intent(this, MyPaintingsActivity::class.java)
            )
        }

        "my-resources" -> {
            startActivity(
                Intent(this, MyResources::class.java).putExtra("target_name", target_name)
            )
        }

        "points" -> {
            startActivity(
                Intent(this, UserPointActivity::class.java)
            )
        }

        "drawing-activity" -> {
            startActivity(Intent(this, ProgressActivity::class.java))
        }

        "favorites" -> {
            startActivity(Intent(this, FavActivity::class.java))
        }

        "settings" -> {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        "store" -> {
            showStoreDialog(this)
        }

        "rate-the-app" -> {
            rateApp()
        }

        "profile" -> {
            if (target_id.isNotEmpty()) {
                val id: String =
                    constants.getString(constants.UserId, this)
                        .toString() ?: ""

                if (id == target_id) {
                    FireUtils.openProfileScreen(this, null)
                } else {
                    val intent = Intent(
                        this,
                        UserProfileActivity::class.java
                    )
                    intent.putExtra(StringConstants.SelectedUserId, target_id)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            } else {
                showToast(getString(R.string.ss_something_went_wrong_try_again_later))
            }
        }

        "user" -> {
            FireUtils.openProfileScreen(this, null)
        }

        else -> {}
    }
}

private fun Context.rateApp() {
    try {
        val rateIntent = rateIntentForUrl("market://details")
        startActivity(rateIntent)
    } catch (e: ActivityNotFoundException) {
        val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
        startActivity(rateIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun Context.rateIntentForUrl(url: String): Intent {
    val packageName = packageName

    Log.d("packageName", "rateIntentForUrl: $packageName")

    val intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, packageName)))
    var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    flags = if (Build.VERSION.SDK_INT >= 21) {
        flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
    } else {
        flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
    }
    intent.addFlags(flags)
    return intent
}
