package com.paintology.lite.photorealistic.drawing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.paintology.lite.photorealistic.drawing.Activity.profile.UserProfileActivity
import com.paintology.lite.photorealistic.drawing.Adapter.ViewFollowersAdapter
import com.paintology.lite.photorealistic.drawing.Adapter.ViewFollowersAdapter.ItemClickListener
import com.paintology.lite.photorealistic.drawing.Chat.Firebase_User
import com.paintology.lite.photorealistic.drawing.Model.Follower
import com.paintology.lite.photorealistic.drawing.Model.Following
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityFollowersFollowingBinding
import com.paintology.lite.photorealistic.drawing.util.ChatUtils
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class FollowersFollowingActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityFollowersFollowingBinding
    private var constants: StringConstants = StringConstants()
    private var lstFollowers: ArrayList<Follower> = ArrayList()
    private var lstFollowing: ArrayList<Following> = ArrayList()
    private var adapter: ViewFollowersAdapter? = null
    var userID: String = ""
    var userName: String = ""
    private var fromOtherUserProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFollowersFollowingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        userID = intent.getStringExtra("data") ?: constants.getString(constants.UserId, this)

        if (intent.getStringExtra("user_name") != null) {
            userName = intent.getStringExtra("user_name").toString()

        }

        val fromFollowers = intent.getBooleanExtra("from_followers", true)
        fromOtherUserProfile = intent.getBooleanExtra("fromOtherUserProfile", true)


        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUserStatusUpdateListener()

        val listener: ItemClickListener = object : ItemClickListener {
            override fun viewItemClicked(view: View) {
                val userId = if (fromFollowers) {
                    lstFollowers[view.tag as Int].userID
                } else {
                    lstFollowing[view.tag as Int].userID
                }

                val id: String =
                    constants.getString(constants.UserId, this@FollowersFollowingActivity)
                        .toString() ?: ""

                if (id.equals(userId)) {
                    FireUtils.openProfileScreen(this@FollowersFollowingActivity, null)
                } else {
                    val _intent = Intent(
                        this@FollowersFollowingActivity,
                        UserProfileActivity::class.java
                    )
                    _intent.putExtra(StringConstants.SelectedUserId, userId)
                    _intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(_intent)
                }
            }

            override fun chatMenuClicked(view: View, userID: String, userName: String) {
                ChatUtils(this@FollowersFollowingActivity).openChatScreen(userID, userName)
            }

            override fun unfollowMenuClicked(
                view: View?,
                userID: String,
                userName: String,
                isFromFollowers: Boolean,
                following: Following,
                position: Int
            ) {
                showUnfollowDialog(userID, userName, isFromFollowers, following, position)
            }
        }


        try {
            if (fromFollowers) {
                FirebaseFirestoreApi.getFollowers(userID).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result.documents
                        for (doc in documents) {
                            val follower = Follower()
                            follower.username = doc.getString("name") ?: ""
                            follower.profilePic = doc.getString("avatar") ?: ""
                            follower.userID = doc.getString("user_id") ?: ""
                            follower.isOnline = false
                            lstFollowers.add(follower)
                        }
                        title = String.format("Followers (%d)", lstFollowers.size)
                        setData(listener, fromFollowers)
                    }
                }
            } else {
                FirebaseFirestoreApi.getFollowings(userID).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result.documents
                        for (doc in documents) {
                            val following = Following()
                            following.username = doc.getString("name") ?: ""
                            following.profilePic = doc.getString("avatar") ?: ""
                            following.userID = doc.getString("user_id") ?: ""
                            following.isOnline = false
                            lstFollowing.add(following)
                        }
                        title = String.format("Following (%d)", lstFollowing.size)
                        setData(listener, fromFollowers)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setData(listener: ItemClickListener, fromFollowers: Boolean) {

        if (fromFollowers) {
            if (lstFollowers.isEmpty()) {
                mBinding.layoutNoData.visibility = View.VISIBLE
                mBinding.EmptyDataImg.drawable

                mBinding.EmptyDataImg.setImageResource(R.drawable.no_user_fav)
                mBinding.HeadingTxt.text = "Not Following Anyone"
                mBinding.DescriptionTxt.text = userName + " hasn't followed anyone yet."

            }
        } else {
            if (lstFollowing.isEmpty()) {
                mBinding.layoutNoData.visibility = View.VISIBLE
                mBinding.EmptyDataImg.drawable

                mBinding.EmptyDataImg.setImageResource(R.drawable.no_user_fav)
                mBinding.HeadingTxt.text = "No Followers Yet"
                mBinding.DescriptionTxt.text = userName + " doesn't have any followers yet."
            }


        }

        adapter = ViewFollowersAdapter(
            this@FollowersFollowingActivity,
            lstFollowers,
            listener,
            lstFollowing,
            fromFollowers,
            fromOtherUserProfile
        )

        mBinding.list.layoutManager = LinearLayoutManager(this@FollowersFollowingActivity)
        mBinding.list.adapter = adapter
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

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.value != null) {
                val user = snapshot.getValue(Firebase_User::class.java)

                if (user != null) {
                    var userFound = false
                    for (follower in lstFollowers) {
                        if (follower.userID.equals(user.user_id)) {
                            val index = lstFollowers.indexOf(follower)
                            follower.isOnline = user.is_online == "true"
                            follower.profilePic = user.profile_pic
                            follower.username = user.user_name
                            lstFollowers[index] = follower
                            adapter?.notifyItemChanged(index)
                            userFound = true
                            break
                        }
                    }

                    if (!userFound) {
                        for (following in lstFollowing) {
                            if (following.userID.equals(user.user_id)) {
                                val index = lstFollowing.indexOf(following)
                                following.isOnline = user.is_online == "true"
                                following.profilePic = user.profile_pic
                                following.username = user.user_name
                                lstFollowing[index] = following
                                adapter?.notifyItemChanged(index)
                                break
                            }
                        }
                    }
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    private fun setUserStatusUpdateListener() {
        FirebaseDatabase.getInstance()
            .getReference(StringConstants().firebase_user_list)
            .addChildEventListener(childEventListener)
    }

    private fun removeUserStatusUpdateListener() {
        FirebaseDatabase.getInstance()
            .getReference(StringConstants().firebase_user_list)
            .removeEventListener(childEventListener)
    }

    override fun onDestroy() {
        removeUserStatusUpdateListener()
        super.onDestroy()
    }

    fun showUnfollowDialog(
        userID: String,
        Username: String,
        isFromFollowers: Boolean,
        following: Following,
        position: Int
    ) {
        val dialog = Dialog(this@FollowersFollowingActivity)
        dialog.setContentView(R.layout.unfollow_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tv_msg = dialog.findViewById<View>(R.id.tv_msg) as TextView
        val tv_cancle = dialog.findViewById<View>(R.id.btn_cancel) as TextView
        val tv_unfollow = dialog.findViewById<View>(R.id.btn_unfollow) as TextView

        tv_msg.text = "Do you wan't to Unfollow $Username ?"

        tv_cancle.setOnClickListener { dialog.dismiss() }

        tv_unfollow.setOnClickListener {
            dialog.setOnDismissListener {
                FireUtils.showProgressDialog(
                    this@FollowersFollowingActivity,
                    "UnFollowing Please Wait..."
                )
                val bundle = Bundle()
                bundle.putString("user_id", userID)
                sendUserEventWithParam(StringConstants.user_follow, bundle)
                FirebaseFirestoreApi.unfollowUser(userID).addOnCompleteListener {
                    FireUtils.hideProgressDialog()
                    if (it.isSuccessful) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@FollowersFollowingActivity,
                                constants.user_profile_unfollow_success,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@FollowersFollowingActivity,
                            constants.user_profile_unfollow_success
                        )
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@FollowersFollowingActivity,
                                constants.user_profile_unfollow_fail,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@FollowersFollowingActivity,
                            constants.user_profile_unfollow_fail
                        )
                    }
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}