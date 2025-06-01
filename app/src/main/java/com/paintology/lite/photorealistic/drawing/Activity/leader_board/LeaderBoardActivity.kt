package com.paintology.lite.photorealistic.drawing.Activity.leader_board

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.checkForIntroVideo
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter.LeaderBoardCountriesAdapter
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter.LeaderBoardRankingAdapter
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter.YourRankingAdapterLevels
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.CountriesModel
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.CountryInfo
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.LeaderBoardRankingModel
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.LeaderBoardRankingModel.Companion.getUserBG
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.LeaderBoardRankingModel.Companion.getUserBadgeIcon
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityLeaderBoardBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetChallengeBinding
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class LeaderBoardActivity : BaseActivity(), LeaderBoardCountriesAdapter.OnCountryClick,
    YourRankingAdapterLevels.OnCountryClick {
    private val binding by lazy {
        ActivityLeaderBoardBinding.inflate(layoutInflater)
    }
    var constants = StringConstants()

    private lateinit var progressDialog: ProgressDialog
    private var bottomSheet: BottomSheetDialog? = null
    private var bottomSheetLevels: BottomSheetDialog? = null
    private var CountryCode: String? = ""
    private var countryName: String? = ""
    private var Levels: String? = ""

    private var leaderBoardRankingAdapter: LeaderBoardRankingAdapter? = null
    private var leaderBoardCountriesAdapter: LeaderBoardCountriesAdapter? = null
    private val countriesData = ArrayList<CountryInfo>()
    private var yourRankingAdapter: YourRankingAdapterLevels? = null

    private var isBottomSheetOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.load))
        topLevel.onCreate().writePosition(this, 0)

        checkForIntroVideo(StringConstants.intro_leaderboard)

        val targetName = intent.getStringExtra("target_name")
        if (!targetName.isNullOrEmpty()) {
            if (targetName == "level") {
                val targetId = intent.getStringExtra("target_id")
                if (!targetId.isNullOrEmpty()) {
                    val level = intent.getStringExtra("target_id")
                    initToolbar(" - $level")
                    fetchLeaderBoardData(levelBY = level)
                } else {
                    val level =
                        sharedPref.getString(StringConstants.user_level, StringConstants.beginner)
                            .toString()
                    initToolbar(" - $level")
                    fetchLeaderBoardData(levelBY = level)
                }
            } else {
                val targetId = intent.getStringExtra("target_id")
                if (!targetId.isNullOrEmpty()) {
                    initToolbar(" - $targetId")
                    fetchLeaderBoardData(
                        countryCode = targetId,
                        countryName = targetId
                    )
                } else {
                    val country =
                        constants.getString(constants.UserCountry, this)
                            .toString()
                    if(country == "")
                    {
                        initToolbar(" - World")
                        fetchLeaderBoardData()
                    }else{
                        initToolbar(" - $country")
                        fetchLeaderBoardData(
                            countryCode = country,
                            countryName = country
                        )
                    }
                }
            }
        } else if (intent.getStringExtra("tvRankLevel") != null) {
            initToolbar(" - ${intent.getStringExtra("tvRankLevel")}")
            fetchLeaderBoardData(levelBY = intent.getStringExtra("tvRankLevel"))
        } else {
            initToolbar(" - World")
            fetchLeaderBoardData()
        }
    }

    private fun fetchLeaderBoardData(
        countryCode: String? = null,
        countryName: String? = null,
        levelBY: String? = null
    ) {
        binding.loadingView.isVisible = true
        //Make null on WordWide selected
        val code = if (countryCode == "WW") {
            null
        } else {
            countryCode
        }

        val level = if (levelBY.isNullOrEmpty()) {
            null
        } else {
            levelBY
        }

        val bundle = Bundle()
        bundle.putString("country", countryCode)
        bundle.putString("level", levelBY)
        sendUserEventWithParam(StringConstants.leaderboards_filter, bundle)


        this.CountryCode = countryCode
        this.Levels = level

        val name = if (countryCode == "WW") {
            null
        } else {
            countryName
        }

        if (countryCode == null || countryCode == "WW") {
            viewModel.fetchUserListWithFacet(countryBy = null)
        } else {
            viewModel.fetchUserListWithFacet(countryBy = "country:=${countryCode}")
        }

        setCurrentUserRank(code)

        rankingViewModel.topUsers.observe(this) { users ->
            users?.let { userList ->
                val top3 = userList.take(3)
                val remainingUsers = userList.drop(3)
                setTop3Rank(name, top3)
                initRecyclerView(remainingUsers)
                binding.loadingView.isVisible = false
            }
        }

        rankingViewModel.fetchTopUsers(level = level, countryCode = code)

        /*FirebaseUtils.getUsersByPoints(code) { userList ->
            val top3 = userList.take(3)
            val remainingUsers = userList.drop(3)
            setTop3Rank(name, top3)
            initRecyclerView(remainingUsers)
            binding.loadingView.isVisible = false
        }*/
    }

    @SuppressLint("SetTextI18n")
    fun setTop3Rank(countryName: String?, user: List<LeaderBoardRankingModel>) {
        val currentUserId = FirebaseUtils.getCurrentUserId(this@LeaderBoardActivity)
        binding.apply {
            //RANK 1
            user.getOrNull(0)?.let { user ->
                binding.layoutFirstPerson.isVisible = true
                tvFirstRankName.text = user.name.toString()
                tvPointsRank1.text = user.points.toString()
                loadImage(iv = binding.imgPersonFirstPosition, imgUrl = user.avatar)
                imgPersonFirstPosition.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("rank", "1")
                    if (countryName == null) {
                        bundle.putString("country", "")
                    } else {
                        bundle.putString("country", countryName)
                    }
                    bundle.putString("user_id", user.docId)
                    sendUserEventWithParam(StringConstants.leaderboards_open_user, bundle)
                    FireUtils.openProfileScreen(this@LeaderBoardActivity, user.docId)
                }


                if (user.getUserBadgeIcon() != null) {
                    binding.llLevel1.isVisible = true
                    ivIconOfRank1.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@LeaderBoardActivity,
                            user.getUserBadgeIcon()!!
                        )
                    )
                    tvLevelOfRank1.text = user.level.toString()
                } else {
                    binding.llLevel1.isVisible = false
                }
            } ?: run {
                binding.layoutFirstPerson.isVisible = false
            }
            //RANK 2
            user.getOrNull(1)?.let { user ->
                binding.layoutSecondPerson.isVisible = true
                tvSecondRankName.text = user.name.toString()
                tvPointsRank2.text = user.points.toString()
                loadImage(iv = binding.imgPersonSecondPosition, imgUrl = user.avatar)
                imgPersonSecondPosition.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("rank", "2")
                    if (countryName == null) {
                        bundle.putString("country", "")
                    } else {
                        bundle.putString("country", countryName)
                    }
                    bundle.putString("user_id", user.docId)
                    sendUserEventWithParam(StringConstants.leaderboards_open_user, bundle)
                    FireUtils.openProfileScreen(this@LeaderBoardActivity, user.docId)
                }
                if (user.getUserBadgeIcon() != null) {
                    binding.llLevel2.isVisible = true
                    ivIconOfRank2.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@LeaderBoardActivity,
                            user.getUserBadgeIcon()!!
                        )
                    )
                    tvLevelOfRank2.text = user.level.toString()
                } else {
                    binding.llLevel2.isVisible = false
                }
            } ?: run { binding.layoutSecondPerson.isVisible = false }
            //RANK 3
            user.getOrNull(2)?.let { user ->
                binding.layoutThirdPerson.isVisible = true
                tvThirdRankName.text = user.name.toString()
                tvPointsRank3.text = user.points.toString()
                loadImage(iv = binding.imgPersonThirdPosition, imgUrl = user.avatar)
                imgPersonThirdPosition.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("rank", "3")
                    if (countryName == null) {
                        bundle.putString("country", "")
                    } else {
                        bundle.putString("country", countryName)
                    }
                    bundle.putString("user_id", user.docId)
                    sendUserEventWithParam(StringConstants.leaderboards_open_user, bundle)
                    FireUtils.openProfileScreen(this@LeaderBoardActivity, user.docId)
                }
                if (user.getUserBadgeIcon() != null) {
                    binding.llLevel3.isVisible = true
                    ivIconOfRank3.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@LeaderBoardActivity,
                            user.getUserBadgeIcon()!!
                        )
                    )
                    tvLevelOfRank3.text = user.level.toString()
                } else {
                    binding.llLevel3.isVisible = false
                }
            } ?: run {
                binding.layoutThirdPerson.isVisible = false
            }
        }
    }

    private fun loadImage(iv: ImageView, imgUrl: String? = null) {
        if (this.isFinishing) {
            return
        }
        imgUrl?.let {
            Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.img_default_avatar)  // Placeholder image
                .into(iv)
        }
    }

    private fun initToolbar(countryName: String) {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                if (CountryCode == "WW" || CountryCode == null) {
                    finish()
                } else {
                    ReturnToWorldWide()
                }

            }

            tvTitle.text = buildString {
                append(getString(R.string.leaderboard))
                appendln(countryName)
            }

            tvTitle.textSize = 15f
            dropDown.hide()
            dots.show()
            layoutCountries.onSingleClick {
                getCountriesData()
            }
            dots.onSingleClick {
                getLevelsData()
            }
        }
    }

    private fun getLevelsData() {
        if (Lists.rankingList.isNotEmpty()) {
            openCountriesButtonSheetLevels()
            return
        }
    }

    private fun openCountriesButtonSheetLevels() {
        bottomSheetLevels = BottomSheetDialog(this)
        val bottomSheetBehavior: BottomSheetBehavior<View>?

        val dialogBinding: LayoutBottomSheetChallengeBinding =
            LayoutBottomSheetChallengeBinding.inflate(layoutInflater)
        bottomSheetLevels?.setContentView(dialogBinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.bottomSheet.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        val layout: CoordinatorLayout? = bottomSheetLevels?.findViewById(R.id.bottomSheet)
        val layoutParams = layout?.layoutParams
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.52).toInt()
        // Set the height of the bottom sheet
        layoutParams?.height = desiredHeight// Apply the new layout parameters
        layout?.layoutParams = layoutParams

        yourRankingAdapter = YourRankingAdapterLevels(Lists.rankingList, this)
        dialogBinding.rvImagesList.apply {
            layoutManager = LinearLayoutManager(this@LeaderBoardActivity)
            adapter = yourRankingAdapter
            val list = Lists.rankingList
            scrollToPosition(list.size - 1)
        }

        bottomSheetLevels?.show()
    }

    override fun countryClick(model: YourRankingModel) {
        fetchLeaderBoardData(countryCode = "WW", levelBY = model.tvRankLevel)
        initToolbar(" - " + model.tvRankLevel)
        bottomSheetLevels?.dismiss()
    }

    private fun ReturnToWorldWide() {
        fetchLeaderBoardData(countryCode = "WW", levelBY = null)
        initToolbar(" - World")
        topLevel.onCreate().writePosition(this@LeaderBoardActivity, 0)
        binding.customToolbar.imgWorld.setImageDrawable(
            ContextCompat.getDrawable(
                this@LeaderBoardActivity,
                R.drawable.img_world
            )
        )
    }

    override fun onBackPressed() {
        if ((CountryCode == null || CountryCode == "WW") && Levels.isNullOrEmpty()) {
            super.onBackPressed()
        } else {
            ReturnToWorldWide()
        }
    }

    private fun initRecyclerView(userList: List<LeaderBoardRankingModel>) {
        binding.apply {
            if (userList.isEmpty()) {
                rvLeaderBoard.isVisible = false
                tvNoUsers.isVisible = true
            } else {
                rvLeaderBoard.isVisible = true
                tvNoUsers.isVisible = false
            }
        }
        leaderBoardRankingAdapter = LeaderBoardRankingAdapter(userList, CountryCode) { rank ->
            // binding.layoutOwnRank.tvSerialNumber.text = rank
        }
        binding.rvLeaderBoard.apply {
            layoutManager = LinearLayoutManager(this@LeaderBoardActivity)
            adapter = leaderBoardRankingAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentUserRank(countryCode: String?) {
        binding.apply {
            flCurrentUser.isVisible = true
            layoutOwnRank.root.isInvisible = true
            pbCurrentUserRank.isVisible = true
            layoutOwnRank.apply {
                tvName.setTextColor(Color.WHITE)
            }
        }
        if (!AppUtils.isLoggedIn()) {
            binding.flCurrentUser.isVisible = false
            return
        }

        val constants = StringConstants()
        val userCountryCode = constants.getString(constants.UserCountry, this)
        val userName = constants.getString(constants.Username, this)
        val userId = constants.getString(constants.UserId, this)
        val profilePic = constants.getString(constants.ProfilePicsUrl, this)

        Log.d("userCountryCode", userCountryCode)
        Log.d("userName", userName)
        Log.d("userId", userId)

        if (countryCode != null && countryCode != userCountryCode) {
            binding.flCurrentUser.isVisible = false
            return
        }
        loadImage(binding.layoutOwnRank.imgProfile, profilePic)

        binding.layoutOwnRank.root.setOnClickListener {
            if (AppUtils.isLoggedIn()) {
                FireUtils.openProfileScreen(this, null)
            }
        }

        FirebaseFirestoreApi.getUserRank(countryCode).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                binding.apply {
                    flCurrentUser.isVisible = true
                    layoutOwnRank.root.isVisible = true
                    pbCurrentUserRank.isVisible = false
                }

                try {
                    val data = it.result.data as? List<HashMap<String, Any>>
                    Log.d("result getUserRank", data.toString())
                    val currentUserRank =
                        data?.firstOrNull { map -> map.contains("user_id") && map["user_id"].toString() == userId }
                    if (currentUserRank == null) {
                        binding.flCurrentUser.isVisible = false
                        return@addOnCompleteListener
                    }
                    val rank = currentUserRank["rank"].toString()
                    val points = currentUserRank["points"].toString()
                    val level = currentUserRank["level"].toString()
                    binding.layoutOwnRank.apply {
                        tvName.text = userName ?: ""
                        tvTotalPoints.text = "$points Pts"
                        tvSerialNumber.text = rank
                        tvTotalAwards.text = level
                        getUserBadgeIcon(level)?.let { imgChallengeBadge.setImageResource(it) }
                        getUserBG(level)?.let {
                            binding.layoutOwnRank.layoutMain.setBackgroundResource(
                                it
                            )
                        }
                    }

                } catch (e: Exception) {
                    binding.apply {
                        flCurrentUser.isVisible = false
                    }
                }

            } else {
                binding.apply {
                    flCurrentUser.isVisible = false
                }
            }
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

        // Create a map from country code to users for quick lookup
        val countryUsersMap = countriesData.associateBy({ it.code }, { it.users })
        val totalUsers = countriesData.sumOf { it.users ?: 0 }
        val countries = Lists.countriesList.map { country ->
            if (country.countryCode == "WW") {
                country.copy(users = totalUsers)
            } else {
                country.copy(users = countryUsersMap[country.countryCode] ?: 0)
            }
        }.sortedByDescending { it.users }.toMutableList()

        leaderBoardCountriesAdapter =
            LeaderBoardCountriesAdapter(countries, this, topLevel, this, true)
        with(dialogBinding.rvImagesList) {
            layoutManager = LinearLayoutManager(this@LeaderBoardActivity)
            adapter = leaderBoardCountriesAdapter
        }

        sendUserEvent(StringConstants.ldbd_world_dialog_select)
        bottomSheet?.show()
    }

    private fun getCountriesData() {
        if (countriesData.isNotEmpty()) {
            openCountriesButtonSheet()
            return
        }
        progressDialog.show()
        FirebaseFirestoreApi.getCountries().addOnCompleteListener {
            if (it.isSuccessful) {
                progressDialog.dismiss()
                val data = it.result.data as? List<HashMap<String, Any>>
                Log.d("result", data.toString());
                try {
                    data?.forEach { map ->
                        countriesData.add(
                            CountryInfo(
                                map["code"].toString(),
                                map["name"].toString(),
                                map["users"].toString().toLong()
                            )
                        )
                    }
                    openCountriesButtonSheet()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("Something went wrong, Please try again!")
                    progressDialog.dismiss()
                }

            } else {
                Log.e("ex", it.exception.toString())
                showToast("Something went wrong, Please try again!")
                progressDialog.dismiss()
            }
        }
    }

    override fun countryClick(model: CountriesModel, flag: Int?, position: Int) {
        if (model.users != null && model.users > 0) {
            topLevel.onCreate().writePosition(this, position)
            fetchLeaderBoardData(model.countryCode, model.countryName, null)
            setCountryIconToolbar(model)
        }

        bottomSheet?.dismiss()
    }

    private fun setCountryIconToolbar(model: CountriesModel) {
        binding.customToolbar.imgWorld.setImageDrawable(
            model.flag?.let {
                ContextCompat.getDrawable(
                    this@LeaderBoardActivity,
                    it
                )
            })

        initToolbar(" - " + model.countryName)
    }

}