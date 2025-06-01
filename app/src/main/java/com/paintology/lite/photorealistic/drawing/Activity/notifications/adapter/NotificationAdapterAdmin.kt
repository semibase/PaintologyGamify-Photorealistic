package com.paintology.lite.photorealistic.drawing.Activity.notifications.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.NotificationAdmin
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.databinding.LayoutNotificationsItemAdminBinding

class NotificationAdapterAdmin(
    private val notificationList: List<NotificationAdmin>,
    private val onNotificationClick: OnNotificationClick,
) : RecyclerView.Adapter<NotificationAdapterAdmin.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutNotificationsItemAdminBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificationList[position]

        with(holder.binding) {
            tvNotificationTitle.text = item.title
            tvNotificationContent.text = item.body
            tvTime.text = item.created_at?.toDate().toString()

            itemCl.onSingleClick {
                onNotificationClick.onItemClick(item, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class ViewHolder(val binding: LayoutNotificationsItemAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnNotificationClick {
        fun onItemClick(model: NotificationAdmin, position: Int)
    }
}
