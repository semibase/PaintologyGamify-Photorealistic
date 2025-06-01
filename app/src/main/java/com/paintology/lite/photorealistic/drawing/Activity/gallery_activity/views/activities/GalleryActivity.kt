package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.checkForIntroVideo
import com.paintology.lite.photorealistic.drawing.Activity.commonMenuClick
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.ToolbarButtonClickListener
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter.GalleryViewPager
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter.LeaderBoardCountriesAdapter
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.CountriesModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.utils.showPopupMenu
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.ads.callbacks.BannerCallBack
import com.paintology.lite.photorealistic.drawing.ads.enums.NativeType
import com.paintology.lite.photorealistic.drawing.databinding.ActivityGallery2Binding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetChallengeBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.events.GalleryEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class GalleryActivity : BaseActivity(), LeaderBoardCountriesAdapter.OnCountryClick {


    private var constant: StringConstants = StringConstants()
    private val binding by lazy {
        ActivityGallery2Binding.inflate(layoutInflater)
    }

    private var leaderBoardCountriesAdapter: LeaderBoardCountriesAdapter? = null
    private var countryDrawingsCount: MutableMap<String, Int> = mutableMapOf()
    private var countryFreehandCount: MutableMap<String, Int> = mutableMapOf()
    private var countryTutorialCount: MutableMap<String, Int> = mutableMapOf()
    private var countryChecker: MutableMap<String, Int> = mutableMapOf()
    private var bottomSheet: BottomSheetDialog? = null

    private var adapter: GalleryViewPager? = null
    var drawingType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        topLevel.onCreate().writeFlagId(this, R.drawable.img_world)
        topLevel.onCreate().writeLangName(this, "World")
        diComponent.admobNativeAds.loadNativeAds(this,
            binding.adsPlaceHolder,
            if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110" else diComponent.sharedPreferenceUtils.rcvNativeID,
            diComponent.sharedPreferenceUtils.rcvNativeCommunity,
            diComponent.sharedPreferenceUtils.isAppPurchased,
            diComponent.internetManager.isInternetConnected,
            NativeType.CUSTOM_DOWN,
            object : BannerCallBack {
                override fun onAdFailedToLoad(adError: String) {

                }

                override fun onAdLoaded() {

                }

                override fun onAdImpression() {

                }

                override fun onPreloaded() {

                }

                override fun onAdClicked() {

                }

                override fun onAdClosed() {

                }

                override fun onAdOpened() {

                }

                override fun onAdSwipeGestureClicked() {

                }

            })

        getIntentData()

        checkForIntroVideo(StringConstants.intro_gallery)


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
            filterBy = "type:=freehand"
        )
        viewModel.fetchDrawingListWithFacet(
            facetBy = "author.country",
            filterBy = "type:=tutorials"
        )
        initViewPagerAdepter()
        initToolbar()

        initListeners()

    }

    fun checkData() {
        val totalTutorials = countryTutorialCount[""] ?: 0
        binding.tabLayout.getTabAt(0)?.text = "${getString(R.string.tutorials)} (${totalTutorials})"

        val totalFreehands = countryFreehandCount[""] ?: 0
        binding.tabLayout.getTabAt(1)?.text = "${getString(R.string.freehand)} (${totalFreehands})"
    }


    private fun initListeners() {
        binding.apply {
            layoutCountries.onSingleClick {
                openCountriesButtonSheet()
            }

            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    topLevel.onCreate().clearDataStore(this@GalleryActivity)
                    topLevel.onCreate()
                        .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))

                }
            })

            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    topLevel.onCreate()
                        .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))
//                    topLevel.onCreate().clearDataStore(this@GalleryActivity)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    topLevel.onCreate()
                        .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))
//                    topLevel.onCreate().clearDataStore(this@GalleryActivity)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    topLevel.onCreate()
                        .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))
