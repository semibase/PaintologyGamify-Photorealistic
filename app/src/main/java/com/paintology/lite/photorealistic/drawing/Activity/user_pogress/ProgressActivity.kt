package com.paintology.lite.photorealistic.drawing.Activity.user_pogress

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.checkForIntroVideo
import com.paintology.lite.photorealistic.drawing.Activity.commonMenuClick
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingViewActivity
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.adapter.DrawingAdapter
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.adapter.TutorialAdapter
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.adapter.UserTutorialAdapter
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.DrawUtils
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.DrawingUtils
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialcategory
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialdatum
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialimages
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.ads.callbacks.RewardedOnLoadCallBack
import com.paintology.lite.photorealistic.drawing.ads.callbacks.RewardedOnShowCallBack
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityProgressBinding
import com.paintology.lite.photorealistic.drawing.painting.file.ImageManager
import com.paintology.lite.photorealistic.drawing.room.AppDatabase
import com.paintology.lite.photorealistic.drawing.room.daos.SavedDrawingDao
import com.paintology.lite.photorealistic.drawing.room.entities.SavedDrawingEntity
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants

class ProgressActivity : BaseActivity() {

    private var db: AppDatabase? = null
    private var drawingDao: SavedDrawingDao? = null

    private var level = ""

    private var tUtils: TutorialUtils? = null
    private var constant: StringConstants = StringConstants()

    var isClicked = false
    private var tutorialsList = mutableListOf<Any?>()
    private var drawingsList = mutableListOf<Any?>()
    private var userTutorialsList = mutableListOf<Any?>()

    var userData = hashMapOf<String, String>()
    var userProgress = hashMapOf<String, Int>()
    var completedUserData = hashMapOf<String, String>()

    val acts = listOf(
        "post_drawing_to_gallery",
        "save_drawing",
        "draw_strokes",
        "scribble_on_your_canvas",
        "open_tutorial"
    )

    val dataProgress = hashMapOf(
        "open_tutorial" to 1,
        "scribble_on_your_canvas" to 2,
        "draw_strokes" to 3,
        "save_drawing" to 4,
        "post_drawing_to_gallery" to 5
    )

    var pageNo = 1;
    private val binding by lazy {
        ActivityProgressBinding.inflate(layoutInflater)
    }
    private var userAdapter: UserTutorialAdapter? = null
    private var drawingAdapter: DrawingAdapter? = null
    private var tutorialAdapter: TutorialAdapter? = null

    private val mHandler = Handler(Looper.getMainLooper())
    private val adsRunner = Runnable { checkAdvertisement() }
    private var isInterstitialLoadOrFailed = false
    private var mCounter: Int = 0

    private var dialog: Dialog? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        level =
            sharedPref.getString(StringConstants.user_level, StringConstants.beginner).toString()

        db = MyApplication.getDb()
        tUtils = TutorialUtils(this)
        drawingDao = db?.savedDrawingDao()
        binding.tvLevel.setText(level)

        checkForIntroVideo(StringConstants.intro_progress)


