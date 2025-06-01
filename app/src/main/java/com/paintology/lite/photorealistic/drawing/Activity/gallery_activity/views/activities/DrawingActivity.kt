package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.Lists.getCountryFlagResource
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.UserGalleryViewPagerAdapter
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter.LeaderBoardCountriesAdapter
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.CountriesModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.Constants_Gallery
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityDrawingBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetChallengeBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.events.UserGalleryEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class DrawingActivity : BaseActivity(), LeaderBoardCountriesAdapter.OnCountryClick {

    private val binding by lazy {
        ActivityDrawingBinding.inflate(layoutInflater)
    }


    private var leaderBoardCountriesAdapter: LeaderBoardCountriesAdapter? = null
    private var countryDrawingsCount: MutableMap<String, Int> = mutableMapOf()
    private var bottomSheet: BottomSheetDialog? = null

    private var countryFreehandCount: MutableMap<String, Int> = mutableMapOf()
    private var countryTutorialCount: MutableMap<String, Int> = mutableMapOf()
    private var getModel: NewDrawing? = null
    private var adapter: UserGalleryViewPagerAdapter? = null
    var drawingType: String = ""

    var IsActityOpenedFirstTime: Boolean = false;

    companion object {
        var userID = ""
        var userName: String? = null
        var isShowingUserLevelData = false
        var userLevel = "Beginner 1"
        var userLevelFromBottomSheet = "Beginner 1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val country = getCountryFlagResource(intent.getStringExtra("country").toString())
        topLevel.onCreate().writeFlagId(this@DrawingActivity, country)

        getIntentData()

        if (isShowingUserLevelData) {

            binding.ivArrow.visibility = View.VISIBLE
            /*  viewModel.countryList.observe(this) { drawingList ->
                  if (drawingList.facetCounts.isNotEmpty()) {
                      for (count in drawingList.facetCounts[0].counts) {
                          countryDrawingsCount[count.highlighted] = count.count
                      }

                      val total = countryDrawingsCount.values.sumOf { it }
                      countryDrawingsCount[""] = drawingList.page.totalElements - total;
                  } else {
                      countryDrawingsCount[""] = drawingList.page.totalElements;
                  }
              }

              viewModel.fetchDrawingListWithFacet(
                  facetBy = "author.country",
                  filterBy = "author.level:=$userLevel&&type:=freehand"
              )*/

            viewModel.countryTutorialList.observe(this) { drawingList ->
                if (drawingList.facetCounts.isNotEmpty()) {
                    for (count in drawingList.facetCounts[0].counts) {
                        val oldCount = countryDrawingsCount[count.highlighted] ?: 0
                        val tot = oldCount + count.count
                        countryDrawingsCount[count.highlighted] = tot
                        countryTutorialCount[count.highlighted] = count.count
                    }
                    val oldCount2 = countryDrawingsCount[""] ?: 0
                    val total = countryTutorialCount.values.sumOf { it }
                    val finalTotal = drawingList.page.totalElements - total;
                    countryTutorialCount[""] = drawingList.page.totalElements
                    countryDrawingsCount[""] = oldCount2 + finalTotal
                } else if (drawingList.page.totalElements > 0) {
                    val oldCount2 = countryDrawingsCount[""] ?: 0
                    countryDrawingsCount[""] = oldCount2 + drawingList.page.totalElements;
                    countryTutorialCount[""] = drawingList.page.totalElements
                }
                checkData()
            }
            viewModel.countryFreehandList.observe(this) { drawingList ->
                if (drawingList.facetCounts.isNotEmpty()) {
                    for (count in drawingList.facetCounts[0].counts) {
                        val oldCount = countryDrawingsCount[count.highlighted] ?: 0
                        val tot = oldCount + count.count
                        countryDrawingsCount[count.highlighted] = tot
                        countryFreehandCount[count.highlighted] = count.count
                    }
                    val oldCount2 = countryDrawingsCount[""] ?: 0
                    val total = countryFreehandCount.values.sumOf { it }
                    val finalTotal = drawingList.page.totalElements - total;
                    countryFreehandCount[""] = drawingList.page.totalElements
                    countryDrawingsCount[""] = oldCount2 + finalTotal
                } else if (drawingList.page.totalElements > 0) {
                    val oldCount2 = countryDrawingsCount[""] ?: 0
                    countryDrawingsCount[""] = oldCount2 + drawingList.page.totalElements;
                    countryFreehandCount[""] = drawingList.page.totalElements
                }
                checkData()
            }


            viewModel.fetchDrawingListWithFacet(
                facetBy = "author.country",
                filterBy = "author.level:=$userLevel&&type:=freehand"
            )

            viewModel.fetchDrawingListWithFacet(
                facetBy = "author.country",
                filterBy = "author.level:=$userLevel&&type:=tutorials"
            )
        } else {
            binding.ivArrow.visibility = View.INVISIBLE
            viewModel.userDrawingsList.observe(this) { drawingList ->

                if (drawingList.facetCounts.isNotEmpty() && drawingList.facetCounts[0].stats.totalValues > 0) {

                    for (count in drawingList.facetCounts[0].counts) {
                        if (count.highlighted == "freehand") {
                            binding.tabLayout.getTabAt(1)?.text =
                                "${getString(R.string.freehand)} (${count.count})"
                        } else if (count.highlighted == "tutorials") {
                            binding.tabLayout.getTabAt(0)?.text =
                                "${getString(R.string.tutorials)} (${count.count})"
                        }
                    }

                    if (drawingList.facetCounts[0].stats.totalValues == 1) {
                        if (drawingList.facetCounts[0].counts.get(0).highlighted == "freehand") {
                            binding.tabLayout.getTabAt(0)?.text = getString(R.string.tutorials)
                            binding.viewPager2.currentItem = 1
                        } else {
                            binding.tabLayout.getTabAt(1)?.text = getString(R.string.freehand)
                        }
                    }
                } else {
                    binding.tabLayout.getTabAt(0)?.text = getString(R.string.tutorials)
                    binding.tabLayout.getTabAt(1)?.text = getString(R.string.freehand)
                }
            }

            viewModel.fetchDrawingListWithFacet(
                facetBy = "type",
                filterBy = "author.user_id:=${getModel?.author?.userId}"
            )
        }
        initToolbar()

        initListeners()

        IsActityOpenedFirstTime = true

    }

    fun checkData() {
        val totalTutorials = countryTutorialCount[""] ?: 0
        binding.tabLayout.getTabAt(0)?.text =
            "${getString(R.string.tutorials)} (${totalTutorials})"

        val totalFreehands = countryFreehandCount[""] ?: 0
        binding.tabLayout.getTabAt(1)?.text =
            "${getString(R.string.freehand)} (${totalFreehands})"
    }


    /*@Subscribe(threadMode = ThreadMode.MAIN)
    fun onTutorialChangeEvent(event: TutorialCountEvent) {
        if (!isShowingUserLevelData && binding.tabLayout.tabCount > 0) {
            if (event.totalCount > 0) {
                binding.tabLayout.getTabAt(0)?.text =
                    "${getString(R.string.tutorials)} (${event.totalCount})"
            } else {
                binding.tabLayout.getTabAt(0)?.text = getString(R.string.tutorials)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFreehandChangeEvent(event: FreeHandCountEvent) {
        if (!isShowingUserLevelData && binding.tabLayout.tabCount > 0) {
            if (event.totalCount > 0) {
                binding.tabLayout.getTabAt(1)?.text =
                    "${getString(R.string.freehand)} (${event.totalCount})"
            } else {
                binding.tabLayout.getTabAt(1)?.text = getString(R.string.freehand)
            }
        }
    }*/

    private fun initListeners() {
        binding.apply {

            layoutCountries.isEnabled = isShowingUserLevelData
            layoutCountries.onSingleClick {
                openCountriesButtonSheet()
            }

            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    topLevel.onCreate().clearDataStore(this@DrawingActivity)
                    updateToolbarTitle(position)
                }
            })

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    topLevel.onCreate().clearDataStore(this@DrawingActivity)
                    tab?.let { updateToolbarTitle(it.position) }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                    topLevel.onCreate().clearDataStore(this@DrawingActivity)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