//                    topLevel.onCreate().clearDataStore(this@GalleryActivity)
                }
            })
        }

    }


    private fun getIntentData() {
        drawingType = intent?.getStringExtra("type").toString()
        if (drawingType == "freehand") {
            binding.viewPager2.post {
                binding.viewPager2.setCurrentItem(1, false)
            }
        } else if (drawingType == "tutorials") {
            binding.viewPager2.post {
                binding.viewPager2.setCurrentItem(0, false)
            }
        }
        sendUserEvent(StringConstants.home_screen_gallery)
        CoroutineScope(Dispatchers.Main).launch {
            topLevel.onCreate().readFlagId(this@GalleryActivity).collectLatest {
                binding.imgWorld.setImageResource(it)
            }
        }

        /*     CoroutineScope(Dispatchers.Main).launch {
                 topLevel.onCreate().readLangName(this@GalleryActivity).collectLatest {
                     binding.appCompatTextView11.text = it
                 }
             }*/
    }


    @SuppressLint("SetTextI18n")
    private fun initToolbar() {


        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
                topLevel.onCreate()
                    .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))
                topLevel.onCreate().clearDataStore(this@GalleryActivity)
            }


            // set country name at runtime when user change country it should change the
            // country name as well with selected country
            CoroutineScope(Dispatchers.Main).launch {
                topLevel.onCreate().readLangName(this@GalleryActivity).collectLatest {

                    if (it == "Gallery - World") {
                        tvTitle.text = getString(R.string.gallery)

                    } else {
                        tvTitle.text = "${getString(R.string.gallery)} - $it"

                    }

                }
            }


            ivMenu.visibility = View.VISIBLE
            ivMenu.onSingleClick {
                sendUserEvent("gallery_plus_dialog_post")
                ivMenu.showPopupMenu(R.menu.new_gallery_menu) {
                    commonMenuClick(it, StringConstants.intro_gallery)
                }
            }
            imgFav.apply {
                show()
                setImageResource(R.drawable.img_filter)
                onSingleClick {
                    // Get the current fragment from the ViewPager2 adapter
                    val currentFragment =
                        supportFragmentManager.findFragmentByTag("f${binding.viewPager2.currentItem}")
                    if (currentFragment is ToolbarButtonClickListener) {
                        currentFragment.onToolbarButtonClick()

                    }
                }
            }
        }
    }

    private fun initViewPagerAdepter() {
        adapter = GalleryViewPager(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            // Set tab text or custom view based on position
            when (position) {
                0 -> tab.text = getString(R.string.tutorials)
                1 -> tab.text = getString(R.string.freehand)
            }
        }.attach()
    }

    fun navigateToFragmentTwo() {
        binding.viewPager2.setCurrentItem(1, false)
    }

    fun navigateToFragmentOne() {
        binding.viewPager2.setCurrentItem(0, false)
    }
    /*
        override fun onBackPressed() {
            super.onBackPressed()
            topLevel.onCreate()
                .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))
            topLevel.onCreate().clearDataStore(this@GalleryActivity)

        }*/

    override fun onBackPressed() {
        val currentFragment =
            supportFragmentManager.findFragmentByTag("f${binding.viewPager2.currentItem}")

        /*  if (currentFragment is OnBackPressedListener) {
              if (!(currentFragment as OnBackPressedListener).onBackPressed()) {
                  super.onBackPressed()
                  clearDataStoreAndWriteFilterItem()
              }
          } else {
              super.onBackPressed()
              clearDataStoreAndWriteFilterItem()
          }
  */
        super.onBackPressed()
        clearDataStoreAndWriteFilterItem()
    }

    private fun clearDataStoreAndWriteFilterItem() {
        topLevel.onCreate()
            .writeFilterItem(this@GalleryActivity, getString(R.string.date_descending))
        topLevel.onCreate().clearDataStore(this@GalleryActivity)
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
        EventBus.getDefault().post(GalleryEvent(model, flag, position))

        val bundle = Bundle()
        bundle.putString("country", model.countryName.toString())
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

