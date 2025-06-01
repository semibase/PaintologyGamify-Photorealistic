package com.paintology.lite.photorealistic.drawing.notification

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.Activity.notifications.ui.activities.NotificationActivity
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Chat.ChatActivity
import com.paintology.lite.photorealistic.drawing.Chat.ChatUserList
import com.paintology.lite.photorealistic.drawing.Community.ShowPostFromNotification
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity
import com.paintology.lite.photorealistic.drawing.Model.NotificationModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityNotificationsBinding
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.MarginDecoration
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail

class NotificationsActivity : AppCompatActivity(), NotificationAdapter.OnItemClickListener {

    private lateinit var binding: ActivityNotificationsBinding
    private var dataList: List<NotificationModel> = ArrayList()
    private lateinit var rvAdapter: NotificationAdapter
    private var chatDataList: List<NotificationModel> = ArrayList()
    private lateinit var rvChatAdapter: NotificationAdapter
    private var constants: StringConstants? = StringConstants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        title = getString(R.string.title_notifications)

        initToolbar()

        setListeners()
        setupRecyclerView()

        if (AppUtils.isLoggedIn()) {
            binding.loginMsgContainer.visibility = View.GONE
            getNotifications()
            getChatNotifications()
        } else {
            binding.loginMsgContainer.visibility = View.VISIBLE
        }

        AppUtils.saveHasUnreadNotifications(this@NotificationsActivity, false)
    }

    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.title_notifications)
            imgFav.apply {
                show()
                setImageResource(R.drawable.img_test)
                onSingleClick {
                    openActivity(NotificationActivity::class.java)
                }
            }
        }
    }

    private fun setListeners() {
        binding.ivCommunity.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@NotificationsActivity,
                    StringConstants.slidepop_notif_comm,
                    Toast.LENGTH_SHORT
                ).show()
            }
            FirebaseUtils.logEvents(this@NotificationsActivity, StringConstants.slidepop_notif_comm)
            val intent = Intent(
                this@NotificationsActivity,
                CommunityDetail::class.java
            )
            intent.putExtra(
                "user_id",
                constants?.getString(
                    constants?.UserId,
                    this@NotificationsActivity
                )
            )
            intent.putExtra("showPost", true)
            startActivity(intent)
        }

        binding.ivChat.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@NotificationsActivity,
                    StringConstants.slidepop_notif_chat,
                    Toast.LENGTH_SHORT
                ).show()
            }
            FirebaseUtils.logEvents(this@NotificationsActivity, StringConstants.slidepop_notif_chat)
            val intent = Intent(
                this@NotificationsActivity,
                ChatUserList::class.java
            )
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@NotificationsActivity,
                    StringConstants.slidepop_notif_login,
                    Toast.LENGTH_SHORT
                ).show()
            }
            FirebaseUtils.logEvents(
                this@NotificationsActivity,
                StringConstants.slidepop_notif_login
            )
            val intent = Intent(
                this@NotificationsActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.notificationList.addItemDecoration(
            MarginDecoration(
                applicationContext
            )
        )
        binding.notificationChatList.addItemDecoration(
            MarginDecoration(
                applicationContext
            )
        )
    }

    private fun getNotifications() {
        dataList = AppUtils.getNotificationsLocally(this)

        if (dataList.isNotEmpty()) {
            binding.tvCommunityEmptyMsg.visibility = View.GONE
            // initialize the adapter,
            // and pass the required argument
            rvAdapter = NotificationAdapter(dataList, this@NotificationsActivity, false)
            // attach adapter to the recycler view
            binding.notificationList.adapter = rvAdapter
        } else {
            binding.tvCommunityEmptyMsg.visibility = View.VISIBLE
        }
    }

    private fun getChatNotifications() {
        chatDataList = AppUtils.getChatNotificationsLocally(this)

        if (chatDataList.isNotEmpty()) {
            binding.tvChatEmptyMsg.visibility = View.GONE
            // initialize the adapter,
            // and pass the required argument
            rvChatAdapter = NotificationAdapter(chatDataList, this, true)
            // attach adapter to the recycler view
            binding.notificationChatList.adapter = rvChatAdapter
        } else {
            binding.tvChatEmptyMsg.visibility = View.VISIBLE
        }
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

    override fun onItemClicked(item: NotificationModel, isChatItems: Boolean) {
        if (isChatItems) {

            val _intent = Intent(
                this@NotificationsActivity,
                ChatActivity::class.java
            )
            _intent.putExtra("userUid", item.userId)
            startActivity(_intent)

//            val intent = Intent(
//                this@NotificationsActivity,
//                ChatActivity::class.java
//            )
//            val userData: String = _gson.toJson(_user_list.get(view.getTag() as Int))
//            Log.e("TAG", "Converted Data $userData")
//            intent.putExtra("selected_user", userData)
//            startActivity(intent)

        } else {
            val bundle = Bundle()
            bundle.putString("post_id", item.postId)

            val intent = Intent(
                this,
                ShowPostFromNotification::class.java
            )
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}