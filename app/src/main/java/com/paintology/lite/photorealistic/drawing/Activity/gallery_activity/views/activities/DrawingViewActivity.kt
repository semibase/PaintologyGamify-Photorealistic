package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities


import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.Lists.challengeLevelList
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.CommentsAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.TagAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.Comment
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.Constants_Gallery
import com.paintology.lite.photorealistic.drawing.Activity.utils.getUserOnlineStatus
import com.paintology.lite.photorealistic.drawing.Activity.utils.getUserProfileData
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.hideKeyboard
import com.paintology.lite.photorealistic.drawing.Activity.utils.invisible
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick1
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToastRelease
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivityWithRank
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.challenge.ChallengeLevelsModel
import com.paintology.lite.photorealistic.drawing.challenge.adapter.ChallengeLevelsAdapter
import com.paintology.lite.photorealistic.drawing.challenge.adapter.LevelClick
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.countDrawingView
import com.paintology.lite.photorealistic.drawing.databinding.ActivityDrawingViewBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetChallengeBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.SpecingDecoration
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import kotlin.collections.set


class DrawingViewActivity : BaseActivity(), LevelClick, CommentsAdapter.OnReplyToComment {

    private val binding by lazy {
        ActivityDrawingViewBinding.inflate(layoutInflater)
    }
    private var commentsAdapter: CommentsAdapter? = null
    private var getModel: NewDrawing? = null
    private var rate = 5f
    private var challengeLevelsAdapter: ChallengeLevelsAdapter? = null
    private var constants: StringConstants = StringConstants()

    private var progressDialog: ProgressDialog? = null


    private var userLevel = "Beginner 1"
    private var isReplyToComment = false
    private var commentParentId: String = ""

    val TAG = "TAG_Drawing"

    private var auth: FirebaseAuth? = null

    private lateinit var drawingRepository: DrawingRepository
    private var hasRatedLocally = false
    private lateinit var bottomSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        drawingRepository = DrawingRepository(this)


        viewModel.fetchDrawingListWithFacet()

        initProgressBar()

        getIntentData()

        countDrawingView()

        initRecyclerView()

        fetchComments()

//        fetchDrawing()

