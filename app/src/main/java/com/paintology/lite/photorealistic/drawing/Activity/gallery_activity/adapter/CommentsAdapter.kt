package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.Comment
import com.paintology.lite.photorealistic.drawing.Activity.utils.getUserOnlineStatus
import com.paintology.lite.photorealistic.drawing.Activity.utils.getUserProfileData
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutCommentsBinding


class CommentsAdapter(
    private val onReplyToComment: OnReplyToComment
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val commentList: MutableList<Comment> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutCommentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = commentList[position]

        getUserProfileData(item.userId, holder.binding.ivProfilePic,holder.binding.imgCountry)

        with(holder.binding) {

            imgUserActiveStatus.getUserOnlineStatus(item.userId)

            tvUserName.text = item.name
            tvComment.text = item.comment

            tvCommentTime.text = item.createdAt.toString()

            // Format createdAt timestamp using getTimeAgo utility function
            val createdAtTime = item.createdAt.toDate().time
            tvCommentTime.text = getTimeAgo(createdAtTime)

            // Set up the replies RecyclerView
            val repliesAdapter = CommentReplyAdapter()
            rvReplies.layoutManager = LinearLayoutManager(holder.binding.root.context)
            rvReplies.adapter = repliesAdapter
            repliesAdapter.setCommentReplyData(item.replies)

            if (item.replies.isEmpty()) {
                layoutViewReply.hide()
            } else {
                layoutViewReply.show()
            }

            // Toggle replies visibility
            layoutViewReply.onSingleClick {
                if (rvReplies.visibility == RecyclerView.GONE) {
                    rvReplies.visibility = RecyclerView.VISIBLE
                    tvViewReply.text = "Hide Replies"
                    imgArrow.setImageResource(R.drawable.arrow_up)
                } else {
                    imgArrow.setImageResource(R.drawable.arrow_down)
                    rvReplies.visibility = RecyclerView.GONE
                    tvViewReply.text = "View ${item.replies.size} Replies"
                }
            }

            // Reply to a comment
            tvReply.onSingleClick {
                onReplyToComment.replyToComment(item, position)
            }
        }
    }

    private fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString()
    }


    override fun getItemCount(): Int {
        return commentList.size
    }

    // Function to set new data and notify the adapter
    fun setData(newCommentList: List<Comment>) {
        commentList.clear()
        commentList.addAll(newCommentList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: LayoutCommentsBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnReplyToComment {
        fun replyToComment(model: Comment, position: Int)
    }

}