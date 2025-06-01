package com.paintology.lite.photorealistic.drawing.Activity.notifications.ui.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.commonMenuClick
import com.paintology.lite.photorealistic.drawing.Activity.notifications.adapter.NotificationViewPager
import com.paintology.lite.photorealistic.drawing.Activity.notifications.checkNotify
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityNotificationBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class NotificationActivity : BaseActivity() {


    var constants = StringConstants()

    private val binding by lazy {
        ActivityNotificationBinding.inflate(layoutInflater)
    }

    private fun updateNotificationStatus(nId: String) {
        if (constants.getString(constants.UserId, this) != "" && nId != "") {

            val bundle = Bundle()
            bundle.putString("notification_id", nId)
            sendUserEventWithParam(StringConstants.notifications_read, bundle)

            val firestore = FirebaseFirestore.getInstance()
            val notificationRef = firestore.collection("users")
                .document(constants.getString(constants.UserId, this))
                .collection("notifications")
                .document(nId)

            notificationRef.update("readFromDevice", true, "read", true)
                .addOnCompleteListener {}
        }
    }

    private var currentUser: FirebaseUser? = null
    private var adapter: NotificationViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        currentUser = FirebaseAuth.getInstance().currentUser

        if (intent != null && intent.hasExtra("notification_id")) {
            val notification_id = intent.getStringExtra("notification_id") ?: ""
            val target_type = intent.getStringExtra("target_type") ?: ""
            val target_name = intent.getStringExtra("target_name") ?: ""
            val target_id = intent.getStringExtra("target_id") ?: ""
            val bundle = Bundle()
            bundle.putString("notification_id", notification_id)
            updateNotificationStatus(notification_id)
            sendUserEventWithParam(StringConstants.notifications_open, bundle)
            checkNotify(target_type, target_name, target_id)
        }


        notificationsViewModel.loadNotificationsCount(
            FirebaseAuth.getInstance().currentUser?.uid ?: return
        )
        notificationsViewModel.loadNotificationsAdminCount()

        notificationsViewModel.totalNotifications.observe(this) {
            totalNotifications(it ?: 0)
        }

        notificationsViewModel.totalNotificationsAdmin.observe(this) {
            totalNotificationsAdmin(it ?: 0)
        }

        initToolbar()
        initViewPagerAdepter()

    }

    /* private fun updateNotificationStatus(model: String) {
         if (constants.getString(constants.UserId, this) != "") {
             val firestore = FirebaseFirestore.getInstance()
             val notificationRef = firestore.collection("users")
                 .document(constants.getString(constants.UserId, this))
                 .collection("notifications")
                 .document(model)

             notificationRef.update("read", true)
                 .addOnCompleteListener { }
         }
     }

 */


    fun totalNotifications(i: Int) {
        binding.tabLayout.getTabAt(0)?.text = "${getString(R.string.account)} (${i})"
    }

    fun totalNotificationsAdmin(i: Int) {
        binding.tabLayout.getTabAt(1)?.text = "${getString(R.string.paintology)} (${i})"
    }

    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            ivMenu.visibility = View.VISIBLE
            ivMenu.onSingleClick {
                ivMenu.showPopupMenu(R.menu.new_common_menu) {
                    commonMenuClick(it, StringConstants.intro_notifications)
                }
            }
            tvTitle.text = getString(R.string.title_notifications)
            imgFav.hide()
        }
    }

    private fun initViewPagerAdepter() {
        adapter = NotificationViewPager(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            // Set tab text or custom view based on position
            when (position) {
                0 -> tab.text = getString(R.string.account)
                1 -> tab.text = getString(R.string.paintology)
            }
        }.attach()
    }
}
