package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paintology.lite.photorealistic.drawing.Activity.Lists.emptyNewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.ToolbarButtonClickListener
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.GalleryTutorialsAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingViewActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.DrawNowActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.FragmentGalleryFreeHandragmentBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutGalleryFilterBottomSheetBinding
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.events.FilterChangeEvent
import com.paintology.lite.photorealistic.drawing.util.events.GalleryEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class GalleryFreeHandFragment : BaseFragment(), GalleryTutorialsAdapter.OnGalleryMenuClick,
    ToolbarButtonClickListener {


    lateinit var binding: FragmentGalleryFreeHandragmentBinding
    private var galleryTutorialsAdapter: GalleryTutorialsAdapter? = null
    private var freehandDrawingList = mutableListOf<NewDrawing>()

    private var selectedCountry = "";
    private lateinit var drawingRepository: DrawingRepository

    private var bottomSheetForFilter: BottomSheetDialog? = null

    private var pageNo = 1
    private val perPage = 10
    private var isLoading = false
    private var isLastPage = false

    companion object {
        const val TAG = "FREEHAND_TAG"
    }

    private var totalDrawings = 0
    private var currentFilter = "Date Descending (Default)"
    private var currentFilterBy = "created_at:desc"

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryFreeHandragmentBinding.inflate(layoutInflater, container, false)
        drawingRepository = DrawingRepository(context)


        viewModel.freehandDrawings.observe(viewLifecycleOwner) { freehandDrawings ->
            // Update UI with freehand drawings
            Log.e(GalleryTutorailsFragment.TAG, "freehandDrawings: $freehandDrawings")


            if (freehandDrawings.isNotEmpty()) {
                freehandDrawingList.clear()

                if (freehandDrawingList.isEmpty()) {
                    freehandDrawingList.add(0, emptyNewDrawing)
                }

                isLastPage = false// TO load next data
                freehandDrawingList.addAll(freehandDrawings)
            } else {
                if (pageNo == 1) {
                    freehandDrawingList.clear()
                }
                isLastPage = true// TO load next data
            }

            isLoading = false // TO load next data

            totalDrawings = freehandDrawingList.size

            if (totalDrawings <= 0) {
                binding.layoutNoData.show()
                binding.rvBlogPosts.hide()
            } else {
                binding.layoutNoData.hide()
                binding.rvBlogPosts.show()
            }

            galleryTutorialsAdapter?.notifyDataSetChanged()


        }

        viewModel.fetchDrawingFreeHandList(
            pageNo,
            perPage,
            selectedCountry,
            "type:=freehand",
            currentFilterBy
        )


        initListener()

        initRecyclerView()

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
                galleryTutorialsAdapter = GalleryTutorialsAdapter(
                    true,
                    freehandDrawingList,
                    this@GalleryFreeHandFragment,
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
                                    viewModel.fetchDrawingFreeHandList(
                                        pageNo,
                                        perPage,
                                        selectedCountry,
                                        "type:=freehand",
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
        openMenuDialog(model, root, onFavourite)
    }

    override fun onItemClick(model: NewDrawing, position: Int) {
        if (position == 0) {

            val intent = Intent(requireContext(), PaintActivity::class.java)
            intent.action = "New Paint"
            intent.putExtra("drawingType", "freehand")
            startActivity(intent)

        } else {
            val bundle = Bundle()
            bundle.putString("post_id", model.id)
            bundle.putString("post_type", "freehand")
            bundle.putString("user_id", model.author.userId)
            bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
            context?.sendUserEventWithParam(StringConstants.gallery_post_open, bundle)
            context?.startDrawingActivity(model, DrawingViewActivity::class.java, false)
        }
    }

    private fun openMenuDialog(model: NewDrawing, root: ImageView, onFavourite: () -> Unit) {
        root.showPopupMenu(R.menu.gallery_frre_hand) {
            when (it.itemId) {
                R.id.openItem -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "freehand")
                    bundle.putString("user_id", model.author.userId)
                    bundle.putString(
                        "country",
                        if (selectedCountry == "") "WW" else selectedCountry
                    )
                    context?.sendUserEventWithParam(StringConstants.gallery_post_open, bundle)
                    context?.startDrawingActivity(model, DrawingViewActivity::class.java, false)
                }

//                R.id.favItem -> {
//                    StringConstants.gallery_freehand_xxxx_add_favs.replace(
//                        StringConstants.event_id,
//                        model.id
//                    )
//                    context?.showToastRelease("fav click!")
//                }

                R.id.learnDrawing -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "freehand")
                    bundle.putString("user_id", model.author.userId)
                    bundle.putString(
                        "country",
                        if (selectedCountry == "") "WW" else selectedCountry
                    )
                    context?.sendUserEventWithParam(
                        StringConstants.gallery_post_learn_drawing,
                        bundle
                    )
                    context?.openActivity(CategoryActivity::class.java)
                }


                R.id.favItem -> {

                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "freehand")
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

                }

                /*  val dao = appDatabase.drawingFavDao()

                  val drawing = DrawingEntity(
                      model.id ?: UUID.randomUUID().toString(),
                      model.author.name,
                      model.title,
                      model.description,
                      model.images.content,
                      model.createdAt,
                      model.uid,
                      model.tags,
                      model.type,
                      model.rating,
                      model.likes,
                      model.comments,
                      model.level,
                      model.totalPoints,
                      model.serverUserId,
                      model.youtubeLink,
                      model.path,
                      model.parentFolderPath,
                  )
                  Log.i("RoomFavourite", model.id ?: "")
                  CoroutineScope(Dispatchers.IO).launch {
                      val favourites = dao.favouritesIds
                      if (!favourites.contains(drawing.id)) {
                          dao.insert(drawing)
                      } else {
                          favourites.remove(drawing.id)
                          dao.delete(drawing)
                      }
                      withContext(Dispatchers.Main) {
                          onFavourite()
                      }
                  }
              }*/
            }

        }
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

            tvLikes.setTextColor(
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


            tvComments.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvComments.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvComments.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }
            tvTutorails.visibility = View.GONE
            /*tvTutorails.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvTutorails.text.toString())
                if (activity != null && activity is GalleryActivity) {
                    (activity as GalleryActivity?)?.navigateToFragmentOne()
                }
            }*/
            tvDateAscending.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvDateAscending.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvDateAscending.text.toString()))
                bottomSheetForFilter!!.dismiss()

            }



            tvDateDescending.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvDateDescending.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvDateDescending.text.toString()))
                bottomSheetForFilter!!.dismiss()

            }
            tvFreeHand.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvFreeHand.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvFreeHand.text.toString()))
                bottomSheetForFilter!!.dismiss()

            }
            tvRatings.onSingleClick {
                topLevel.onCreate().writeFilterItem(requireContext(), tvRatings.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvRatings.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvLikes.onSingleClick {
                topLevel.onCreate().writeFilterItem(requireContext(), tvLikes.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvLikes.text.toString()))
                bottomSheetForFilter!!.dismiss()
            }

            tvByLevels.onSingleClick {
                topLevel.onCreate()
                    .writeFilterItem(requireContext(), tvByLevels.text.toString())
                EventBus.getDefault().post(FilterChangeEvent(tvByLevels.text.toString()))
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
    }

    fun filerData() {
        if (currentFilter.equals("Comments", true)) {
            currentFilterBy = "statistic.comments:desc"
        } else if (currentFilter.equals("Likes", true)) {
            currentFilterBy = "statistic.likes:desc"
        } else if (currentFilter.equals("Date Ascending", true)) {
            currentFilterBy = "created_at:asc"
        } else if (currentFilter.equals("Date Descending (Default)", true)) {
            currentFilterBy = "created_at:desc"
        } else if (currentFilter.equals("Freehand (Default)", true)) {
            currentFilterBy = ""
        } else if (currentFilter.equals("Ratings", true)) {
            currentFilterBy = "statistic.ratings:desc"
        } else if (currentFilter.equals("By Levels", true)) {
            currentFilterBy = "${'$'}levels(sorting_number:asc)"
        }
        viewModel.fetchDrawingFreeHandList(
            pageNo,
            perPage,
            selectedCountry,
            "type:=freehand",
            currentFilterBy
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterChangeEvent(event: FilterChangeEvent) {
        pageNo = 1;
        currentFilter = event.filterType
        if (currentFilter.equals("Tutorials (Default)", true)) {
            currentFilter = "Freehand (Default)"
        }
        filerData()
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
}