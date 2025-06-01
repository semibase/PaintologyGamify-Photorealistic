package com.paintology.lite.photorealistic.drawing.challenge.view

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mak.cardflipview.views.CardFlipView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.challenge.ChallengeLevelsModel
import com.paintology.lite.photorealistic.drawing.challenge.TutorialChallengeMode
import com.paintology.lite.photorealistic.drawing.challenge.ViewModel.ChallengeActivityViewModel
import com.paintology.lite.photorealistic.drawing.challenge.adapter.ChallengeLevelsAdapter
import com.paintology.lite.photorealistic.drawing.challenge.adapter.ChallengePagerAdapter
import com.paintology.lite.photorealistic.drawing.challenge.adapter.LevelClick
import com.paintology.lite.photorealistic.drawing.challenge.enums.ChallengeEvent
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityChallengeBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetChallengeBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToastRelease
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class ChallengeActivity : AppCompatActivity(),LevelClick,
    CoroutineScope by MainScope() {

    private lateinit var challengeViewModel: ChallengeActivityViewModel
    private lateinit var binding: ActivityChallengeBinding
    lateinit var adapter: ChallengePagerAdapter

    private var dificulties: List<String> = emptyList()

    private val userCurrentLevel = "Beginner Bronze"

    private var challengeLevelsAdapter: ChallengeLevelsAdapter? = null

    val challengeLevelList: ArrayList<ChallengeLevelsModel> = ArrayList()

    private var progressDialog: ProgressDialog? = null

    private val defaultLink =
        ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/"

    lateinit var _object: PostDetailModel
    var tutorial_type: Tutorial_Type? = null

    private var userId = ""
    private val stringConstants = StringConstants()
    private val apiCallback : ChallengeApiCallback by lazy {
        ChallengeApiCallback(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolBar()
        challengeViewModel = ViewModelProvider(this)[ChallengeActivityViewModel::class.java]
        userId = stringConstants.getString(stringConstants.UserId, this@ChallengeActivity)
        // Images left navigation
        binding.leftNav.setOnClickListener {
            var tab: Int = binding.viewPager.currentItem
            if (tab > 0) {
                tab--
                binding.viewPager.currentItem = tab
            } else if (tab == 0) {
                binding.viewPager.currentItem = tab
            }
        }

        // Images right navigation
        binding.rightNav.setOnClickListener {
            var tab: Int = binding.viewPager.currentItem
            tab++
            binding.viewPager.currentItem = tab
        }

        setObservers()

        challengeViewModel.getChallengeList(userCurrentLevel) {
            adapter.notifyItemChanged(it)
        }
        challengeViewModel.getDifficultyLevels()

        setUpChallengeAdapter()
    }

    private fun setUpChallengeAdapter() {
        adapter = ChallengePagerAdapter {
            when (it) {
                is ChallengeEvent.OnDetailClick -> {

                }

                is ChallengeEvent.OnGalleryClick -> {
                    val intent = Intent(this@ChallengeActivity, GalleryActivity::class.java)
                    startActivity(intent)
                }

                is ChallengeEvent.OnLevelMeterClick -> {
                    openButtonSheetOfMultipleImages()
                }

                is ChallengeEvent.OnLikeClick -> {
                    handleLikeDislike(it.data, it.pos)
                }

                is ChallengeEvent.OnOpenTutorial -> {
                    val catId = it.data.custom_fields?.tutorial_data?.category_id ?: return@ChallengePagerAdapter
                    val posId = it.data.custom_fields?.tutorial_data?.id ?: return@ChallengePagerAdapter
                    lifecycleScope.launch {
                        apiCallback.getChallengeInfo(catId.toString(),posId.toString())
                    }

                }
            }
        }
        binding.viewPager.adapter = adapter
    }

    private fun handleLikeDislike(data: TutorialChallengeMode, pos: Int) {
        val challengeKey = data.key ?: return
        FirebaseFirestoreApi.addLike(challengeKey, onSuccess = {
            val adapterList = adapter.getItemList()[pos]
            // Update the like count in the stats object of the item
            adapterList.statistic.likes =   adapterList.statistic.likes + 1

            // Notify the adapter that the data has changed
            adapter.notifyItemChanged(pos)
        }, onFailure = {
            val adapterList = adapter.getItemList()[pos]
            // Update the like count in the stats object of the item
            adapterList.statistic.likes =   adapterList.statistic.likes - 1

            // Notify the adapter that the data has changed
            adapter.notifyItemChanged(pos)
            FirebaseFirestoreApi.removeLike(challengeKey)
        })
    }

    private fun setObservers() {
        challengeViewModel.tutorialChallengeList.observe(this) { list ->
            Log.e("Challenges", "$list")
            adapter.setData(ArrayList(list))
        }

        challengeViewModel.difficultyLevels.observe(this) { list ->
            Log.e("difficultyLevels", "$list")
            dificulties = list
            dificulties.forEachIndexed { index, s ->
                challengeLevelList.add(
                    ChallengeLevelsModel(
                        R.drawable.img_bronze,
                        s,
                        "50 users are participating in this challenge"
                    )
                )
            }
        }
    }

    private fun initToolBar() {
        with(binding.toolbar1) {
            imgMenu.apply {
                onSingleClick {
                    onBackPressed()
                }
            }
            tvTitle.text = getString(R.string.challenges)
            imgFav.hide()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openButtonSheetOfMultipleImages() {
        val bottomSheet = BottomSheetDialog(this)
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

        challengeLevelsAdapter = ChallengeLevelsAdapter(
            emptyList(),
            challengeLevelList,
            userCurrentLevel,
            this
        )
        with(dialogBinding.rvImagesList) {
            layoutManager = LinearLayoutManager(this@ChallengeActivity)
            adapter = challengeLevelsAdapter
        }

        bottomSheet.show()
    }

    override fun onBackPressed() {
        if (binding.viewPager.findViewWithTag<CardFlipView>("CardFlipView${binding.viewPager.currentItem}")?.isBackSide == true) {
            binding.viewPager.findViewWithTag<CardFlipView>("CardFlipView${binding.viewPager.currentItem}")
                ?.flipTheView(true)
            binding.viewPager.isUserInputEnabled = true
            binding.leftNav.isVisible = true
            binding.rightNav.isVisible = true
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCLick(position: Int, model: ChallengeLevelsModel) {
        showToastRelease(model.titleChallenge.toString())
    }

}