        initToolbar()


    }

    private fun initProgressBar() {
        progressDialog = ProgressDialog(this)

        progressDialog?.setTitle(resources.getString(R.string.please_wait))
        progressDialog?.setMessage(getString(R.string.load_tut))
        progressDialog?.setCanceledOnTouchOutside(false)
    }

    private fun countDrawingView() {
        getModel?.id.toString().countDrawingView()
    }

    private fun fetchComments() {
        FirebaseFirestoreApi.fetchComments(getModel?.id.toString())
            .addOnSuccessListener { querySnapshot ->
                val commentsList = mutableListOf<Comment>()
                val commentsMap =
                    mutableMapOf<String, MutableList<Comment>>() // Map to store comments and their replies

                Log.d(TAG, "Fetched ${querySnapshot.documents.size} documents")

                for (document in querySnapshot.documents) {
                    val id = document.id
                    val avatar = document.getString("avatar") ?: ""
                    val commentText = document.getString("comment") ?: ""
                    val country = document.getString("country") ?: ""
                    val createdAt = document.getTimestamp("created_at") ?: Timestamp(Date())
                    val gender = document.getString("gender") ?: ""
                    val name = document.getString("name") ?: ""
                    val parentId = document.getString("parent_id") ?: ""
                    val userId = document.getString("user_id") ?: ""

                    val comment = Comment(
                        id,
                        avatar,
                        commentText,
                        country,
                        createdAt,
                        gender,
                        name,
                        userId,
                        parentId
                    )

                    Log.e(
                        TAG,
                        "Comment ID: $id, Parent ID: $parentId, User ID: $userId, Comment: $commentText"
                    )

                    // Check if it's a reply and add to its parent's list of replies
                    if (parentId.isNotEmpty()) {
                        if (!commentsMap.containsKey(parentId)) {
                            commentsMap[parentId] = mutableListOf()
                        }
                        commentsMap[parentId]?.add(comment)
                        Log.e(TAG, "Added reply to parent ID: $parentId")
                    } else {
                        // It's a main comment
                        commentsList.add(comment)
                        Log.e(TAG, "Added main comment ID: $id")
                    }
                }

                // Assign replies to their respective parent comments
                for (comment in commentsList) {
                    comment.replies = commentsMap[comment.id] ?: emptyList()
                    Log.e(
                        TAG,
                        "Assigned ${comment.replies.size} replies to comment ID: ${comment.id}"
                    )
                }

                // Set up your main comments RecyclerView adapter
                commentsAdapter?.setData(commentsList)
                Log.e(TAG, "Set data to commentsAdapter with ${commentsList.size} main comments")

                binding.apply {
                    btnComment.show()
                    progress.hide()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "fetchComments: $exception")
            }
    }


    // this show all comment
    /*    private fun fetchComments() {
            FirebaseFirestoreApi.fetchComments(getModel?.id.toString())
                .addOnSuccessListener { querySnapshot ->
                    commentsList.clear()
                    for (document in querySnapshot) {
                        val id = document.getString("ID") ?: ""
                        val avatar = document.getString("avatar") ?: ""
                        val commentText = document.getString("comment") ?: ""
                        val createdAt = document.getTimestamp("created_at")
                        val email = document.getString("email") ?: ""
                        val level = document.getString("level") ?: ""
                        val name = document.getString("name") ?: ""
                        val userId = document.getString("user_id") ?: ""

                        val comment =
                            Comment(id, avatar, commentText, createdAt!!, email, level, name, userId)
                        commentsList.add(comment)
                        Log.e(TAG, "fetchComments: $comment", )
                    }
                    commentsAdapter?.setData(commentsList)
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    // For simplicity, log the error
                    Log.e(TAG, "fetchComments: $exception")
                }
        }*/


    @SuppressLint("SetTextI18n")
    private fun getIntentData() {
        val intent = intent

        getModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("drawing_model", NewDrawing::class.java)
        } else {
            intent.getParcelableExtra("drawing_model")
        }

        Log.d(TAG, "getIntentData: $getModel")

        if (getModel != null) {
            getUserProfileData(
                getModel?.author?.userId.toString(),
                binding.ivProfilePic,
                binding.imgCountry,
                binding.tvTotalPoints
            )
            binding.imgUserActiveStatus.getUserOnlineStatus(getModel?.author?.userId.toString())
        }

        // set data to views
        binding.apply {


            if (getModel?.links?.youtube?.isEmpty() == true || getModel?.links?.youtube?.endsWith("null") == true) {
                imgVideo.visibility = View.GONE
                ivYoutube?.visibility  =View.GONE
            } else {
                ivYoutube?.visibility=View.VISIBLE
                imgVideo.visibility = View.VISIBLE
                ivYoutube?.setOnClickListener {
                    try {
                        val bundle = Bundle()
                        bundle.putString("post_id", getModel?.id.toString())
                        bundle.putString("user_id", getModel?.author?.userId)
                        sendUserEventWithParam(StringConstants.gallery_post_youtube_button_click, bundle)
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                    KGlobal.openInBrowser(
                        this@DrawingViewActivity,
                        getModel?.links?.youtube.toString()
                    )
                }
                imgVideo.setOnClickListener {
                    try {
                        val bundle = Bundle()
                        bundle.putString("post_id", getModel?.id.toString())
                        bundle.putString("user_id", getModel?.author?.userId)
                        sendUserEventWithParam(StringConstants.gallery_post_youtube_button_click, bundle)
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                    KGlobal.openInBrowser(
                        this@DrawingViewActivity,
                        getModel?.links?.youtube.toString()
                    )
                }
            }

            toturialRatingBar.setOnTouchListener { v, event -> true }
            toturialRatingBar.rating = getModel?.statistic?.ratings?.toFloat()!!

            val averageRating = if (getModel?.statistic?.reviewsCount!! > 0) {
                getModel!!.statistic.ratings!!.toFloat() / getModel?.statistic?.reviewsCount!!
            } else {
                0f
            }
            val formattedAverageRating = String.format("%.1f", averageRating)


            tvAvgRating.text = formattedAverageRating

            tvTutorialContent.text = getModel?.description


            // set likes
            val likesFromModel = getModel?.statistic?.likes ?: 0
            if (likesFromModel > 0) {
                binding.tvLikes.text = likesFromModel.toString()
            } else {
                binding.tvLikes.text = "0"
            }


            val tags = getModel?.tags ?: listOf()
            if (tags.isNotEmpty()) {
                if (tags.size == 1 && tags[0] == "") {
                    binding.rvTags!!.visibility = View.GONE
                } else {
                    val layoutManager =
                        LinearLayoutManager(
                            this@DrawingViewActivity,
                            RecyclerView.HORIZONTAL,
                            false
                        )
                    val adapter = TagAdapter(this@DrawingViewActivity, tags, true)
                    binding.rvTags?.layoutManager = layoutManager
                    binding.rvTags?.adapter = adapter
                }
            } else {
                binding.rvTags!!.visibility = View.GONE
            }

            // set rating count
            if (getModel?.statistic?.ratings == 0) {
                tvRateUsCount.text = "0"
            } else {
                tvRateUsCount.text = getModel?.statistic?.reviewsCount.toString()
            }

            // check comments and set value
            if (getModel?.statistic?.comments == null) {
                tvComments.text = "0"
            } else {
                tvComments.text = getModel?.statistic?.comments.toString()
            }

            // check drawing views and set value
            if (getModel?.statistic?.views?.equals(0) == true) {
                tvViewCounts.text = "0"
            } else {
                tvViewCounts.text = getModel?.statistic?.views?.toString()
            }

            tvUserName.text = getModel?.author?.name


            btnDrawing.text = getModel?.title

            Glide.with(this@DrawingViewActivity)
                .load(getModel?.images?.content)
                .placeholder(R.drawable.feed_thumb_default)
                .error(R.drawable.feed_thumb_default)
                .into(binding.imgDrawing)

            // underline text view
            tvRank.paintFlags = tvRank.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            if (getModel?.author?.level.isNullOrEmpty()) {
                tvRank.text = Constants_Gallery.Beginner_1
            } else {
                tvRank.text = getModel?.author?.level
            }

            // setting up ranking image based on user Level
            when (tvRank.text) {
                Constants_Gallery.Beginner_1 -> {
                    imgRank.setImageResource(R.drawable.img_beginner_1)
                }

                Constants_Gallery.Beginner_2 -> {
                    imgRank.setImageResource(R.drawable.img_beginner_2)
                }

                Constants_Gallery.Beginner_3 -> {
                    imgRank.setImageResource(R.drawable.img_beginner_3)
                }

                Constants_Gallery.Intermediate_1 -> {
                    imgRank.setImageResource(R.drawable.img_intermidiate_1)
                }

                Constants_Gallery.Intermediate_2 -> {
                    imgRank.setImageResource(R.drawable.img_intermidiate_2)
                }

                Constants_Gallery.Intermediate_3 -> {
                    imgRank.setImageResource(R.drawable.img_intermidiate_3)
                }

                Constants_Gallery.Advanced_1 -> {
                    imgRank.setImageResource(R.drawable.img_advance_1)
                }

                Constants_Gallery.Advanced_2 -> {
                    imgRank.setImageResource(R.drawable.img_advance_2)
                }

                Constants_Gallery.Advanced_3 -> {
                    imgRank.setImageResource(R.drawable.img_advance_3)
                }

                Constants_Gallery.Expert -> {
                    imgRank.setImageResource(R.drawable.img_expert)
                }
            }

            // set flag according to the country


        }

        initListeners()

    }

    private fun fetchDrawing() {
        FirebaseFirestoreApi.getDrawingById(getModel?.id.toString())
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val drawing = document.toObject(NewDrawing::class.java)
                    binding.apply {
                        tvComments.text = drawing?.statistic?.comments.toString()

                        tvLikes.text = drawing?.statistic?.likes.toString()
                        tvViewCounts.text = drawing?.statistic?.views.toString()

                        toturialRatingBar.rating = drawing?.statistic?.ratings?.toFloat() ?: 0f
                        tvRateUsCount.text = drawing?.statistic?.reviewsCount.toString()

                        val averageRating = if ((drawing?.statistic?.reviewsCount ?: 0) > 0) {
                            drawing?.statistic?.ratings?.toFloat()
                                ?: (0f / drawing?.statistic?.reviewsCount!!)
                                ?: 1
                        } else {
                            0f
                        }
                        val formattedAverageRating = String.format("%.1f", averageRating)

                        tvAvgRating.text = formattedAverageRating
                    }

                    Log.d(TAG, "FetchDrawing : DocumentSnapshot data: $drawing")
                } else {
                    Log.d(TAG, "FetchDrawing : No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "FetchDrawing :getDrawingById failed with ", exception)
            }
    }


    private fun initListeners() {
        binding.apply {

            imgMenuDrawing.onSingleClick {

                imgMenuDrawing.showPopupMenu(R.menu.drawing_screen_menu) {

                    when (it.itemId) {
                        R.id.openDrawing -> {
                            val bundle = Bundle()
                            bundle.putString("post_id", getModel?.id.toString())
                            bundle.putString("user_id", getModel?.author?.userId)
                            sendUserEventWithParam(StringConstants.gallery_post_enlarge, bundle)
                            startDrawingActivity(getModel!!, DrawingFullScreenActivity::class.java)
                        }

                        R.id.shareDrawing -> {
                            val model = getModel ?: return@showPopupMenu
                            val bundle = Bundle()
                            bundle.putString("post_id", model.id)
                            bundle.putString(
                                "post_type", model.type
                            )
                            bundle.putString("user_id", model.author.userId)
                            sendUserEventWithParam(StringConstants.gallery_post_share, bundle)

                            loadImageAndSave()
                        }

                        R.id.doTutorialDrawing -> {
                            if (getModel?.type != "freehand") {

                                val model = getModel ?: return@showPopupMenu
                                val bundle = Bundle()
                                bundle.putString("post_id", model.id)
                                bundle.putString(
                                    "post_type", "tutorials"
                                )
                                bundle.putString("user_id", model.author.userId)
                                sendUserEventWithParam(
                                    StringConstants.gallery_post_do_tutorial,
                                    bundle
                                )
                                if (getModel?.metadata?.tutorialId?.isEmpty() == true) {
                                    Toast.makeText(
                                        this@DrawingViewActivity,
                                        getString(R.string.no_tut),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    openActivity(CategoryActivity::class.java)
                                } else {
                                    FireUtils.showProgressDialog(
                                        this@DrawingViewActivity,
                                        getString(R.string.please_wait)
                                    )
                                    TutorialUtils(this@DrawingViewActivity).parseTutorial(getModel?.metadata?.tutorialId.toString())
                                }
                            } else {
                                openActivity(DrawNowActivity::class.java)
                            }
                        }


                        R.id.addFav -> {
                            val model = getModel ?: return@showPopupMenu

                            val bundle = Bundle()
                            bundle.putString("post_id", model.id)
                            bundle.putString("post_type", model.type)
                            bundle.putString("user_id", model.author.userId)
                            sendUserEventWithParam(
                                StringConstants.gallery_post_add_favorite,
                                bundle
                            )
                            val newDraw:com.paintology.lite.photorealistic.drawing.Activity.favourite.NewDrawing =
                                com.paintology.lite.photorealistic.drawing.Activity.favourite.NewDrawing(
                                    model.author,
                                    model.createdAt,
                                    model.description,
                                    model.id,
                                    model.images,
                                    model.links,
                                    model.metadata,
                                    model.referenceId,
                                    model.statistic,
                                    model.tags,
                                    model.title,
                                    model.type
                                )

                            drawingRepository.insertDrawing(newDraw)
                        }

                        R.id.closeDrawing -> {
                            finish()
                        }
                    }
                }
            }

            ivProfilePic.onSingleClick {
                getModel?.let {
                    val bundle = Bundle()
                    bundle.putString("post_id", it.id)
                    bundle.putString(
                        "post_type",
                        if (it.type == "freehand") "freehand" else "tutorials"
                    )
                    bundle.putString("user_id", it.author.userId)
                    sendUserEventWithParam(StringConstants.gallery_post_open_author, bundle)
                    FireUtils.openProfileScreen(this@DrawingViewActivity, it.author.userId)
                }
            }

            tvUserName.onSingleClick {
                getModel?.let {
                    startDrawingActivity(
                        it,
                        DrawingActivity::class.java,
                        false,
                        getModel?.author?.country.toString()
                    )
                }
            }

            tvRank.onSingleClick {
                getModel?.let {
                    if (!it.author.level.isNullOrEmpty()) {
                        val bundle = Bundle()
                        bundle.putString("post_id", it.id)
                        bundle.putString(
                            "post_type",
                            if (it.type == "freehand") "freehand" else "tutorials"
                        )
                        bundle.putString("user_id", it.author.userId)
                        bundle.putString("level", it.author.level)
                        sendUserEventWithParam(
                            StringConstants.gallery_post_open_author_level,
                            bundle
                        )
                        startDrawingActivityWithRank(
                            it,
                            DrawingActivity::class.java,
                            true,
                            getModel?.type.toString(), ""
                        )
                    }
                }
            }

            btnComment.onSingleClick {

                val comment = edtComment.text.toString().trim()
                val drawingId = getModel?.id ?: ""

                if (comment.isEmpty()) {
                    // Handle case where comment is empty
                    Log.e(TAG, "Comment is empty")
                    showToastRelease(getString(R.string.c_empty))
                    return@onSingleClick
                }

                if (drawingId.isEmpty()) {
                    // Handle case where drawing ID is empty or null
                    Log.e(TAG, "Drawing ID is empty or null")
                    return@onSingleClick
                }

                binding.apply {
                    btnComment.invisible()
                    progress.show()
                }

                edtComment.setText("")
                hideKeyboard(edtComment)

                if (isReplyToComment) {
                    FirebaseFirestoreApi.drawingReplyCommentFunction(
                        getModel?.id.toString(),
                        comment,
                        commentParentId
                    )
                        .addOnCompleteListener { task ->
                            val commentCount = binding.tvComments.text.toString().toInt() + 1
                            binding.tvComments.text = commentCount.toString()

                            if (task.isSuccessful) {

                                fetchDrawing()
                                fetchComments()

                                val bundle = Bundle()
                                bundle.putString("post_id", getModel?.id.toString())
                                bundle.putString("parent_comment_id", commentParentId)
                                bundle.putString("user_id", getModel?.author?.userId)
                                sendUserEventWithParam(
                                    StringConstants.gallery_post_reply_comment,
                                    bundle
                                )

                                Log.e(
                                    TAG, "Reply Comment added successfully " +
                                            "drawingID:  ${getModel?.id} " +
                                            "comment: $comment" +
                                            "  commentParentId: $commentParentId"
                                )
                                // Clear comment text after successful submission
                                edtComment.setText("")
                                hideKeyboard(edtComment)
                            } else {
                                binding.apply {
                                    btnComment.show()
                                    progress.hide()
                                }
                                // Handle failure, e.g., show an error message or retry
                                Log.e(TAG, "Failed to add reply comment", task.exception)
                                showToastRelease(getString(R.string.f_to_reply))
                            }
                        }

                } else {
                    FirebaseFirestoreApi.drawingCommentFunction(drawingId, comment)
                        .addOnCompleteListener { task ->
                            val commentCount = binding.tvComments.text.toString().toInt() + 1
                            binding.tvComments.text = commentCount.toString()
                            if (task.isSuccessful) {

                                val bundle = Bundle()
                                bundle.putString("post_id", getModel?.id.toString())
                                bundle.putString("user_id", getModel?.author?.userId)
                                sendUserEventWithParam(StringConstants.gallery_post_comment, bundle)

                                fetchDrawing()
                                fetchComments()
                                Log.e(TAG, "Comment added successfully")
                                // Clear comment text after successful submission
                                edtComment.setText("")
                                hideKeyboard(edtComment)
                            } else {
                                binding.apply {
                                    btnComment.show()
                                    progress.hide()
                                }
                                // Handle failure, e.g., show an error message or retry
                                Log.e(TAG, "Failed to add comment", task.exception)
                                showToastRelease(getString(R.string.f_to_comm))
                            }
                        }
                }


            }


            imgShare.onSingleClick {
                loadImageAndSave()
            }
            imgFullScreen.onSingleClick {
                val bundle = Bundle()
                bundle.putString("post_id", getModel?.id.toString())
                bundle.putString("user_id", getModel?.author?.userId)
                sendUserEventWithParam(StringConstants.gallery_post_enlarge, bundle)
                startDrawingActivity(getModel!!, DrawingFullScreenActivity::class.java)
            }
            imgDrawing.onSingleClick {
                val bundle = Bundle()
                bundle.putString("post_id", getModel?.id.toString())
                bundle.putString("user_id", getModel?.author?.userId)
                sendUserEventWithParam(StringConstants.gallery_post_enlarge, bundle)
                startDrawingActivity(getModel!!, DrawingFullScreenActivity::class.java)
            }

            imgRateUS.onSingleClick {

                rateDrawing()
            }

            appCompatImageView5.onSingleClick {
                edtComment.requestFocus()
                edtComment.isFocusableInTouchMode = true
                edtComment.isEnabled = true

            }

            // like dislike button
            btnLike.onSingleClick1 {
                // Get the current user
                val currentUser =
                    FirebaseAuth.getInstance().currentUser?.uid ?: return@onSingleClick1
                // Drawing ID
                val drawingId = getModel?.id.toString()


                FirebaseFirestoreApi.isDrawingLiked(drawingId, currentUser)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val isLiked = task.result
                            Log.e(TAG, "isLiked: $isLiked")

                            if (isLiked) {
                                // Optimistically update the UI
                                val currentLikes = binding.tvLikes.text.toString().toInt()
                                if (currentLikes > 0) {
                                    val likeCount = currentLikes - 1
                                    binding.tvLikes.text = likeCount.toString()
                                }
                                val bundle = Bundle()
                                bundle.putString("post_id", drawingId)
                                bundle.putString("user_id", getModel?.author?.userId)
                                sendUserEventWithParam(StringConstants.gallery_post_unlike, bundle)

                                // If currently liked, call the unlike function
                                FirebaseFirestoreApi.drawingUnlikeFunction(drawingId)
                                    .addOnCompleteListener { unlikeTask ->
                                        if (unlikeTask.isSuccessful) {
                                            Log.e(TAG, "initListeners: UnLike")
                                        } else {
                                            // If unlike fails, try to re-like and revert the UI if necessary
                                            Log.e(TAG, "Failed to unlike: ${unlikeTask.exception}")
                                        }
                                    }
                            } else {

                                val bundle = Bundle()
                                bundle.putString("post_id", drawingId)
                                bundle.putString("user_id", getModel?.author?.userId)
                                sendUserEventWithParam(StringConstants.gallery_post_like, bundle)

                                // Optimistically update the UI
                                val likeCount = binding.tvLikes.text.toString().toInt() + 1
                                binding.tvLikes.text = likeCount.toString()

                                // If currently unliked, call the like function
                                FirebaseFirestoreApi.drawingLikeFunction(drawingId)
                                    .addOnCompleteListener { likeTask ->
                                        if (likeTask.isSuccessful) {
                                            Log.e(TAG, "initListeners: Like")
                                        } else {
                                            // If like fails, try to re-unlike and revert the UI if necessary
                                            Log.e(TAG, "Failed to like: ${likeTask.exception}")
                                        }
                                    }
                            }

                        }
                    }
            }

            imgRank.onSingleClick {
                openButtonSheetOfMultipleImages()
            }
        }
    }


    private fun openButtonSheetOfMultipleImages() {
        bottomSheet = BottomSheetDialog(this)
        val bottomSheetBehavior: BottomSheetBehavior<View>?

        val dialogBinding: LayoutBottomSheetChallengeBinding =
            LayoutBottomSheetChallengeBinding.inflate(layoutInflater)
        bottomSheet.setContentView(dialogBinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.bottomSheet.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        val layout: CoordinatorLayout? = bottomSheet.findViewById(R.id.bottomSheet)
        val layoutParams = layout?.layoutParams
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.7).toInt()
        // Set the height of the bottom sheet
        layoutParams?.height = desiredHeight// Apply the new layout parameters
        layout?.layoutParams = layoutParams

        // set user level to bottom sheet list
        val userLevel = if (getModel?.author?.level.isNullOrEmpty()) {
            Constants_Gallery.Beginner_1
        } else {
            getModel?.author?.level.toString()
        }

        viewModel.drawingList.observe(this) { drawings ->
            drawings?.let { list ->
                val counts = list.flatMap { it.counts }

                challengeLevelsAdapter =
                    ChallengeLevelsAdapter(counts, challengeLevelList, userLevel, this)
            }
        }

//        challengeLevelsAdapter = ChallengeLevelsAdapter(emptyList(), challengeLevelList, userLevel, this)
        with(dialogBinding.rvImagesList) {
            layoutManager = LinearLayoutManager(this@DrawingViewActivity)
            adapter = challengeLevelsAdapter
        }

        bottomSheet.show()
    }


    private fun rateUsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_rate_us)
        val ratingBar = dialog.findViewById(R.id.pdfRatingBar) as RatingBar
        val btnNo: Button = dialog.findViewById(R.id.btnNo) as Button
        val btnYes: Button = dialog.findViewById(R.id.btnYes) as Button
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                rate = rating
            }

        btnYes.setOnClickListener {
            dialog.setOnDismissListener {
                val ratingCount = binding.tvRateUsCount.text.toString().toInt() + 1
                binding.tvRateUsCount.text = ratingCount.toString()
                // User has not rated this post, proceed with rating
                FirebaseFirestoreApi.reviewDrawing(
                    drawingId = getModel?.id.toString(),
                    rating = rate.toInt(),
                    message = binding.edtComment.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val bundle = Bundle()
                        bundle.putString("post_id", getModel?.id.toString())
                        bundle.putString("user_id", getModel?.author?.userId)
                        sendUserEventWithParam(StringConstants.gallery_post_rate, bundle)

                        val result = task.result?.data
                        updateLocalData(rate)
                        hasRatedLocally = true
                        Log.d(TAG, "Cloud Function execution result: $result")
                    } else {
                        val exception = task.exception
                        Log.e(TAG, "Cloud Function execution failed", exception)

                        // Log more details if available
                        exception?.let {
                            Log.e(TAG, "Exception message: ${it.message}")
                            it.printStackTrace()
                        }
                    }
                }
            }
            dialog.dismiss()
        }
        btnNo.setOnClickListener {
            if (dialog.isShowing)
                dialog.dismiss()
        }
        if (!dialog.isShowing)
            dialog.show()
    }

    private fun rateDrawing() {
        FirebaseFirestoreApi.isPostRated(
            getModel?.id.toString(),
            constants.getString(constants.UserId, this)
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val hasRated = task.result
                if (hasRated == true) {
                    // User has already rated this post
                    showToastRelease(getString(R.string.a_rate))
                } else {
                    // increment rating count
                    rateUsDialog()
                }
            } else {
                // Handle error
                showToastRelease(getString(R.string.e_check))
            }
        }
    }

    private fun updateLocalData(rate: Float) {
        val currentDrawing = getModel // Assuming getModel holds the current drawing object

        currentDrawing?.let {
            it.statistic.reviewsCount = it.statistic.reviewsCount?.plus(1) ?: 1
            it.statistic.ratings = it.statistic.ratings?.plus(rate.toInt()) ?: rate.toInt()

            // Calculate the average rating
            val averageRating = if (it.statistic.reviewsCount!! > 0) {
                it.statistic.ratings!!.toFloat() / it.statistic.reviewsCount!!
            } else {
                0f
            }

            // Update UI elements
            binding.apply {
                val formattedAverageRating = String.format("%.1f", averageRating)

                tvRateUsCount.text = it.statistic.reviewsCount.toString()
                tvAvgRating.text = formattedAverageRating
                toturialRatingBar.rating = averageRating
            }
        }
    }


    private fun loadImageAndSave() {
        val imageUrl = getModel?.images?.content
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.feed_thumb_default).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Save the bitmap to a file
                    val file = File(applicationContext.cacheDir, "shared_image.png")
                    val outStream = FileOutputStream(file)
                    resource.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    outStream.close()
                    // Share the image
                    shareImage(file)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle load cleared
                }
            })
    }

    fun shareImage(file: File) {

        FirebaseFirestoreApi.shareCountDrawing(getModel?.id.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Handle success
                    val result = task.result?.data
                    Log.d(TAG, "Cloud Function execution result: $result")
                } else {
                    // Handle error
                    val exception = task.exception
                    Log.e(TAG, "Cloud Function execution failed", exception)
                }
            }

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            file
        )


        var message = ""
        var url = getModel?.links?.youtube ?: ""
        var name = getModel?.author?.name ?: ""

        if (url.isNotEmpty() && !url.endsWith("null")) {
            message += "Watch video : " + "\n"
            message += url + "\n\n"
        }

        message += "Check out this Gallery drawing by user $name on the Paintology app.\n" +
                "App:\n" +
                "https://play.google.com/store/apps/details?id=com.paintology.lite\n"


        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Adding subject and body for Gmail
            putExtra(Intent.EXTRA_SUBJECT, "Gallery image from Paintology")
            putExtra(
                Intent.EXTRA_TEXT,
                message
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }


    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }

            val userName = getModel?.author?.name


            // for scrolling textview
            tvTitle.isSelected = true

            if (getModel?.type == "freehand") {
                tvTitle.text = "$userName - ${getString(R.string.freehand)}"
            } else {
                tvTitle.text = "$userName - ${getString(R.string.tutorials)}"
            }
            imgFav.hide()
        }
    }

    private fun initRecyclerView() {

        // bottom spacing of recyclerview
        val space = resources.getDimensionPixelSize(R.dimen._80sdp)
        val spacingDecoration = SpecingDecoration(0, space)
        binding.rvComments.addItemDecoration(spacingDecoration)


        commentsAdapter = CommentsAdapter(this)
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentsAdapter
        }
    }

    override fun onCLick(position: Int, model: ChallengeLevelsModel) {
        if (bottomSheet.isShowing) {
            bottomSheet.dismiss()
        }
        getModel?.let {
            startDrawingActivityWithRank(
                it,
                DrawingActivity::class.java,
                true,
                getModel?.type.toString(),
                model.titleChallenge.toString()
            )
        }
    }

    override fun replyToComment(model: Comment, position: Int) {
        isReplyToComment = true
        commentParentId = model.id
        Log.e(TAG, "replyToComment: $model")
        binding.edtComment.apply {
            requestFocus()
            isFocusableInTouchMode = true
            isEnabled = true
        }
    }
}