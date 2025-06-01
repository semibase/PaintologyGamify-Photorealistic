package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.ToolbarButtonClickListener
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.GalleryTutorialsAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
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
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.DialogPlusButtonBinding
import com.paintology.lite.photorealistic.drawing.databinding.FragmentGalleryTutorailsBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutGalleryFilterBottomSheetBinding
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.events.FilterChangeEvent
import com.paintology.lite.photorealistic.drawing.util.events.GalleryEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.paintology.lite.photorealistic.drawing.util.setSharedNo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream

class GalleryTutorailsFragment : BaseFragment(),
    GalleryTutorialsAdapter.OnGalleryMenuClick, ToolbarButtonClickListener {

    private var pageNo = 1
    private val perPage = 10
    private var isLoading = false
    private var isLastPage = false
    private var progressDialog: ProgressDialog? = null

    private lateinit var drawingRepository: DrawingRepository

    companion object {
        const val TAG = "GalleryTAG"
    }

    lateinit var binding: FragmentGalleryTutorailsBinding
    private var galleryTutorialsAdapter: GalleryTutorialsAdapter? = null

    private var tutorialsDrawingList = mutableListOf<NewDrawing>()

    private var bottomSheetForFilter: BottomSheetDialog? = null

    private var selectedCountry = "";

    private var currentFilter = "Date Descending (Default)"
    private var currentFilterBy = "created_at:desc"

    @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryTutorailsBinding.inflate(layoutInflater, container, false)

        initProgressBar()

        drawingRepository = DrawingRepository(context)

        viewModel.tutorialDrawings.observe(viewLifecycleOwner) { tutorialDrawings ->
            // Update UI with tutorial drawings


            Log.e(TAG, "tutorialDrawings: ${tutorialDrawings.size} $pageNo")


            if (tutorialDrawings.isNotEmpty()) {

                tutorialsDrawingList.clear()

                if (tutorialsDrawingList.isEmpty()) {
                    tutorialsDrawingList.add(0, Lists.emptyNewDrawing)
                }

                isLastPage = false// TO load next data
                tutorialsDrawingList.addAll(tutorialDrawings)
            } else {
                if (pageNo == 1) {
                    tutorialsDrawingList.clear()
                }
                isLastPage = true// TO load next data
            }

            isLoading = false

            if (tutorialsDrawingList.size <= 0) {
                binding.layoutNoData.show()
                binding.rvBlogPosts.hide()
            } else {
                binding.layoutNoData.hide()
                binding.rvBlogPosts.show()
            }

            galleryTutorialsAdapter?.notifyDataSetChanged()


        }

        viewModel.fetchDrawingStagingList(
            pageNo,
            perPage,
            selectedCountry,
            "type:=tutorials",
            currentFilterBy
        )


        initRecyclerView()

        initListener()


        return binding.root
    }

    private fun initProgressBar() {
        progressDialog = ProgressDialog(this@GalleryTutorailsFragment.requireContext())

        progressDialog?.setTitle(resources.getString(R.string.please_wait))
        progressDialog?.setMessage("Loading Tutorials...")
        progressDialog?.setCanceledOnTouchOutside(false)
    }

    private fun initListener() {
        binding.apply {
            btnDraw.onSingleClick {
                context?.openActivity(DrawNowActivity::class.java)
            }
            btnTutorial.onSingleClick {
                if (context != null) {
                    context?.openActivity(CategoryActivity::class.java)
                }
            }
        }
    }

    private fun initRecyclerView() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = appDatabase.drawingFavDao()
            val favourites = dao.favouritesIds
            withContext(Dispatchers.Main) {

                Log.e(TAG, "initRecyclerView: $tutorialsDrawingList")

                galleryTutorialsAdapter = GalleryTutorialsAdapter(
                    true,
                    tutorialsDrawingList,
                    this@GalleryTutorailsFragment,
                    favourites
                )
                binding.rvBlogPosts.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = galleryTutorialsAdapter

                    binding.rvBlogPosts.addOnScrollListener(object :
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
                                    viewModel.fetchDrawingStagingList(
                                        pageNo,
                                        perPage,
                                        selectedCountry,
                                        "type:=tutorials",
                                        currentFilterBy
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
        Log.d(TAG, "onMenuClick: $model")
    }

    override fun onItemClick(model: NewDrawing, position: Int) {
        if (position == 0) {

            val dialog = Dialog(requireContext())
            val dialogBinding: DialogPlusButtonBinding =
                DialogPlusButtonBinding.inflate(layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(dialogBinding.root)


            dialogBinding.apply {
                btnPostToGallery.onSingleClick {
                    val mPrefBackgroundColor = -1
                    requireContext().setSharedNo()
                    val lIntent1 = Intent(requireContext(), PaintActivity::class.java)
                    lIntent1.setAction("New Paint")
                    StringConstants.constants.putInt(
                        "background_color",
                        mPrefBackgroundColor,
                        requireContext()
                    )
                    lIntent1.putExtra("background_color", mPrefBackgroundColor)
                    lIntent1.putExtra("isFromGallery", true)
                    startActivity(lIntent1)
                    dialog.dismiss()
                }

                btnTutorials.onSingleClick {
                    dialog.dismiss()
                    if (context != null) {
                        context?.openActivity(CategoryActivity::class.java)
                    }
                }
            }


            if (!dialog.isShowing)
                dialog.show()


        } else {
            val bundle = Bundle()
            bundle.putString("post_id", model.id)
            bundle.putString("post_type", "tutorials")
            bundle.putString("user_id", model.author.userId)
            bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
            context?.sendUserEventWithParam(StringConstants.gallery_post_open, bundle)
            context?.startDrawingActivity(model, DrawingViewActivity::class.java, false)
        }
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
                    bundle.putString(
                        "country",
                        if (selectedCountry == "") "WW" else selectedCountry
                    )
                    context?.sendUserEventWithParam(
                        StringConstants.gallery_post_do_tutorial,
                        bundle
                    )
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
                    bundle.putString(
                        "country",
                        if (selectedCountry == "") "WW" else selectedCountry
                    )
                    context?.sendUserEventWithParam(
                        StringConstants.gallery_post_add_favorite,
                        bundle
                    )

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
                    bundle.putString(
                        "country",
                        if (selectedCountry == "") "WW" else selectedCountry
                    )
                    context?.sendUserEventWithParam(StringConstants.gallery_post_share, bundle)

                    loadImageAndSave(model.images.content, item = model)
                }
            }
        }
    }

    public fun loadImageAndSave(
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

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_SUBJECT, "Gallery image from Paintology")

            var message = ""
            if (item.links.youtube.isNotEmpty() && !item.links.youtube.endsWith("null")) {
                message += "Watch video : " + "\n"
                message += item.links.youtube + "\n\n"
            }

            message += "Check out this Gallery drawing by user ${item.author.name} on the Paintology app.\n" +
                    "App:\n" +
                    "https://play.google.com/store/apps/details?id=com.paintology.lite\n"

            putExtra(
                Intent.EXTRA_TEXT,
                message
                )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }


    override fun onToolbarButtonClick() {
        showFilterDialog()
    }

    fun clearFilter(dialogBinding: LayoutGalleryFilterBottomSheetBinding) {

        with(dialogBinding) {

            tvComments.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvTutorails.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvDateDescending.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvDateAscending.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvFreeHand.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvRatings.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvLikes.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvByLevels.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            tvFavourites.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
    }

    fun setFilter(dialogBinding: LayoutGalleryFilterBottomSheetBinding, filterItem: String) {

        with(dialogBinding)
        {
            when (filterItem.lowercase()) {
                tvComments.text.toString().lowercase() -> {
                    tvComments.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvTutorails.text.toString().lowercase() -> {
                    tvTutorails.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvDateAscending.text.toString().lowercase() -> {
                    tvDateAscending.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvDateDescending.text.toString().lowercase() -> {
                    tvDateDescending.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvFreeHand.text.toString().lowercase() -> {
                    tvFreeHand.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvRatings.text.toString().lowercase() -> {
                    tvRatings.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvLikes.text.toString().lowercase() -> {
                    tvLikes.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

//                                tvByAward.text.toString() -> {
//                                    tvByAward.setTextColor(
//                                        ContextCompat.getColor(
//                                            requireContext(),
//                                            R.color.blue_a
//                                        )
//                                    )
//                                }

                tvByLevels.text.toString().lowercase() -> {
                    tvByLevels.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }

                tvFavourites.text.toString().lowercase() -> {
                    tvFavourites.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_filter
                        )
                    )
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showFilterDialog() {


        bottomSheetForFilter = BottomSheetDialog(requireContext())
        val bottomSheetBehavior: BottomSheetBehavior<View>?

        val dialogBinding: LayoutGalleryFilterBottomSheetBinding =
            LayoutGalleryFilterBottomSheetBinding.inflate(layoutInflater)
        bottomSheetForFilter?.setContentView(dialogBinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.bottomSheet.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        val layout: CoordinatorLayout? = bottomSheetForFilter?.findViewById(R.id.bottomSheet)
        val layoutParams = layout?.layoutParams
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.55).toInt()

        // Set the height of the bottom sheet
        layoutParams?.height = desiredHeight// Apply the new layout parameters
        layout?.layoutParams = layoutParams

        clearFilter(dialogBinding)
        setFilter(dialogBinding, currentFilter)

        with(dialogBinding) {

            /*   viewLifecycleOwner.lifecycleScope.launch {
                   // Check if the fragment is added before accessing requireContext()
                   if (isAdded) {
                       topLevel.onCreate().readFilterItem(requireContext())
                           .collectLatest { filterItem ->
                               Log.e("TAGRR", filterItem + " HELL")

                               bottomSheetForFilter!!.dismiss()
                           }
                   }
               }*/





            tvComments.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvComments.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvComments.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvRatings.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvRatings.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvRatings.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvLikes.onSingleClick {
                topLevel.onCreate().writeFilterItem(requireContext(), tvLikes.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvLikes.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvTutorails.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvTutorails.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvTutorails.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvDateAscending.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvDateAscending.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvDateAscending.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvByLevels.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvByLevels.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvByLevels.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvFreeHand.visibility = View.GONE
            tvFreeHand.onSingleClick {
                /* topLevel.onCreate()
                     .writeFilterItem(requireContext(), tvFreeHand.text.toString())

                 if (activity != null && activity is GalleryActivity) {
                     (activity as GalleryActivity?)?.navigateToFragmentTwo()
                 }*/
            }

            tvDateDescending.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvDateDescending.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvDateDescending.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvFavourites.visibility = View.GONE
            tvFavourites.onSingleClick {
                /*topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvFavourites.text.toString())
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = appDatabase.drawingFavDao()
                    val favourites = dao.favouritesIds
                    withContext(Dispatchers.Main) {
                        drawingList =
                            drawingList.filter { favourites.contains(it.id) }.toMutableList()
                        drawingList.add(0, Lists.emptyDrawing)
                        initRecyclerView()
                    }
                }*/
            }
        }

        bottomSheetForFilter?.show()


//            tvFreeHand.onSingleClick {
//
//                if (filterDialog.isShowing) {
//                    topLevel.onCreate()
//                        .writeFilterItem(requireContext(), tvFreeHand.text.toString())
//
//                    filterDialog.dismiss()
//                    if (activity != null && activity is GalleryActivity) {
//                        (activity as GalleryActivity?)?.navigateToFragmentTwo()
//                    }
//                }
//
//            }
//
//            tvRatings.onSingleClick {
//                if (filterDialog.isShowing) {
//                    topLevel.onCreate()
//                        .writeFilterItem(requireContext(), tvRatings.text.toString())
//
//                    filterDialog.dismiss()
//                    viewModel.fetchDrawingStagingList(
//                        1,
//                        10,
//                        "",
//                        "type:=tutorials",
//                        "statistic.ratings:desc"
//                    )
//
//                }
//            }
//
//            tvByAward.onSingleClick {
//                if (filterDialog.isShowing) {
//                    topLevel.onCreate()
//                        .writeFilterItem(requireContext(), tvByAward.text.toString())
//
//                    filterDialog.dismiss()
//                }
//            }
//
//            tvByLevels.onSingleClick {
//                if (filterDialog.isShowing) {
//                    topLevel.onCreate()
//                        .writeFilterItem(requireContext(), tvByLevels.text.toString())
//                    filterDialog.dismiss()
//                    viewModel.fetchDrawingStagingList(
//                        1,
//                        10,
//                        "",
//                        "type:=tutorials",
//                        "created_at:asc"
//                    )
//
//                }
//            }
//
//            tvFavourites.onSingleClick {
//                if (bottomSheet.isShowing) {
//                    topLevel.onCreate()
//                        .writeFilterItem(requireContext(), tvFavourites.text.toString())
//
//                    filterDialog.dismiss()
//                }
//            }


        // Filter Dialog

//
//        val filterDialog = Dialog(requireContext(), R.style.CustomDialog)
////        val dialogBinding: DialogGalleryFilterBinding =
//            DialogGalleryFilterBinding.inflate(layoutInflater)
//        filterDialog.setContentView(dialogBinding.root)
//
//
//        filterDialog.show()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterChangeEvent(event: FilterChangeEvent) {
        pageNo = 1;
        currentFilter = event.filterType
        if (currentFilter.equals("Freehand (Default)", true)) {
            currentFilter = "Tutorials (Default)"
        }
        filerData()
    }

    fun filerData() {
        if (currentFilter.equals("Comments", true)) {
            currentFilterBy = "statistic.comments:desc"
        } else if (currentFilter.equals("Likes", true)) {
            currentFilterBy = "statistic.likes:desc"
        } else if (currentFilter.equals("Tutorials (Default)", true)) {
            currentFilterBy = ""
        } else if (currentFilter.equals("Date Ascending", true)) {
            currentFilterBy = "created_at:asc"
        } else if (currentFilter.equals("Date Descending (Default)", true)) {
            currentFilterBy = "created_at:desc"
        } else if (currentFilter.equals("Ratings", true)) {
            currentFilterBy = "statistic.ratings:desc"
        } else if (currentFilter.equals("By Levels", true)) {
            currentFilterBy = "${'$'}levels(sorting_number:asc)"
        }

        viewModel.fetchDrawingStagingList(
            pageNo,
            perPage,
            selectedCountry,
            "type:=tutorials",
            currentFilterBy
        )
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGalleryChangeEvent(event: GalleryEvent) {
        // topLevel.onCreate().writePosition(requireContext(), position)
        pageNo = 1;
        event.flag?.let { topLevel.onCreate().writeFlagId(requireContext(), it) }
        topLevel.onCreate().writeLangName(requireContext(), event.model.countryName ?: "")

        if (event.position == 0) {
            selectedCountry = ""
        } else {
            selectedCountry = event.model.countryCode.toString()
        }
        filerData()
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


    fun openTutorialsRewardPoint(mId: String, context: Context = requireContext()) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.open_tutorial, mId)
        /* if (FirebaseAuth.getInstance().currentUser != null) {
             val rewardSetup = AppUtils.getRewardSetup(context)
             if (rewardSetup != null) {
                 FirebaseFirestoreApi.updateIncreasableRewardValue(
                     "opening_tutorials",
                     rewardSetup.opening_tutorials ?: 0,
                     FirebaseAuth.getInstance().currentUser!!.uid
                 )
             }
         }*/
    }
}