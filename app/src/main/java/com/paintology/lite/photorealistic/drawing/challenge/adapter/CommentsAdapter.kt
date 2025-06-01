package com.paintology.lite.photorealistic.drawing.challenge.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.paintology.lite.photorealistic.drawing.challenge.model.Comment
import com.paintology.lite.photorealistic.drawing.challenge.utils.loadImageProfile
import com.paintology.lite.photorealistic.drawing.databinding.ChallengeCommentItemLayoutBinding

class CommentsAdapter(comments: MutableList<Comment>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var comments: MutableList<Comment> = mutableListOf()

    init {
        this.comments = comments

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChallengeCommentItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    class ViewHolder(val binding: ChallengeCommentItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentText.text = comment.comment
            binding.commentTime.text = comment.created_at.formattedTime()
            binding.userName.text = comment.name.ifEmpty { "Guest User" }
            binding.userAvatar.loadImageProfile(comment.avatar)
        }
    }

}

private fun Timestamp.formattedTime(): String {
    val currentTime = System.currentTimeMillis()
    val timeInMillis = this.toDate().time
    return DateUtils.getRelativeTimeSpanString(timeInMillis, currentTime, DateUtils.MINUTE_IN_MILLIS).toString()
}
