package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.ToolbarButtonClickListener
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.GalleryTutorialsAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity.Companion.isShowingUserLevelData
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity.Companion.userID
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity.Companion.userLevel
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
import com.paintology.lite.photorealistic.drawing.databinding.FragmentUserGalleryFreeHandBinding
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

const val TAG = "UserGalleryFreeHand"

class UserGalleryFreeHandFragment : BaseFragment(), GalleryTutorialsAdapter.OnGalleryMenuClick,
    ToolbarButtonClickListener {


    private var pageNo = 1
    private val perPage = 10
    private var isLoading = false
    private var isLastPage = false

    private lateinit var drawingRepository: DrawingRepository
    lateinit var binding: FragmentUserGalleryFreeHandBinding
    private var galleryTutorialsAdapter: GalleryTutorialsAdapter? = null
    private var drawingList = mutableListOf<NewDrawing>()

    private var selectedCountry = "";


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserGalleryFreeHandBinding.inflate(inflater, container, false)


        drawingRepository = DrawingRepository(context)


        // Observe freehandDrawings LiveData
        viewModel.freehandDrawings.observe(viewLifecycleOwner) { freehandDrawings ->
            Log.d(TAG, "onCreateView: ${freehandDrawings.size}")

            if (freehandDrawings.isNotEmpty()) {
                drawingList.clear()
                isLastPage = false// TO load next data
                drawingList.addAll(freehandDrawings)
            } else {
                if (pageNo == 1) {
                    drawingList.clear()
                }
                isLastPage = true// TO load next data
            }

            isLoading = false // TO load next data

            if (drawingList.size <= 0) {
                binding.layoutNoData.show()
                binding.rvBlogPosts.hide()
            } else {
                binding.layoutNoData.hide()
                binding.rvBlogPosts.show()
            }

            galleryTutorialsAdapter?.notifyDataSetChanged()

        }

        if (isShowingUserLevelData) {
            viewModel.fetchDrawingFreeHandList(
                pageNo,
                perPage,
                selectedCountry,
                "author.level:=$userLevel&&type:=freehand",
                ""
            )
        } else {
            viewModel.fetchDrawingFreeHandList(
                pageNo,
                perPage,
                "",
                "author.user_id:=$userID&&type:=freehand",
                ""
            )
        }

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
                    false,
                    drawingList,
                    this@UserGalleryFreeHandFragment,
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
                                    if (isShowingUserLevelData) {
                                        viewModel.fetchDrawingFreeHandList(
                                            pageNo,
                                            perPage,
                                            selectedCountry,
                                            "author.level:=$userLevel&&type:=freehand",
                                            ""
                                        )
                                    } else {
                                        viewModel.fetchDrawingFreeHandList(
                                            pageNo,
                                            perPage,
                                            "",
                                            "author.user_id:=$userID&&type:=freehand",
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
        openMenuDialog(model, root, onFavourite)
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


    private fun openMenuDialog(model: NewDrawing, root: ImageView, onFavourite: () -> Unit) {
        root.showPopupMenu(R.menu.gallery_frre_hand) {
            when (it.itemId) {
                R.id.openItem -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "freehand")
                    bundle.putString("user_id", model.author.userId)
                    bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
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
                    bundle.putString("country", if (selectedCountry == "") "WW" else selectedCountry)
                    context?.sendUserEventWithParam(StringConstants.gallery_post_learn_drawing, bundle)
                    context?.openActivity(CategoryActivity::class.java)
                }


                R.id.favItem -> {
                    val bundle = Bundle()
                    bundle.putString("post_id", model.id)
                    bundle.putString("post_type", "freehand")
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

    /*  @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
      override fun countryClick(model: CountriesModel, flag: Int?, position: Int) {
          Log.i("CountryOnClick", model.countryCode.toString())
          topLevel.onCreate().writePosition(requireContext(), position)
          flag?.let { topLevel.onCreate().writeFlagId(requireContext(), it) }
          topLevel.onCreate().writeLangName(requireContext(), model.countryName ?: "")


          if (isShowingUserLevelData) {

              viewModel.fetchDrawingStagingList(
                  1,
                  10,
                  model.countryCode.toString(),
                  "author.level:=$userLevel",
                  ""
              )
          } else {
              viewModel.fetchDrawingStagingList(
                  1,
                  10,
                  model.countryCode.toString(),
                  "author.user_id:=$userLevel",
                  ""
              )
          }

          viewModel.tutorialDrawings.observe(viewLifecycleOwner) { levelFreehandDrawings ->
  //            Log.e(TAG, "BeginnerDrawings onCreateView: $levelFreehandDrawings")

              drawingList.clear()
              if (levelFreehandDrawings.isEmpty()) {
                  binding.layoutNoData.show()
                  binding.appCompatTextView11.text = "No drawings at this level, be the first!\n" +
                          "\n" +
                          "Do a drawing tutorial or make a freehand drawing\n"
                  binding.appCompatTextView14.hide()
                  binding.rvBlogPosts.hide()
              } else {
                  binding.layoutNoData.hide()
                  binding.rvBlogPosts.show()
              }
              drawingList.addAll(levelFreehandDrawings)
              galleryTutorialsAdapter?.notifyDataSetChanged()
              countryDrawingsCount = levelFreehandDrawings.groupBy { it.author.country ?: "" }
                  .mapValues { it.value.size }


          }

          if (bottomSheet?.isShowing == true) {
              bottomSheet?.dismiss()
          }
      }
  */


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGalleryChangeEvent(event: UserGalleryEvent) {
        // topLevel.onCreate().writePosition(requireContext(), position)
        pageNo = 1;

        Log.i("CountryOnClick", event.model.countryCode ?: "")
        event.flag?.let { topLevel.onCreate().writeFlagId(requireContext(), it) }
        topLevel.onCreate().writeLangName(requireContext(), event.model.countryName ?: "")

        if (event.position == 0) {
            selectedCountry = ""
            viewModel.fetchDrawingFreeHandList(
                pageNo, perPage, "",
                "author.level:=$userLevel&&type:=freehand", "created_at:desc"
            )
        } else {
            selectedCountry = event.model.countryCode.toString()
            viewModel.fetchDrawingFreeHandList(
                pageNo, perPage, event.model.countryCode.toString(),
                "author.level:=$userLevel&&type:=freehand", "created_at:desc"
            )
        }
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
    override fun onToolbarButtonClick() {}
}