//                    topLevel.onCreate().clearDataStore(this@DrawingActivity)
                }
            })

        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateToolbarTitle(position: Int) {
        if (!isShowingUserLevelData) {
            val title = when (position) {
                0 -> "${getString(R.string.tutorials)} - ${userName ?: ""}"
                1 -> "${getString(R.string.freehand)} - ${userName ?: ""}"
                else -> userName ?: ""
            }
            binding.customToolbar.tvTitle.text = title
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initViewPagerAdepter() {
        adapter = UserGalleryViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = 2
        // showing user LEVEL DATA LIKE BEGINNER 1
        if (isShowingUserLevelData) {

            // check user drawing type and show fragment accordingly
            drawingType = intent.getStringExtra("drawingType").toString()
            Log.d("drawingTypeTAG", "getIntentData: $drawingType")
            // set fragments
            if (drawingType == "freehand") {
                binding.viewPager2.post {
                    binding.viewPager2.setCurrentItem(1, false)
                }
            } else if (drawingType == "tutorials") {
                binding.viewPager2.post {
                    binding.viewPager2.setCurrentItem(0, false)
                }
            }

            // set title
            if (userLevelFromBottomSheet == "") {

                if (getModel?.author?.level.isNullOrEmpty()) {
                    binding.customToolbar.tvTitle.text =
                        "${Constants_Gallery.Beginner_1} - All Drawings"
                } else {

                    // country name as well with selected country
                    CoroutineScope(Dispatchers.Main).launch {
                        topLevel.onCreate().readLangName(this@DrawingActivity).collectLatest {
                            if (it == "Gallery - World") {
                                binding.customToolbar.tvTitle.text =
                                    "${getModel?.author?.level} - All Drawings"
                            } else {
                                binding.customToolbar.tvTitle.text =
                                    "${getModel?.author?.level} - $it"

                            }
                        }
                    }

                }
            } else {
                binding.customToolbar.tvTitle.text = "$userLevel - All Drawings"
            }

            TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.tutorials)
                    1 -> tab.text = getString(R.string.freehand)
                }
            }.attach()
        } else {
            TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.tutorials)
                    1 -> tab.text = getString(R.string.freehand)
                }
            }.attach()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getIntentData() {
        val intent = intent
        getModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("drawing_model", NewDrawing::class.java)
        } else {
            intent.getParcelableExtra("drawing_model")
        }
        userID = getModel?.author?.userId.toString()

        // check user level if null assign by default

        isShowingUserLevelData = intent.getBooleanExtra("isShowingUserLevelData", false)
        userLevelFromBottomSheet = intent.getStringExtra("userLevelFromBottom").toString()

        if (userLevelFromBottomSheet == "") {
            userLevel = if (getModel?.author?.level.isNullOrEmpty()) {
                "Beginner 1"
            } else {
                getModel?.author?.level.toString()
            }
        } else {
            userLevel = userLevelFromBottomSheet
        }




        userName = getModel?.author?.name


        lifecycleScope.launch {

            viewModel.fetchDrawingStagingList(
                1,
                50,
                "",
                "author.user_id:=${getModel?.author?.userId.toString()}",
                ""
            )


            /* viewModel.tutorialDrawings.observe(this@DrawingActivity) { drawings ->
                 tutorialCount = drawings.size
                 initViewPagerAdepter()
             }
             viewModel.freehandDrawings.observe(this@DrawingActivity) { drawings ->
                 freeHandCount = drawings.size
                 initViewPagerAdepter()
             }*/

            initViewPagerAdepter()

        }

        CoroutineScope(Dispatchers.Main).launch {

            topLevel.onCreate().readFlagId(this@DrawingActivity).collectLatest {
                binding.imgWorld.setImageResource(it)
            }

        }


        // set flag according to the country
        /*val flagResourceId = Lists.getCountryFlagResource(getModel?.author?.country.toString())
        binding.imgWorld.setImageResource(flagResourceId)*/

    }

    override fun onBackPressed() {
        super.onBackPressed()
        topLevel.onCreate().clearDataStore(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
                topLevel.onCreate().clearDataStore(this@DrawingActivity)
            }
            if (!isShowingUserLevelData) {
                tvTitle.text = "${getModel?.author?.name} - Gallery"
            }
            imgFav.hide()
        }
    }

    private fun openCountriesButtonSheet() {
        bottomSheet = BottomSheetDialog(this)
        val bottomSheetBehavior: BottomSheetBehavior<View>?

        val dialogBinding: LayoutBottomSheetChallengeBinding =
            LayoutBottomSheetChallengeBinding.inflate(layoutInflater)
        bottomSheet?.setContentView(dialogBinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.bottomSheet.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        val layout: CoordinatorLayout? = bottomSheet?.findViewById(R.id.bottomSheet)
        val layoutParams = layout?.layoutParams
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.55).toInt()
        // Set the height of the bottom sheet
        layoutParams?.height = desiredHeight// Apply the new layout parameters
        layout?.layoutParams = layoutParams

        val sortedCountries = Lists.countriesList.take(1) + Lists.countriesList.drop(1)
            .sortedByDescending {
                countryDrawingsCount.getOrDefault(
                    it.countryCode,
                    0
                ) + countryDrawingsCount.getOrDefault(it.countryName, 0)
            }
        leaderBoardCountriesAdapter = LeaderBoardCountriesAdapter(
            leaderBoardCountriesList = sortedCountries.toMutableList(),
            onCountryClick = this,
            topLevel = topLevel,
            context = this,
            isFromLeaderActivity = false,
            countryDrawingsCount = countryDrawingsCount
        )
        with(dialogBinding.rvImagesList) {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderBoardCountriesAdapter
        }

        bottomSheet?.show()
    }

    override fun countryClick(model: CountriesModel, flag: Int?, position: Int) {
        EventBus.getDefault().post(UserGalleryEvent(model, flag, position))

        val bundle = Bundle()
        bundle.putString("country",  model.countryName.toString())
        sendUserEventWithParam(StringConstants.gallery_post_filter, bundle)

        if (position == 0) {
            val totalFreehands = countryFreehandCount[""] ?: 0
            val totalTutorials = countryTutorialCount[""] ?: 0

            binding.tabLayout.getTabAt(0)?.text =
                "${getString(R.string.tutorials)} (${totalTutorials})"

            binding.tabLayout.getTabAt(1)?.text =
                "${getString(R.string.freehand)} (${totalFreehands})"
        } else {

            val totalFreehands = countryFreehandCount[model.countryCode] ?: 0
            val totalTutorials = countryTutorialCount[model.countryCode] ?: 0

            binding.tabLayout.getTabAt(0)?.text =
                "${getString(R.string.tutorials)} (${totalTutorials})"

            binding.tabLayout.getTabAt(1)?.text =
                "${getString(R.string.freehand)} (${totalFreehands})"
        }

        if (bottomSheet?.isShowing == true) {
            bottomSheet?.dismiss()
        }
    }
}