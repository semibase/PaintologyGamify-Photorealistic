package com.paintology.lite.photorealistic.drawing.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Model.NotificationModel
import com.paintology.lite.photorealistic.drawing.databinding.NotificationItemBinding

class NotificationAdapter(
    private val notifications: List<NotificationModel>,
    val itemClickListener: OnItemClickListener,
    val isChatItems: Boolean
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(notifications[position]) {
                binding.title.text = this.title
                binding.title.visibility = View.GONE
                binding.subTitle.text = this.text
                binding.root.setOnClickListener {
                    itemClickListener.onItemClicked(
                        this,
                        isChatItems
                    )
                }
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return notifications.size
    }

    interface OnItemClickListener {
        fun onItemClicked(item: NotificationModel, isChatItems: Boolean)
    }
}