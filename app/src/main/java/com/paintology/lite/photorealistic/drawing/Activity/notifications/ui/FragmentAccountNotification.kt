package com.paintology.lite.photorealistic.drawing.Activity.notifications.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.paintology.lite.photorealistic.drawing.Activity.big_points.BigPointActivity
import com.paintology.lite.photorealistic.drawing.Activity.favourite.FavActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment.BaseFragment
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity
import com.paintology.lite.photorealistic.drawing.Activity.notifications.adapter.NotificationAdapter
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.Notification
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
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Chat.ChatActivity
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList
import com.paintology.lite.photorealistic.drawing.Community.Community
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity
import com.paintology.lite.photorealistic.drawing.Model.firebase.Post
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.FragmentAccountNotificationBinding
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard
import com.paintology.lite.photorealistic.drawing.gallery.MyPaintingsActivity
import com.paintology.lite.photorealistic.drawing.minipaint.MyResources
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.SpecingDecoration
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.paintology.lite.photorealistic.drawing.util.showStoreDialog


class FragmentAccountNotification : BaseFragment(), NotificationAdapter.OnNotificationClick {

    // kashif id  o9mMpbVOIGay0slL5uqLHqsMG5B3
    // mine id   Ze0sQREQi3XVcyNBWQx8aoc3vXu2
    // mirza id   5TTWHDN68CeMGNM5jl96bfMLHU02
    // ferdouse id   v3Y69s9hiaVsvJ0VyIDxYeN5TSB3

    private lateinit var binding: FragmentAccountNotificationBinding

    private var notificationAdapter: NotificationAdapter? = null
    private var currentUser: FirebaseUser? = null
    private var mProgressDialog: ProgressDialog? = null
    private var notificationsList = mutableListOf<Notification>()
    private var notifiList = mutableListOf<String>()
    private var array = mutableMapOf<Int, Int>()

    var isFirst = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountNotificationBinding.inflate(layoutInflater, container, false)

        currentUser = FirebaseAuth.getInstance().currentUser
        Log.w("TAGuserID", "onItemClick: ${currentUser?.uid}")

        initRecyclerView()