        initToolbar()
        initListeners()
        setData()
        startWork()
        binding.tvWatchAd.onSingleClick {
            showLoadingDialog()
            loadInterRewardAd()
            mHandler.post(adsRunner)
        }
    }

    private fun loadInterRewardAd() {
        when (diComponent.sharedPreferenceUtils.rcvInterRewardNotification) {
            0 -> {
                isInterstitialLoadOrFailed = true
            }

            1 -> {
                diComponent.admobRewardedAds.loadRewardedAd(this,
                    if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/5354046379" else diComponent.sharedPreferenceUtils.interProgressActivityRewardID,
                    diComponent.sharedPreferenceUtils.rcvInterRewardNotification,
                    diComponent.sharedPreferenceUtils.isAppPurchased,
                    diComponent.internetManager.isInternetConnected,
                    object : RewardedOnLoadCallBack {
                        override fun onAdFailedToLoad(adError: String) {
                            isInterstitialLoadOrFailed = true
                        }

                        override fun onAdLoaded() {
                            checkAdLoad()
                        }

                        override fun onPreloaded() {
                            isInterstitialLoadOrFailed = true
                        }
                    })
            }

            else -> {
                isInterstitialLoadOrFailed = true
            }
        }
    }

    private fun showLoadingDialog() {
        dialog = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_loading_ads, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    private fun checkAdvertisement() {
        if (mCounter < 16) {
            try {
                mCounter++
                if (isInterstitialLoadOrFailed) {
                    mHandler.removeCallbacks { adsRunner }
                    checkAdLoad()
                } else {
                    mHandler.removeCallbacks { adsRunner }
                    mHandler.postDelayed(
                        adsRunner, (1000)
                    )
                }
            } catch (e: Exception) {
                Log.e("AdsInformation", "${e.message}")
            }
        } else {
            Log.e("AdsInformation", "checkAdvertisement: ELSE")
            isInterstitialLoadOrFailed = true
            mHandler.removeCallbacks { adsRunner }
        }
    }

    private fun checkAdLoad() {
        if (diComponent.admobRewardedAds.isRewardedLoaded()) {
            diComponent.admobRewardedAds.showRewardedAd(
                this,
                object : RewardedOnShowCallBack {
                    override fun onAdClicked() {}
                    override fun onAdDismissedFullScreenContent() {
                        setData()
                    }

                    override fun onAdFailedToShowFullScreenContent() {
                        dialog?.dismiss()
                        showToast(getString(R.string.no_ad))
                    }

                    override fun onAdImpression() {}
                    override fun onAdShowedFullScreenContent() {
                        dialog?.dismiss()
                    }

                    override fun onUserEarnedReward() {
                        Log.w("POINTSS", "onUserEarnedReward: 1")
                        FirebaseFirestoreApi.claimActivityPointsWithId("reward_ads", null)
                    }
                })
        } else {
            dialog?.dismiss()
            showToast(getString(R.string.no_ad))
        }
    }

    override fun onResume() {
        super.onResume()
        if (isClicked) {
            startWork()
        }
    }

    private fun startWork() {
        isClicked = false
        tutorialsList.clear()
        drawingsList.clear()
        userTutorialsList.clear()
        binding.llTutorials.visibility = View.GONE
        binding.llUserDrawings.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        initHorizontalRecyclerView()
    }


    private fun initListeners() {
        binding.tvLevel.onSingleClick {
            startActivity(
                Intent(this, LeaderBoardActivity::class.java).putExtra(
                    "tvRankLevel",
                    level
                )
            )
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val titleTextView = TextView(this)
        titleTextView.text = getString(R.string.your_progress)
        titleTextView.textSize = 20f
        titleTextView.setTypeface(null, Typeface.BOLD)
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        titleTextView.gravity = Gravity.CENTER
        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        binding.toolbar.addView(titleTextView, layoutParams)
    }

    private fun setData() {
        FireUtils.setPoints(this, binding.textView3, null)
    }

    private fun initVerticalRecyclerView() {
        FirebaseFirestoreApi.fetchTutorialsList(
            pageNo,
            "level:=$level",
            "created_at:desc"
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.getResult().getData() as HashMap<*, *>
                val mlist = data.get("data") as List<*>
                for (tlist in mlist) {
                    val item = tlist as HashMap<*, *>
                    if (!userData.contains(
                            item.get("id").toString()
                        ) && !completedUserData.containsKey(
                            item.get("id").toString()
                        ) && tutorialsList.size < 5
                    ) {
                        tutorialsList.add(item)
                    }
                }
                if ((mlist.isEmpty() && tutorialsList.size > 0) || (mlist.isNotEmpty() && tutorialsList.size == 5)) {
                    tutorialsList.add(null)
                    tutorialAdapter =
                        TutorialAdapter(this, level, tutorialsList) { position, item ->
                            isClicked = true
                            FireUtils.showProgressDialog(
                                this@ProgressActivity,
                                getString(R.string.please_wait)
                            )
                            tUtils?.parseTutorial(parseToDatum(item).id)
                        }
                    binding.rvVertical.apply {
                        layoutManager = LinearLayoutManager(this@ProgressActivity)
                        adapter = tutorialAdapter
                    }
                    binding.progress.visibility = View.GONE
                    binding.llTutorials.visibility = View.VISIBLE
                } else {
                    if (mlist.isNotEmpty()) {
                        pageNo++;
                        initVerticalRecyclerView()
                    } else {
                        hideVertical()
                    }
                }
            } else {
                hideVertical()
            }
        }
    }

    fun parseToDatum(data: Any): Tutorialdatum {

        val item = data as HashMap<*, *>
        val mTutorialdatum = Tutorialdatum()

        mTutorialdatum.title = item.get("title").toString()
        mTutorialdatum.content = item.get("content").toString()
        mTutorialdatum.createdAt = item.get("created_at").toString()
        mTutorialdatum.id = item.get("id").toString()
        mTutorialdatum.level = item.get("level").toString()
        mTutorialdatum.status = item.get("status").toString()
        mTutorialdatum.type = item.get("type").toString()
        mTutorialdatum.visibility = item.get("visibility").toString()

        val mlistCategory = item.get("categories") as ArrayList<HashMap<*, *>>
        val mTutorialcategories: ArrayList<Tutorialcategory> = ArrayList()

        for (cat in mlistCategory) {
            val mTutorialcategory = Tutorialcategory()
            mTutorialcategory.setThumbnail(cat.get("thumbnail").toString())
            mTutorialcategory.setId(cat.get("id").toString())
            mTutorialcategory.setName(cat.get("name").toString())
            mTutorialcategories.add(mTutorialcategory)
        }

        mTutorialdatum.tutorialcategories = mTutorialcategories

        val images = item.get("images") as HashMap<*, *>
        val mTutorialimages: Tutorialimages = Tutorialimages()
        if (images.containsKey("thumbnail")) {
            mTutorialimages.setThumbnail(images["thumbnail"].toString())
            Log.e("imagelinks", "added")
        }

        if (images.containsKey("thumbnail_resized") && images["thumbnail_resized"].toString() != null) {
            mTutorialimages.setThumbnailResized(images["thumbnail_resized"].toString())
        }

        mTutorialdatum.tutorialimages = mTutorialimages

        val mlistTags = item.get("tags") as ArrayList<*>
        val mStrings = ArrayList<String>()
        for (text in mlistTags.indices) {
            mStrings.add(mlistTags[text].toString())
        }
        mTutorialdatum.tags = mStrings

        return mTutorialdatum
    }

    private fun initHorizontalRecyclerView() {
        FirebaseFirestoreApi.getActivityProgress().addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result.data as HashMap<*, *>;
                for (text in acts) {
                    if (text.equals("post_drawing_to_gallery")) {
                        for (item in data.get(text) as ArrayList<*>) {
                            completedUserData.put(item.toString(), text)
                        }
                    } else if (data.containsKey(text)) {
                        for (item in data.get(text) as ArrayList<*>) {
                            if (!userData.containsKey(item.toString()) && !completedUserData.containsKey(
                                    item.toString()
                                )
                            ) {
                                userData.put(item.toString(), text)
                            }
                        }
                    }
                }
                if (userData.size > 0) {
                    checkData();
                } else {
                    getCompletedDrawings()
                }
            } else {
                getCompletedDrawings()
            }
        }
    }

    fun checkData() {
        var array = IntArray(userData.size)
        var index = 0;
        userData.forEach {
            array.set(index, it.key.toInt());
            index++;
        }
        Thread({
            val dataArray = drawingDao?.getAllByIds(
                array
            ) as List<SavedDrawingEntity>
            runOnUiThread {
                if (dataArray.size > 0) {
                    dataArray.forEach {
                        if (!dataProgress.containsKey(it.postId.toString()) && userData.containsKey(
                                it.postId.toString()
                            )
                        ) {
                            if (userTutorialsList.size < 4) {
                                userTutorialsList.add(it)
                            }
                            dataProgress.get(userData.get(it.postId.toString()))?.let { it1 ->
                                userProgress.put(
                                    it.postId.toString(),
                                    it1
                                )
                            }
                        }
                    }
                    userTutorialsList.add(null)
                    userAdapter = UserTutorialAdapter(
                        this,
                        userTutorialsList,
                        userProgress
                    ) { position, item ->
                        processDrawing(item)
                    }
                    binding.rvUserDrawings.apply {
                        layoutManager =
                            LinearLayoutManager(
                                this@ProgressActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        adapter = userAdapter
                    }
                    binding.txtUserDrawings.text = getString(R.string.complete_your_drawings)
                    binding.progress.visibility = View.GONE
                    binding.llUserDrawings.visibility = View.VISIBLE
                    initVerticalRecyclerView()
                } else {
                    getCompletedDrawings()
                }
            }

        }).start()
    }

    fun getCompletedDrawings() {

        // binding.txtUserDrawings.text = getString(R.string.your_drawings)
        val filterBy = "author.user_id:=" + constant.getString(constant.UserId, this)
        val filters = filterBy?.let {
            hashMapOf("filter_by" to it)
        }

        val sortBy = "created_at:desc"
        val sorts = sortBy?.let {
            hashMapOf("sort_by" to it)
        }

        FirebaseFirestoreApi.fetchDrawingList(1, 5, filters, sorts)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data as HashMap<*, *>;
                    val mlist = data.get("data") as List<*>
                    for (tlist in mlist) {
                        val item = tlist as HashMap<String, Any>
                        val itemData = item.get("metadata") as HashMap<*, *>
                        if (itemData.containsKey("tutorial_id") && itemData.get("tutorial_id") != null && !userData.containsKey(
                                itemData.get("tutorial_id")
                            )
                        ) {
                            if (drawingsList.size < 4) {
                                drawingsList.add(item)
                            } else break
                        }
                    }
                    if (drawingsList.size > 0) {
                        drawingsList.add(null)
                        drawingAdapter = DrawingAdapter(
                            this,
                            drawingsList
                        ) { position, item ->
                            val drawing = item as HashMap<*, *>
                            startDrawingActivity(
                                DrawingUtils(this).parseDrawing(drawing),
                                DrawingViewActivity::class.java,
                                false
                            )
                        }
                        binding.rvUserDrawings.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    this@ProgressActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                            adapter = drawingAdapter
                        }
                        binding.progress.visibility = View.GONE
                        binding.llUserDrawings.visibility = View.VISIBLE
                        binding.txtUserDrawings.text = getString(R.string.your_drawings)
                        initVerticalRecyclerView()
                    } else {
                        hideHorizontal()
                    }
                } else {
                    hideHorizontal()
                }
            }
    }


    fun hideHorizontal() {
        binding.llUserDrawings.visibility = View.GONE
        initVerticalRecyclerView()
    }

    fun hideVertical() {
        binding.llTutorials.visibility = View.GONE
    }


    fun processDrawing(data: Any) {
        val item = data as SavedDrawingEntity
        val parentFolderPath = KGlobal.getMyPaintingFolderPath(this)
        val lImageManager = ImageManager(this, parentFolderPath)
        val paintItem = lImageManager.getPaintByPath(this, item.localPath)
        if (paintItem != null) {
            isClicked = true
            DrawUtils().editDrawing(this, paintItem, true, item.postId.toString())
        } else {
            Log.e("TAG", "error in opening drawing");
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.new_common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return commonMenuClick(item, StringConstants.intro_progress)
    }


}