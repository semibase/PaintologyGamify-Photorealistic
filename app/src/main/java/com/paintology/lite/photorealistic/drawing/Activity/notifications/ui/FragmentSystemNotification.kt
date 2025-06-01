package com.paintology.lite.photorealistic.drawing.Activity.notifications.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.GsonBuilder
import com.paintology.lite.photorealistic.drawing.Activity.big_points.BigPointActivity
import com.paintology.lite.photorealistic.drawing.Activity.favourite.FavActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.BaseFragment
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity
import com.paintology.lite.photorealistic.drawing.Activity.notifications.adapter.NotificationAdapterAdmin
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.NotificationAdmin
import com.paintology.lite.photorealistic.drawing.Activity.profile.UserProfileActivity
import com.paintology.lite.photorealistic.drawing.Activity.settings.SettingActivity
import com.paintology.lite.photorealistic.drawing.Activity.support.SupportActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.ProgressActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.UserPointActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.DrawingUtils
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
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
import com.paintology.lite.photorealistic.drawing.databinding.FragmentSystemNotificationBinding
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard
import com.paintology.lite.photorealistic.drawing.gallery.MyPaintingsActivity
import com.paintology.lite.photorealistic.drawing.minipaint.MyResources
import com.paintology.lite.photorealistic.drawing.room.AppDatabase
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.SpecingDecoration
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.paintology.lite.photorealistic.drawing.util.showStoreDialog


class FragmentSystemNotification : BaseFragment(), NotificationAdapterAdmin.OnNotificationClick {

    private lateinit var binding: FragmentSystemNotificationBinding

    private var notificationAdapterAdmin: NotificationAdapterAdmin? = null
    private var db: AppDatabase? = null

    private var notificationsList = mutableListOf<NotificationAdmin>()
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSystemNotificationBinding.inflate(layoutInflater, container, false)

        db = MyApplication.getDb()