        notificationsViewModel.getNotifications().observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                refresh(list)
            }
        }
        notificationsViewModel.loadNotifications(currentUser?.uid ?: "")

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh(list: List<Notification>) {
        if (isFirst) {
            isFirst = false
            list.forEach {
                notifiList.add(it.id)
            }
            notificationAdapter!!.addAll(list, notifiList)
        } else {
            list.forEach {
                if (!notifiList.contains(it.id)) {
                    notificationAdapter!!.addItem(it)
                }
            }
        }
        checkData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {

        val space = resources.getDimensionPixelSize(R.dimen._80sdp)
        val spacingDecoration = SpecingDecoration(0, space)
        binding.rvNotifications.addItemDecoration(spacingDecoration)


        /*   , object : NotificationAdapter.onSwipeListener {
               override fun onUndo(key: String, position: Int) {
                   notificationAdapter!!.swipeToDeleteDelegate.onUndo(key)
               }

               override fun onDelete(key: String, position: Int) {
                   val data = notificationsList.indexOfFirst { it.id == key }
                   if (data != -1 && data >= 0 && data < notificationsList.size) {
                       onSwipe(notificationsList[data], data, "swipe")
                   }

               }
           }*/
        notificationAdapter =
            NotificationAdapter(requireContext(), notificationsList, notifiList, this)
        notificationAdapter!!.swipeToDeleteDelegate?.pending = true
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
        val itemTouchHelper = notificationAdapter!!.swipeToDeleteDelegate?.itemTouchCallBack?.let {
            ItemTouchHelper(
                it
            )
        }
        itemTouchHelper!!.attachToRecyclerView(binding.rvNotifications)

    }

    fun checkData() {
        if (notificationsList.size <= 0) {
            binding.emptyData.show()
        } else {
            binding.emptyData.hide()
        }
    }

    override fun onSwipe(model: Notification, position: Int, action: String) {

        try {
            checkData()
            val firestore = FirebaseFirestore.getInstance()
            val notificationRef = firestore.collection("users")
                .document(constants.getString(constants.UserId, requireContext()))
                .collection("notifications")
                .document(model.id)
            notificationRef.update("delete", true)
                .addOnCompleteListener { }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onClick(model: Notification, position: Int, action: String) {
        when (action) {
            "accept" -> {
                checkNotificationTypeWhenAccept(model, position)
            }

            "decline" -> {
                checkNotificationTypeWhenDecline(model, position)
            }
        }
    }

    override fun onItemClick(model: Notification, position: Int) {

        val bundle = Bundle()
        bundle.putString("notification_id", model.id)
        requireContext().sendUserEventWithParam(StringConstants.notifications_open, bundle)

        updateNotificationStatus(model)

        binding.apply {
            systemDetails2.root.show()

            systemDetails2.title.text = model.title
            systemDetails2.body.text = model.body
            Glide.with(this@FragmentAccountNotification)
                .load(model.image_url)
                .placeholder(R.drawable.img_cat_dummy)
                .error(R.drawable.img_cat_dummy)
                .into(systemDetails2.image)

            systemDetails2.close.onSingleClick {
                systemDetails2.root.hide()
            }

            systemDetails2.llMain.onSingleClick {
                systemDetails2.root.hide()
            }

            when (model.data.target_type) {
                "feedback" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        FireUtils.showFeedbackDialog(requireContext())
                    }
                }

                "tutorials" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            startActivity(
                                Intent(
                                    requireContext(),
                                    ChatActivity::class.java
                                ).putExtra("room_id", model.data.target_id)
                            )
                        } else {
                            startActivity(
                                Intent(requireContext(), ChatUserList::class.java)
                            )
                        }
                    }
                }

                "webview" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        if (model.data.target_id.isNotEmpty()) {
                            KGlobal.openInBrowser(requireContext(), model.data.target_id)
                        } else {
                            requireContext().showToast("Something went wrong!Try Again Later")
                        }
                    }
                }

                "gallery" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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

                "followers" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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
                }

                "draw" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(
                            Intent(
                                requireContext(),
                                DrawNowActivity::class.java
                            ).putExtra("target_name", model.data.target_name)
                        )
                    }
                }

                "big-points" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(
                            Intent(
                                requireContext(),
                                BigPointActivity::class.java
                            )
                        )
                    }
                }

                "leaderboards" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(
                            Intent(requireContext(), LeaderBoardActivity::class.java)
                                .putExtra("target_name", model.data.target_name)
                                .putExtra("target_id", model.data.target_id)
                        )

                    }
                }

                "my-paintings" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(
                            Intent(requireContext(), MyPaintingsActivity::class.java)
                        )
                    }

                }

                "my-resources" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(
                            Intent(requireContext(), MyResources::class.java)
                                .putExtra("target_name", model.data.target_name)
                        )
                    }
                }

                "points" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(
                            Intent(requireContext(), UserPointActivity::class.java)
                        )
                    }
                }

                "drawing-activity" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(Intent(requireContext(), ProgressActivity::class.java))
                    }
                }

                "favorites" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(Intent(requireContext(), FavActivity::class.java))
                    }
                }

                "settings" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        startActivity(Intent(requireContext(), SettingActivity::class.java))
                    }
                }

                "store" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        showStoreDialog(requireContext())
                    }
                }

                "rate-the-app" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
                        rateApp()
                    }
                }

                "app-support" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        startActivity(
                            Intent(requireContext(), SupportActivity::class.java)
                        )
                    }
                }

                "profile" -> {
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails2.root.hide()
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
                    systemDetails2.cvMain.onSingleClick {
                        systemDetails.root.hide()
                        updateNotificationStatus(model)
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


    private fun checkNotificationTypeWhenDecline(model: Notification, position: Int) {
        when (model.data.target_type) {
            "followers" -> {
                updateGalleryStatus(model, position)
            }

            else -> {}
        }
    }

    private fun checkNotificationTypeWhenAccept(model: Notification, position: Int) {
        when (model.data.target_type) {
            "followers" -> {
                performFollowBack(model, position)
            }

            else -> {}
        }
    }

    private fun performFollowBack(model: Notification, position: Int) {
        if (constants.getBoolean(constants.IsGuestUser, requireContext())) {
            FireUtils.openLoginScreen(requireContext(), true)
        } else {
            showProgressDialog("Following Please Wait...")
            if (model.data.target_id.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putString("user_id", model.data.target_id)
                requireContext().sendUserEventWithParam(StringConstants.user_follow, bundle)
            }
            FirebaseFirestoreApi.followUser(model.data.target_id ?: return).addOnCompleteListener {
                hideDialog()
                if (it.isSuccessful) {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            requireContext(),
                            constants.user_profile_follow_sucess,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(
                        requireContext(),
                        constants.user_profile_follow_sucess
                    )
                    updateNotificationFollowStatus(model, position)
                } else {
                    try {
                        if (it.exception?.message?.contains("You're already following this user") == true) {
                            updateNotificationFollowStatus(model, position)
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(
                                    requireContext(),
                                    constants.user_profile_follow_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            FirebaseUtils.logEvents(
                                requireContext(),
                                constants.user_profile_follow_fail
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun updateNotificationFollowStatus(model: Notification, pos: Int) {
        if (constants.getString(constants.UserId, requireContext()) != "") {

            val bundle = Bundle()
            bundle.putString("notification_id", model.id)
            requireContext().sendUserEventWithParam(StringConstants.notifications_read, bundle)

            val firestore = FirebaseFirestore.getInstance()
            val notificationRef = firestore.collection("users")
                .document(constants.getString(constants.UserId, requireContext()))
                .collection("notifications")
                .document(model.id)

            notificationRef.update("readFromApp", true, "read", true)
                .addOnCompleteListener {
                    model.readFromApp = true
                    model.read = true
                    notificationAdapter!!.refresh(model, pos)
                }
        }
    }

    private fun updateNotificationStatus(model: Notification) {
        if (constants.getString(constants.UserId, requireContext()) != "") {

            val bundle = Bundle()
            bundle.putString("notification_id", model.id)
            requireContext().sendUserEventWithParam(StringConstants.notifications_read, bundle)

            val firestore = FirebaseFirestore.getInstance()
            val notificationRef = firestore.collection("users")
                .document(constants.getString(constants.UserId, requireContext()))
                .collection("notifications")
                .document(model.id)

            notificationRef.update("readFromApp", true, "read", true)
                .addOnCompleteListener {}
        }
    }

    private fun updateGalleryStatus(model: Notification, pos: Int) {
        if (constants.getString(constants.UserId, requireContext()) != "") {

            val bundle = Bundle()
            bundle.putString("notification_id", model.id)
            requireContext().sendUserEventWithParam(StringConstants.notifications_read, bundle)

            val firestore = FirebaseFirestore.getInstance()
            val notificationRef = firestore.collection("users")
                .document(constants.getString(constants.UserId, requireContext()))
                .collection("notifications")
                .document(model.id)

            notificationRef.update("readFromApp", true, "read", true)
                .addOnCompleteListener {
                    model.readFromApp = true
                    model.read = true
                    notificationAdapter!!.refresh(model, pos)
                }
        }
    }

    private fun showProgressDialog(msg: String) {
        mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog?.apply {
            setTitle(resources.getString(R.string.please_wait))
            setMessage(msg)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun hideDialog() {
        mProgressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}