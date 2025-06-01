package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.ToolbarButtonClickListener
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.GalleryTutorialsAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity.Companion.isShowingUserLevelData
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity.Companion.userID
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity.Companion.userLevel
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingViewActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu1
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.startNewDrawingActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.FragmentUserGalleryTutorialBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.events.UserGalleryEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream

class UserGalleryTutorialFragment : BaseFragment(), GalleryTutorialsAdapter.OnGalleryMenuClick,
    ToolbarButtonClickListener {

    private lateinit var drawingRepository: DrawingRepository
    lateinit var binding: FragmentUserGalleryTutorialBinding
    private var galleryTutorialsAdapter: GalleryTutorialsAdapter? = null
    private var drawingList = mutableListOf<NewDrawing>()

    private var pageNo = 1
    private val perPage = 10
    private var isLoading = false
    private var isLastPage = false
    private var isWorldwide = true

    private var selectedCountry = "";

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserGalleryTutorialBinding.inflate(layoutInflater, container, false)


        val userLevel = userLevel
        drawingRepository = DrawingRepository(context)

        viewModel.tutorialDrawings.observe(viewLifecycleOwner) { tutorialDrawings ->
            Log.d(TAG, "onCreateView: $tutorialDrawings")

            if (tutorialDrawings.isNotEmpty()) {
                drawingList.clear()
                isLastPage = false// TO load next data
                drawingList.addAll(tutorialDrawings)
            } else {
                if (pageNo == 1) {
                    drawingList.clear()
                }
                isLastPage = true// TO load next data
            }

            isLoading = false

            if (drawingList.size <= 0) {
                binding.layoutNoData.show()
                binding.rvUserGalleryTutorial.hide()
            } else {
                binding.layoutNoData.hide()
                binding.rvUserGalleryTutorial.show()
            }

            galleryTutorialsAdapter?.notifyDataSetChanged()

        }

        if (isShowingUserLevelData) {
            viewModel.fetchDrawingStagingList(
                pageNo,
                perPage,
                selectedCountry,
                "author.level:=$userLevel&&type:=tutorials",
                ""
            )
        } else {
            viewModel.fetchDrawingStagingList(
                pageNo,
                perPage,
                "",
                "author.user_id:=$userID&&type:=tutorials",
                ""
            )
        }
        initRecyclerView()

        initListener()


        return binding.root
    }

    private fun initListener() {


        binding.apply {
            btnDraw.onSingleClick {
                context?.openActivity(DrawNowActivity::class.java)
            }
            btnTutorial.onSingleClick {
                context?.openActivity(CategoryActivity::class.java)
            }
        }
    }

    private fun initRecyclerView() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = appDatabase.drawingFavDao()
            val favourites = dao.favouritesIds
            withContext(Dispatchers.Main) {

                Log.e(TAG, "drawingList Tutorial: $drawingList")

                galleryTutorialsAdapter = GalleryTutorialsAdapter(
                    false,
                    drawingList,
                    this@UserGalleryTutorialFragment,
                    favourites
                )
                binding.rvUserGalleryTutorial.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = galleryTutorialsAdapter
                    binding.rvUserGalleryTutorial.addOnScrollListener(object :
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
                                    if (isShowingUserLevelData) {
                                        viewModel.fetchDrawingStagingList(
                                            pageNo,
                                            perPage,
                                            selectedCountry,
                                            "author.level:=$userLevel&&type:=tutorials",
                                            ""
                                        )
                                    } else {
                                        viewModel.fetchDrawingStagingList(
                                            pageNo,
                                            perPage,
                                            "",
                                            "author.user_id:=$userID&&type:=tutorials",
                                            ""
                                        )
                                    }
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
        Log.d(GalleryTutorailsFragment.TAG, "onMenuClick: $model")
    }

    private fun openMenuDialog(
        model: NewDrawing,
        position: Int,
        root: ImageView,
        onFavourite: () -> Unit
    ) {

        root.showPopupMenu1(R.menu.gallery_toturial_menu, false) {
            when (it.itemId) {
                R.id.openItem -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "tutorials")
                    bundle.putString(
                        "country",
                        if (selectedCountry == "") "WW" else selectedCountry
                    )
                    bundle.putString("user_id", model.author.userId)
                    context?.sendUserEventWithParam(StringConstants.gallery_post_open, bundle)
                    context?.startNewDrawingActivity(
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
                    bundle.putString("user_id", model.author.userId)
                    bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
                    context?.sendUserEventWithParam(StringConstants.gallery_post_do_tutorial, bundle)
                    if (model.metadata.tutorialId.isEmpty()) {
                        Toast.makeText(context, "No tutorial found", Toast.LENGTH_SHORT).show()
                        context?.openActivity(CategoryActivity::class.java)
                    } else {
                        FireUtils.showProgressDialog(
                            context,
                            getString(R.string.please_wait)
                        )
                        TutorialUtils(context).parseTutorial(model?.metadata?.tutorialId.toString())
                    }
                }

                R.id.favItem -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "tutorials")
                    bundle.putString("user_id", model.author.userId)
                    bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
                    context?.sendUserEventWithParam(StringConstants.gallery_post_add_favorite, bundle)
                    val newDraw: com.paintology.lite.photorealistic.drawing.Activity.favourite.NewDrawing =
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

//                    val dao = appDatabase.drawingFavDao()
//
//                    val drawing = DrawingEntity(
//                        model.id ?: UUID.randomUUID().toString(),
//                        model.userName,
//                        model.title,
//                        model.description,
//                        model.imageUrl,
//                        model.createdAt,
//                        model.uid,
//                        model.tags,
//                        model.type,
//                        model.rating,
//                        model.likes,
//                        model.comments,
//                        model.level,
//                        model.totalPoints,
//                        model.serverUserId,
//                        model.youtubeLink,
//                        model.path,
//                        model.parentFolderPath
//                    )
//                    Log.i("RoomFavourite", model.id ?: "")
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val favourites = dao.favouritesIds
//                        if (!favourites.contains(drawing.id)) {
//                            dao.insert(drawing)
//                        } else {
//                            favourites.remove(drawing.id)
//                            dao.delete(drawing)
//                        }
//                    }
//                    onFavourite()
                }


                R.id.shareItem12312 -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString(
                        "post_type", "tutorials"
                    )
                    bundle.putString("user_id", model.author.userId)
                    bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
                    context?.sendUserEventWithParam(StringConstants.gallery_post_share, bundle)

                    loadImageAndSave(model.images.content,requireContext(), model)
                }
            }
        }
    }


    fun loadImageAndSave(
        imageUrl: String,
        context: Context = requireContext(),
        item: NewDrawing
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
                    shareImage(file, context, item)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle load cleared
                }
            })
    }

    fun shareImage(file: File, context: Context = requireContext(), item: NewDrawing) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        var message = ""
        var url = item.links.youtube
        var name = item.author.name

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
            putExtra(Intent.EXTRA_SUBJECT, "Gallery image from Paintology")

            putExtra(
                Intent.EXTRA_TEXT,
                message
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }


    override fun onItemClick(model: NewDrawing, position: Int) {
        context?.startDrawingActivity(model, DrawingViewActivity::class.java, false)
    }

    override fun onNameClick(model: NewDrawing, position: Int) {
        val bundle = Bundle()
        bundle.putString("post_id", model.id)
        bundle.putString(
            "post_type",
            if (model.type == "freehand") "freehand" else "tutorials"
        )
        bundle.putString("user_id", model.author.userId)
        context?.sendUserEventWithParam(StringConstants.gallery_post_open_author, bundle)
        FireUtils.openProfileScreen(context, model.author.userId)
    }


    override fun onStart() {
        super.onStart()
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGalleryChangeEvent(event: UserGalleryEvent) {
        // topLevel.onCreate().writePosition(requireContext(), position)
        pageNo = 1;

        //  topLevel.onCreate().writePosition(requireContext(), position)
        event.flag?.let { topLevel.onCreate().writeFlagId(requireContext(), it) }
        topLevel.onCreate().writeLangName(requireContext(), event.model.countryName ?: "")

        if (event.position == 0) {
            selectedCountry = ""
            isWorldwide = true
            viewModel.fetchDrawingStagingList(
                pageNo, perPage,
                "",
                "author.level:=$userLevel&&type:=tutorials",
                ""
            )
        } else {
            selectedCountry = event.model.countryCode.toString()
            isWorldwide = false
            viewModel.fetchDrawingStagingList(
                pageNo, perPage,
                event.model.countryCode.toString(),
                "author.level:=$userLevel&&type:=tutorials",
                ""
            )
        }
    }


    override fun onToolbarButtonClick() {}
}