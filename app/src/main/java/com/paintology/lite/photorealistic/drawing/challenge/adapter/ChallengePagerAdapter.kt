package com.paintology.lite.photorealistic.drawing.challenge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Youtube.player.AbstractYouTubePlayerListener
import com.paintology.lite.photorealistic.drawing.Youtube.player.YouTubePlayer
import com.paintology.lite.photorealistic.drawing.challenge.TutorialChallengeMode
import com.paintology.lite.photorealistic.drawing.challenge.diff.ChallengeDiffUtil
import com.paintology.lite.photorealistic.drawing.challenge.enums.ChallengeEvent
import com.paintology.lite.photorealistic.drawing.challenge.utils.APP_SCREEN
import com.paintology.lite.photorealistic.drawing.challenge.utils.extractVideoIdFromUrl
import com.paintology.lite.photorealistic.drawing.challenge.utils.getCategoryIcon
import com.paintology.lite.photorealistic.drawing.challenge.utils.getChallengeTitle
import com.paintology.lite.photorealistic.drawing.challenge.utils.getDifficultyType
import com.paintology.lite.photorealistic.drawing.challenge.utils.isQuiz
import com.paintology.lite.photorealistic.drawing.challenge.utils.isResource
import com.paintology.lite.photorealistic.drawing.challenge.utils.isTutorials
import com.paintology.lite.photorealistic.drawing.challenge.utils.loadImage
import com.paintology.lite.photorealistic.drawing.challenge.utils.toFormattedNumber
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.addChallengeComment
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.getChallengeComments
import com.paintology.lite.photorealistic.drawing.databinding.ChallengeItemsBinding
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.show


