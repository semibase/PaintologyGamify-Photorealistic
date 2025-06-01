package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.GalleryAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu1
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.startNewDrawingActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityGalleryTagBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class GalleryTagActivity : BaseActivity(), GalleryAdapter.OnGalleryMenuClick {

    var searchTag: String = "";
    private var galleryTutorialsAdapter: GalleryAdapter? = null
    private var tagDrawingList = mutableListOf<NewDrawing>()
    private lateinit var drawingRepository: DrawingRepository

    private var pageNo = 1
    private val perPage = 10
    private var isLoading = false
    private var isLastPage = false

    private val binding by lazy {
        ActivityGalleryTagBinding.inflate(layoutInflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        drawingRepository = DrawingRepository(this)

        getIntentData()
        initRecyclerView()

        viewModel.tagDrawings.observe(this) { tagDrawings ->

            binding.itemProgressBar.visibility = View.GONE

            if (tagDrawings.isNotEmpty()) {

                tagDrawingList.clear()

                isLastPage = false
                tagDrawingList.addAll(tagDrawings)
            } else {
                if (pageNo == 1) {
                    tagDrawingList.clear()
                }
                isLastPage = true
            }

            isLoading = false

            if (tagDrawingList.size <= 0) {
                binding.tvEmptyMsg.show()
                binding.rvGallery.hide()
            } else {
                binding.tvEmptyMsg.hide()
                binding.rvGallery.show()
            }

            galleryTutorialsAdapter?.notifyDataSetChanged()

        }

        binding.itemProgressBar.visibility = View.VISIBLE
        viewModel.fetchDrawingTagList(
            pageNo,
            perPage,
            searchTag
        )

    }

    @SuppressLint("SetTextI18n")
    private fun getIntentData() {
        searchTag = intent.getStringExtra("searchTag") ?: ""

        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = "${getString(R.string.gallery)} - $searchTag"
            imgFav.hide()
        }
    }

    private fun initRecyclerView() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = appDatabase.drawingFavDao()
            val favourites = dao.favouritesIds
            withContext(Dispatchers.Main) {
                galleryTutorialsAdapter = GalleryAdapter(
                    true,
                    tagDrawingList,
                    this@GalleryTagActivity,
                    favourites
                )
                binding.rvGallery.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = galleryTutorialsAdapter

                    binding.rvGallery.addOnScrollListener(object :
                        RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val layoutManager = recyclerView.layoutManager as GridLayoutManager
                            val visibleItemCount = layoutManager.childCount
                            val totalItemCount = layoutManager.itemCount
                            val firstVisibleItemPosition =
                                layoutManager.findFirstVisibleItemPosition()

                            if (!isLoading && !isLastPage) {
                                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0
                                ) {
                                    pageNo++
                                    isLoading = true
                                    viewModel.fetchDrawingTagList(
                                        pageNo,
                                        perPage,
                                        searchTag
                                    )
                                }
                            }
                        }
                    })

                }
            }
        }
    }

    override fun onMenuClick(
        model: NewDrawing,
        position: Int,
        root: ImageView,
        onFavourite: () -> Unit
    ) {
        openMenuDialog(model, position, root, onFavourite)
    }

    override fun onItemClick(model: NewDrawing, position: Int) {
        startDrawingActivity(model, DrawingViewActivity::class.java, false)
    }

    override fun onNameClick(model: NewDrawing, position: Int) {
        FireUtils.openProfileScreen(this, model.author.userId)
    }

    private fun openMenuDialog(
        model: NewDrawing,
        position: Int, root: ImageView, onFavourite: () -> Unit
    ) {

        if (model.type == "freehand") {
            root.showPopupMenu(R.menu.gallery_frre_hand) {
                when (it.itemId) {
                    R.id.openItem -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString("post_type", "freehand")
                        bundle.putString("user_id", model.author.userId)
                        bundle.putString("country", "")
                        sendUserEventWithParam(StringConstants.gallery_post_open, bundle)
                        startDrawingActivity(model, DrawingViewActivity::class.java, false)
                    }

                    R.id.learnDrawing -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString("post_type", "freehand")
                        bundle.putString("user_id", model.author.userId)
                        bundle.putString("country", "")
                        sendUserEventWithParam(StringConstants.gallery_post_learn_drawing, bundle)
                        openActivity(CategoryActivity::class.java)
                    }

                    R.id.favItem -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString("post_type", "freehand")
                        bundle.putString("user_id", model.author.userId)
                        bundle.putString("country", "")
                        sendUserEventWithParam(StringConstants.gallery_post_add_favorite, bundle)
                        val newDraw =
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
                }

            }
        } else {
            root.showPopupMenu1(R.menu.gallery_toturial_menu, false) {
                when (it.itemId) {
                    R.id.openItem -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString("post_type", "tutorials")
                        bundle.putString("user_id", model.author.userId)
                        bundle.putString("country", "")
                        sendUserEventWithParam(StringConstants.gallery_post_open, bundle)
                        startNewDrawingActivity(
                            model,
                            DrawingViewActivity::class.java,
                            false
                        )
                    }

                    R.id.doTutorialItem -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString(
                            "post_type", "tutorials"
                        )
                        bundle.putString("country", "")
                        bundle.putString("user_id", model.author.userId)
                        sendUserEventWithParam(StringConstants.gallery_post_do_tutorial, bundle)
                        if (model.metadata.tutorialId.isEmpty()) {
                            Toast.makeText(this, getString(R.string.no_tut), Toast.LENGTH_SHORT)
                                .show()
                            openActivity(CategoryActivity::class.java)
                        } else {
                            FireUtils.showProgressDialog(
                                this,
                                getString(R.string.please_wait)
                            )
                            TutorialUtils(this).parseTutorial(model?.metadata?.tutorialId.toString())
                        }
                    }

                    R.id.favItem -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString("post_type", "tutorials")
                        bundle.putString("country", "")
                        bundle.putString("user_id", model.author.userId)
                        sendUserEventWithParam(StringConstants.gallery_post_add_favorite, bundle)

                        val newDraw =
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


                    R.id.shareItem12312 -> {
                        val bundle = Bundle()
                        bundle.putString("post_id", model.id)
                        bundle.putString(
                            "post_type", "tutorials"
                        )
                        bundle.putString("country", "")
                        bundle.putString("user_id", model.author.userId)
                        sendUserEventWithParam(StringConstants.gallery_post_share, bundle)
                        loadImageAndSave(model.images.content, position = position)
                    }
                }
            }
        }
    }

    public fun loadImageAndSave(
        imageUrl: String,
        context: Context = this,
        position: Int
    ) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.img_cat_dummy).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Save the bitmap to a file
                    val file = File(context.cacheDir, "shared_image.png")
                    val outStream = FileOutputStream(file)
                    resource.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    outStream.close()
                    // Share the image
                    shareImage(file, context, position)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle load cleared
                }
            })
    }

    fun shareImage(file: File, context: Context = this, position: Int) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_SUBJECT, "Gallery image from Paintology")

            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this Gallery drawing by user ${tagDrawingList.get(position).author.name} on the Paintology app.\n" +
                        "App:\n" +
                        "https://play.google.com/store/apps/details?id=com.paintology.lite\n"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }
}