        currentUser = FirebaseAuth.getInstance().currentUser
        initRecyclerViewAdmin()
        notificationsViewModel.getNotificationsAdmin().observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                refresh(list)
            }
        }
        notificationsViewModel.loadNotificationsAdmin()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh(list: List<NotificationAdmin>) {
        notificationsList.clear()
        notificationsList.addAll(list)
        notificationAdapterAdmin!!.notifyDataSetChanged()
        checkData()
    }

    fun checkData() {
        if (notificationsList.size <= 0) {
            binding.emptyData.show()
        } else {
            binding.emptyData.hide()
        }
    }

    private fun initRecyclerViewAdmin() {
        val space = resources.getDimensionPixelSize(R.dimen._80sdp)
        val spacingDecoration = SpecingDecoration(0, space)
        binding.rvNotificationAdmin.addItemDecoration(spacingDecoration)
        notificationAdapterAdmin = NotificationAdapterAdmin(notificationsList, this)
        binding.rvNotificationAdmin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapterAdmin
        }
    }


    override fun onItemClick(model: NotificationAdmin, position: Int) {

        val bundle = Bundle()
        bundle.putString("notification_id", model.id)
        requireContext().sendUserEventWithParam(StringConstants.notifications_open, bundle)

        binding.apply {
            systemDetails.root.show()

            systemDetails.title.text = model.title
            systemDetails.body.text = model.body
            Glide.with(this@FragmentSystemNotification)
                .load(model.image_url)
                .placeholder(R.drawable.img_cat_dummy)
                .error(R.drawable.img_cat_dummy)
                .into(systemDetails.image)

            systemDetails.close.onSingleClick {
                systemDetails.root.hide()
            }
            systemDetails.llMain.onSingleClick {
                systemDetails.root.hide()
            }
            when (model.data.target_type) {
                "feedback" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        FireUtils.showFeedbackDialog(requireContext())
                    }
                }

                "tutorials" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            FireUtils.showProgressDialog(
                                requireContext(),
                                getString(R.string.please_wait)
                            )
                            TutorialUtils(requireContext()).parseTutorial(model.data.target_id)
                        } else {
                            if (model.data.target_name.isEmpty()) {
                                startActivity(
                                    Intent(requireContext(), CategoryActivity::class.java)
                                )
                            } else {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        CategoryActivity::class.java
                                    ).putExtra("cate_id", model.data.target_name)
                                )
                            }
                        }
                    }
                }

                "chat" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            startActivity(
                                Intent(requireContext(), ChatActivity::class.java).putExtra("room_id", model.data.target_id)
                            )
                        }else{
                            startActivity(
                                Intent(requireContext(), ChatUserList::class.java)
                            )
                        }
                    }
                }

                "webview" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            KGlobal.openInBrowser(requireContext(), model.data.target_id)
                        } else {
                            requireContext().showToast("Something went wrong!Try Again Later")
                        }
                    }
                }

                "gallery" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            FireUtils.showProgressDialog(
                                requireContext(),
                                getString(R.string.please_wait)
                            )
                            DrawingUtils(requireContext()).fetchGalleryPost(model.data.target_id)
                        } else {
                            startActivity(
                                Intent(requireContext(), GalleryActivity::class.java)
                            )
                        }
                    }

                }

                "community-posts" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            startActivity(
                                Intent(requireContext(), CommunityDetail::class.java)
                                    .putExtra("post_id", model.data.target_id)
                            )
                        } else {
                            startActivity(
                                Intent(requireContext(), Community::class.java)
                            )
                        }
                    }
                }

                "posts" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            FireUtils.showProgressDialog(
                                requireContext(),
                                getString(R.string.please_wait)
                            )
                            FirebaseFirestoreApi
                                .getPostDetail(model.data.target_id)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        FireUtils.hideProgressDialog()
                                        GalleryDashboard.longLog("${it.result.data}")
                                        val responseData = it.result.data as Map<String, Any>
                                        val gson = GsonBuilder().create()
                                        val json = gson.toJson(responseData)

                                        val post = gson.fromJson(
                                            json,
                                            Post::class.java
                                        )

                                        goToBlogPost(post)
                                    } else {
                                        FireUtils.hideProgressDialog()
                                    }
                                }
                        } else {
                            requireContext().showToast("Something went wrong!Try Again Later")
                        }
                    }
                }

                "video-guides" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            startActivity(
                                Intent(
                                    requireContext(),
                                    IntroVideoActivity::class.java
                                ).putExtra("video_id", model.data.target_id)
                            )
                        } else {
                            startActivity(
                                Intent(requireContext(), IntroVideoListActivity::class.java)
                            )
                        }
                    }

                }

                "draw" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), DrawNowActivity::class.java)
                                .putExtra("target_name", model.data.target_name)
                        )
                    }
                }

                "big-points" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(
                                requireContext(),
                                BigPointActivity::class.java
                            )
                        )
                    }
                }

                "leaderboards" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), LeaderBoardActivity::class.java)
                                .putExtra("target_name", model.data.target_name)
                                .putExtra("target_id", model.data.target_id)
                        )
                    }
                }

                "my-paintings" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), MyPaintingsActivity::class.java)
                        )
                    }
                }

                "my-resources" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), MyResources::class.java)
                                .putExtra("target_name", model.data.target_name)
                        )
                    }
                }

                "points" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), UserPointActivity::class.java)
                        )
                    }
                }

                "drawing-activity" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(Intent(requireContext(), ProgressActivity::class.java))
                    }
                }

                "favorites" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(Intent(requireContext(), FavActivity::class.java))
                    }
                }

                "settings" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(Intent(requireContext(), SettingActivity::class.java))
                    }
                }

                "store" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        showStoreDialog(requireContext())
                    }
                }

                "rate-the-app" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        rateApp()
                    }
                }

                "app-support" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), SupportActivity::class.java)
                        )
                    }
                }

                "profile" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            val id: String =
                                constants.getString(constants.UserId, requireContext())
                                    .toString() ?: ""

                            if (id == model.data.target_id) {
                                FireUtils.openProfileScreen(requireContext(), null)
                            } else {
                                val intent = Intent(
                                    requireContext(),
                                    UserProfileActivity::class.java
                                )
                                intent.putExtra(
                                    StringConstants.SelectedUserId,
                                    model.data.target_id
                                )
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                        } else {
                            requireContext().showToast("Something went wrong!Try Again Later")
                        }
                    }
                }

                "user" -> {
                    systemDetails.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        FireUtils.openProfileScreen(requireContext(), null)
                    }

                }

                else -> {
                }
            }
        }

    }


    fun rateApp() {
        try {
            val rateIntent = rateIntentForUrl("market://details")
            startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
            startActivity(rateIntent)
        }
    }

    private fun rateIntentForUrl(url: String): Intent {
        val packageName = requireContext().packageName

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

    private fun goToBlogPost(post: Post) {
        post.ref?.let {
            KGlobal.openInBrowser(requireContext(), it)
        }
    }
}