class ChallengePagerAdapter(
    val callback: (ChallengeEvent) -> Unit
) : RecyclerView.Adapter<ChallengePagerAdapter.ViewPagerViewHolder>() {

    private var list: ArrayList<TutorialChallengeMode> = arrayListOf()

    fun getItemList() = list
    override fun getItemCount(): Int = list.size

    private var isViewAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = ChallengeItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(list[position])
        holder.binding.flipView.tag = "CardFlipView${position}"
    }

    fun setData(newList: ArrayList<TutorialChallengeMode>) {
        val diffResult = DiffUtil.calculateDiff(
            ChallengeDiffUtil(list, newList),
            true
        )
        list = ArrayList(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewPagerViewHolder(val binding: ChallengeItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewFront.ivDifficultyIcon.setOnClickListener {
                callback.invoke(ChallengeEvent.OnLevelMeterClick)
            }

            binding.viewFront.btnDetail.setOnClickListener {
                val challenge = list[adapterPosition]
                callback.invoke(ChallengeEvent.OnDetailClick(challenge))
                binding.flipView.flipTheView(true)
            }

            binding.viewFront.btnPeople.setOnClickListener {
                callback.invoke(ChallengeEvent.OnGalleryClick)
            }
            binding.viewFront.imgLike.setOnClickListener {
                val challenge = list[adapterPosition]
                callback.invoke(ChallengeEvent.OnLikeClick(challenge, pos = adapterPosition))
            }
            binding.viewFront.imgPlay.setOnClickListener {
                val challenge = list[adapterPosition]
                val guide = challenge.custom_fields?.tutorial_data?.guide
                KGlobal.openInBrowser(binding.root.context, guide)
            }

            binding.viewBack.btnArrow.setOnClickListener {
                val challenge = list[adapterPosition]
                callback.invoke(ChallengeEvent.OnOpenTutorial(challenge))
            }

            binding.viewFront.imgChat.setOnClickListener {
                binding.flipView.flipTheView(true)
            }

        }

        fun setData(challenge: TutorialChallengeMode) {
            try {
                if (!isViewAdded) {
                    isViewAdded = true
                    val viewData = list[adapterPosition]
                    FirebaseFirestoreApi.addView(viewData.key ?: "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.viewFront.tvChallengeTitle.text = challenge.getChallengeTitle()
            // set challenge difficulty icon
            if (challenge.type.isResource()) {
                binding.viewFront.ivDifficultyIcon.hide()
            } else {
                val resId = challenge.difficulty?.getDifficultyType() ?: -1
                binding.viewFront.ivDifficultyIcon.loadImage(resId)
            }

            binding.viewFront.ivCategoryIcon.loadImage(challenge.getCategoryIcon())
            binding.viewFront.tvChallengeReward.text = challenge.points.toString()

            binding.viewFront.challengeImage.loadImage(challenge.images?.banner)


            binding.viewFront.tvViews.text = challenge.statistic.views.toFormattedNumber()
            binding.viewFront.tvLikes.text = challenge.statistic.likes.toFormattedNumber()
            binding.viewFront.tvChats.text = challenge.statistic.comments.toFormattedNumber()

            if (challenge.type.isQuiz()) {
                binding.viewFront.statisticsLayout.show()
            }

            if (challenge.custom_fields?.links?.firstOrNull()?.target != "app_screen") {
                binding.viewFront.statisticsLayout.show()
            } else {
                binding.viewFront.statisticsLayout.show()
            }

            if (challenge.type.isQuiz()) {
                binding.viewFront.imgPlay.hide()
                binding.viewFront.tvPlay.hide()
                binding.viewBack.tvChallengeTitle.text =
                    challenge.custom_fields?.questions?.firstOrNull()?.question
                binding.viewBack.recyclerViewComments.hide()
                binding.viewBack.commentEditText.hide()
                binding.viewBack.commentsTitle.hide()
                binding.viewBack.addCommentButton.hide()
                binding.viewBack.tvDescription.text =
                    challenge.custom_fields?.questions?.firstOrNull()?.question
            } else {
                binding.viewFront.imgPlay.show()
                binding.viewFront.tvPlay.show()
                binding.viewBack.tvChallengeTitle.text = challenge.description
                binding.viewBack.recyclerViewComments.show()
                binding.viewBack.commentEditText.show()
                binding.viewBack.commentsTitle.show()
                binding.viewBack.addCommentButton.show()
                binding.viewBack.tvDescription.text = challenge.description
            }

            if (challenge.custom_fields?.links?.firstOrNull()?.target == APP_SCREEN) {
                binding.viewBack.btnArrow.show()
            } else {
                binding.viewBack.btnArrow.visibility = View.INVISIBLE
            }

            if (challenge.custom_fields?.links?.firstOrNull()?.target != APP_SCREEN) {
                binding.viewFront.btnDetail.show()
            } else {
                binding.viewFront.btnDetail.hide()
            }
            var challengeYouTubePlayer: YouTubePlayer? = null

            if (challenge.type.isTutorials()) {
                binding.viewFront.btnPeople.show()
                val youtube = challenge.custom_fields?.tutorial_data?.youtube
                val guide = challenge.custom_fields?.tutorial_data?.guide
                youtube?.extractVideoIdFromUrl()?.let { videoId ->
                    binding.viewBack.youtubePlayerView.initialize({ youTubePlayer ->
                        youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady() {
                                challengeYouTubePlayer = youTubePlayer
                                challengeYouTubePlayer!!.loadVideo(videoId, 0f)
                                challengeYouTubePlayer!!.pause()
                            }
                        })
                    }, true)
                }
                binding.viewBack.btnLink.setOnClickListener {
                    KGlobal.openInBrowser(binding.root.context, guide)
                }

                if (youtube.isNullOrEmpty()) {
                    binding.viewBack.btnLink.isVisible = false
                    binding.viewBack.youtubePlayerView.isVisible = true
                } else if (!guide.isNullOrEmpty()) {
                    binding.viewBack.btnLink.isVisible = true
                } else {
                    binding.viewBack.btnLink.isVisible = false
                    binding.viewBack.thumbnailOverlay.isVisible = false
                    binding.viewBack.youtubePlayerView.isVisible = false
                }
            } else {
                binding.viewFront.btnPeople.hide()
            }

            binding.viewBack.challengeImageBack.loadImage(challenge.images?.banner)

            if (challenge.type.isResource()) {
                challenge.custom_fields?.links?.firstOrNull()?.value?.let { link ->
                    binding.viewBack.btnLink.setOnClickListener {
                        KGlobal.openInBrowser(binding.root.context, link)
                    }
                    if (link.contains("youtube")) {
                        binding.viewBack.btnLink.isVisible = false
                        binding.viewBack.youtubePlayerView.isVisible = true
                        link.extractVideoIdFromUrl()?.let { videoId ->
                            binding.viewBack.youtubePlayerView.initialize({ youTubePlayer ->
                                youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                                    override fun onReady() {
                                        challengeYouTubePlayer = youTubePlayer
                                        challengeYouTubePlayer!!.loadVideo(videoId, 0f)
                                        challengeYouTubePlayer!!.pause()
                                    }
                                })
                            }, true)
                        }
                    } else if (link.isNotEmpty()) {
                        binding.viewBack.youtubePlayerView.isVisible = false
                        binding.viewBack.btnLink.isVisible = true
                    } else {
                        binding.viewBack.btnLink.isVisible = false
                        binding.viewBack.thumbnailOverlay.isVisible = false
                        binding.viewBack.youtubePlayerView.isVisible = false
                    }
                }
            } else {
                binding.viewBack.btnLink.isVisible = false
                binding.viewBack.thumbnailOverlay.isVisible = false
                binding.viewBack.youtubePlayerView.isVisible = false
            }

            binding.viewBack.addCommentButton.setOnClickListener {
                val comment = binding.viewBack.commentEditText.text.toString()
                addChallengeComment(comment, challenge.key ?: "",
                    before = {
                        binding.viewBack.addCommentButton.isEnabled = false
                        binding.viewBack.commentEditText.text = null
                        binding.viewBack.commentEditText.clearFocus()
                    }, after = {
                        binding.viewBack.addCommentButton.isEnabled = true
                    }, onSuccess = {
                        getChallengeComments(challenge.key ?: "") { comments ->
                            binding.viewBack.recyclerViewComments.adapter =
                                CommentsAdapter(comments)
                        }
                    })
            }

            binding.flipView.setOnFlipListener { _, _ ->
                challengeYouTubePlayer?.pause()
            }
            binding.viewBack.recyclerViewComments.adapter = CommentsAdapter(challenge.comments)

            if (challenge.custom_fields?.tutorial_data != null) {
                binding.viewBack.btnArrow.show()
            } else {
                binding.viewBack.btnArrow.hide()
            }

        }


    }
}