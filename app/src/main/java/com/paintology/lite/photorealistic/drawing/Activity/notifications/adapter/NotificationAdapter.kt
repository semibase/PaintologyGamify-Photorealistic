package com.paintology.lite.photorealistic.drawing.Activity.notifications.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.Notification
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.SwipeToDeleteDelegate
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ISwipeToDeleteAdapter
import com.paintology.lite.photorealistic.drawing.Activity.notifications.swipe2delete.interfaces.ISwipeToDeleteHolder
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutNotificationsItemBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class NotificationAdapter(
    val context: Context,
    private var notificationList: MutableList<Notification>,
    private var notifyList: MutableList<String>,
    private val onNotificationClick: OnNotificationClick,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>(),
    ISwipeToDeleteAdapter<Int, Notification, NotificationAdapter.ViewHolder> {

    var isFirst = true
    var height = 0;
    var width = 0
    val swipeToDeleteDelegate =
        SwipeToDeleteDelegate(items = notificationList, swipeToDeleteAdapter = this)
    var bottomContainer = true


    override fun findItemPositionByKey(key: String) =
        notificationList.indexOfFirst { it.id == key }

    fun refresh(model: Notification, position: Int) {
        notificationList[position] = model
        notifyItemChanged(position)
    }

    fun addAll(notificationList: List<Notification>, notifyList: MutableList<String>) {
        this.notificationList.addAll(notificationList)
        this.notifyList = notifyList;
        notifyDataSetChanged()
    }

    fun addItem(item: Notification) {
        notificationList.add(0, item)
        notifyList.add(0, item.id)
        notifyItemInserted(0)
    }

    override fun onBindPendingItem(
        holder: ViewHolder,
        key: String,
        item: Notification,
        position: Int
    ) {


        holder.binding.itemCl.visibility = View.GONE
        holder.binding.undoContainer.visibility = View.VISIBLE

        holder.binding.undoContainer.layoutParams = FrameLayout.LayoutParams(
            width,
            height
        )
        if (bottomContainer) {
            holder.apply {
                holder.binding.includedSwipe.btnUndo.setOnClickListener {
                    swipeToDeleteDelegate.onUndo(key)
                }
                holder.binding.includedSwipe.btnDelete.setOnClickListener {
                    removeItem(key)
                    val bundle = Bundle()
                    bundle.putString("notification_id", key)
                    context.sendUserEventWithParam(StringConstants.notifications_delete, bundle)

                    val firestore = FirebaseFirestore.getInstance()
                    val notificationRef = firestore.collection("users")
                        .document(
                            StringConstants.constants.getString(
                                StringConstants.constants.UserId,
                                context
                            )
                        )
                        .collection("notifications")
                        .document(key)
                    notificationRef.update("delete", true)
                        .addOnCompleteListener { }
                }
            }
        }

    }

    override fun removeItem(key: String) {
        swipeToDeleteDelegate.removeItem(key)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutNotificationsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        swipeToDeleteDelegate.onBindViewHolder(holder, notificationList[position].id, position)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindCommonItem(
        holder: ViewHolder,
        key: String,
        item: Notification,
        position: Int
    ) {

        val itemm = notificationList[position]

        holder.binding.itemCl.visibility = View.VISIBLE
        holder.binding.undoContainer.visibility = View.GONE

        with(holder.binding) {

            tvNotificationTitle.text = itemm.title
            tvNotificationContent.text = itemm.body
            tvTime.text = itemm.created_at?.toDate().toString()
            tvPostType.text = itemm.data.target_type

            if (itemm.data.target_type == "followers" && !itemm.readFromApp) {
                btnDecline.show()
                btnAccept.show()
            } else {
                btnDecline.hide()
                btnAccept.hide()
            }

            when (itemm.data.target_type) {
                "followers" -> {
                    if (btnAccept.visibility == View.VISIBLE) {
                        imgNotification.setImageResource(R.drawable.img_frnd_req)
                        btnDecline.onSingleClick {
                            onNotificationClick.onClick(itemm, position, "decline")
                        }

                        btnAccept.apply {
                            text = "Follow Back"
                            // text = "Follow Back"
                            onSingleClick {
                                itemm.isAccepted = true
                                onNotificationClick.onClick(itemm, position, "accept")
                            }
                        }
                    } else {
                        itemCl.apply {
                            onSingleClick {
                                onNotificationClick.onItemClick(itemm, position)
                            }
                        }
                    }
                }

                else -> {
                    itemCl.apply {
                        onSingleClick {
                            onNotificationClick.onItemClick(itemm, position)
                        }
                    }
                }
            }
        }
        if (isFirst && notificationList.size > 0) {
            isFirst = false
            holder.binding.itemCl.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    holder.binding.itemCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    height = holder.binding.itemCl.height
                    width = holder.binding.itemCl.width
                }
            })
        }


    }


    inner class ViewHolder(val binding: LayoutNotificationsItemBinding) :
        RecyclerView.ViewHolder(binding.root), ISwipeToDeleteHolder<Int> {
        override val topContainer: View
            get() =
                if (pendingDelete && bottomContainer)
                    binding.undoContainer
                else binding.itemCl
        override var key: String = ""
        override var pendingDelete: Boolean = false
    }

    interface OnNotificationClick {

        fun onItemClick(model: Notification, position: Int)
        fun onClick(model: Notification, position: Int, action: String)
        fun onSwipe(model: Notification, position: Int, action: String)
    }


}
