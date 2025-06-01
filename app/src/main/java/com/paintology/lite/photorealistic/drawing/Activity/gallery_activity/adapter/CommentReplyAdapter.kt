package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.Comment
import com.paintology.lite.photorealistic.drawing.Activity.utils.getUserProfileData
import com.paintology.lite.photorealistic.drawing.databinding.LayoutCommentReplyBinding

class CommentReplyAdapter : RecyclerView.Adapter<CommentReplyAdapter.ViewHolder>() {

    private val commentReplyList: MutableList<Comment> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutCommentReplyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = commentReplyList[position]

        getUserProfileData(item.userId, holder.binding.ivReplyProfilePic, holder.binding.imgCountry)

        holder.binding.apply {

            tvReplyUserName.text = item.name
            tvReplyContent.text = item.comment

            // Format createdAt timestamp using getTimeAgo utility function
            val createdAtTime = item.createdAt.toDate().time
            tvReplyCommentTime.text = getTimeAgo(createdAtTime)


        }

    }

    private fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCommentReplyData(newCommentList: List<Comment>) {
        commentReplyList.clear()
        commentReplyList.addAll(newCommentList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return commentReplyList.size
    }

    inner class ViewHolder(val binding: LayoutCommentReplyBinding) :
        RecyclerView.ViewHolder(binding.root)


}