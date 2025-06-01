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
import com.google.firebase.firestore.FirebaseFirestore
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.checkForIntroVideo
import com.paintology.lite.photorealistic.drawing.Activity.commonMenuClick
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.LeaderBoardActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Community.Community
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.ads.callbacks.RewardedOnLoadCallBack
import com.paintology.lite.photorealistic.drawing.ads.callbacks.RewardedOnShowCallBack
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityUserPointBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutNewPointBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants

class UserPointActivity : BaseActivity() {
    private var dialog: Dialog? = null
    private var pointDrawings = 0;
    private var pointCommunity = 0;
    private var pointGallery = 0;
    private var pointTutorial = 0;
    private var pointOther = 0;

    private lateinit var db_firebase: FirebaseFirestore

    private var constant: StringConstants = StringConstants()

    private var totPoints = 0;
    private var level = ""
    private val binding by lazy {
        ActivityUserPointBinding.inflate(layoutInflater)
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val adsRunner = Runnable { checkAdvertisement() }
    private var isInterstitialLoadOrFailed = false
    private var mCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        level =
            sharedPref.getString(StringConstants.user_level, StringConstants.beginner).toString()
        db_firebase = FirebaseFirestore.getInstance()
        checkForIntroVideo(StringConstants.intro_points)


        binding.tvLevel.text = level
        initToolbar()
        checkDrawingPoints()
        setData()
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
                    if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/5354046379" else diComponent.sharedPreferenceUtils.interUserPointActivityRewardID,
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

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val titleTextView = TextView(this)
        titleTextView.text = getString(R.string.your_points)
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

        binding.tvLevel.onSingleClick {
            startActivity(
                Intent(this, LeaderBoardActivity::class.java).putExtra(
                    "tvRankLevel",
                    level
                )
            )
        }
    }

    fun setData() {
        FireUtils.setPoints(this, binding.textView3, null)
    }

    private fun checkDrawingPoints() {
        try {
            db_firebase.collection("users").document(constant.getString(constant.UserId, this))
                .collection("gamifications").document("activity").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document != null) {
                            if (document.exists()) {
                                fetchDrawingPoints()
                                return@addOnCompleteListener
                            }
                        }
                    }
                    checkRewardPoints()
                }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }


    }

    private fun fetchDrawingPoints() {

        db_firebase.collection("users").document(constant.getString(constant.UserId, this))
            .collection("gamifications").document("activity")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    checkRewardPoints()
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val result = snapshot.get("points_by_types") as HashMap<*, *>
                    if (result.isNotEmpty()) {
                        totPoints = 0;
                        if (result.containsKey("Community")) {
                            pointCommunity = result.get("Community").toString().toInt()
                            totPoints += pointCommunity;
                        }

                        if (result.containsKey("Drawing")) {
                            pointDrawings = result.get("Drawing").toString().toInt()
                            totPoints += pointDrawings;
                        }

                        if (result.containsKey("Gallery")) {
                            pointGallery = result.get("Gallery").toString().toInt()
                            totPoints += pointGallery;
                        }

                        if (result.containsKey("Tutorial")) {
                            pointTutorial = result.get("Tutorial").toString().toInt()
                            totPoints += pointTutorial;
                        }

                        if (result.containsKey("Other")) {
                            pointOther += result.get("Other").toString().toInt()
                            totPoints += pointOther;
                        }

                        if (result.containsKey("Common")) {
                            pointOther += result.get("Common").toString().toInt()
                            totPoints += pointOther;
                        }
                    }
                }

                checkRewardPoints()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun checkRewardPoints() {

        binding.tvTotal.text = totPoints.toString()

        binding.tvDrawingPoints.text = "${getString(R.string.drawings)} ($pointDrawings Pts)"
        binding.llDrawingPoints.visibility = View.VISIBLE
        val viewBinding = LayoutNewPointBinding.inflate(layoutInflater)
        viewBinding.txtRewardName.text = getString(R.string.c_with_dr)
        viewBinding.layoutMain.onSingleClick {
            startActivity(
                Intent(
                    this@UserPointActivity,
                    ProgressActivity::class.java
                )
            )
        }
        if (binding.llDrawingPoints.childCount > 1) {
            binding.llDrawingPoints.removeViewAt(1)
        }
        binding.llDrawingPoints.addView(viewBinding.root)


        binding.tvCommunityPoints.text = "${getString(R.string.community1)} ($pointCommunity Pts)"
        binding.llCommunityPoints.visibility = View.VISIBLE
        val viewBinding2 = LayoutNewPointBinding.inflate(layoutInflater)
        viewBinding2.txtRewardName.text = getString(R.string.p_post)
        viewBinding2.layoutMain.onSingleClick {
            startActivity(
                Intent(
                    this@UserPointActivity,
                    Community::class.java
                )
            )
        }
        if (binding.llCommunityPoints.childCount > 1) {
            binding.llCommunityPoints.removeViewAt(1)
        }
        binding.llCommunityPoints.addView(viewBinding2.root)

        binding.tvGalleryPoints.text = "${getString(R.string.gallery)} ($pointGallery Pts)"
        binding.llGalleryPoints.visibility = View.VISIBLE
        val viewBinding3 = LayoutNewPointBinding.inflate(layoutInflater)
        viewBinding3.txtRewardName.text = getString(R.string.s_dr)
        viewBinding3.layoutMain.onSingleClick {
            startActivity(
                Intent(
                    this@UserPointActivity,
                    GalleryActivity::class.java
                )
            )
        }
        if (binding.llGalleryPoints.childCount > 1) {
            binding.llGalleryPoints.removeViewAt(1)
        }
        binding.llGalleryPoints.addView(viewBinding3.root)

        binding.tvTutorialPoints.text = "${getString(R.string.tutorials)} ($pointTutorial Pts)"
        binding.llTutorialPoints.visibility = View.VISIBLE
        val viewBinding4 = LayoutNewPointBinding.inflate(layoutInflater)
        viewBinding4.txtRewardName.text = getString(R.string.tutorials_point)
        viewBinding4.layoutMain.onSingleClick {
            startActivity(
                Intent(
                    this@UserPointActivity,
                    CategoryActivity::class.java
                ).putExtra("cate_id",StringConstants.CATE_ID)
            )
        }
        if (binding.llTutorialPoints.childCount > 1) {
            binding.llTutorialPoints.removeViewAt(1)
        }
        binding.llTutorialPoints.addView(viewBinding4.root)

        binding.tvOtherPoints.text = "${getString(R.string.other)} ($pointOther Pts)"
        binding.llOtherPoints.visibility = View.VISIBLE
        val viewBinding5 = LayoutNewPointBinding.inflate(layoutInflater)
        viewBinding5.txtRewardName.text = getString(R.string.e_more)
        viewBinding5.layoutMain.onSingleClick {
            finish()
        }
        if (binding.llOtherPoints.childCount > 1) {
            binding.llOtherPoints.removeViewAt(1)
        }
        binding.llOtherPoints.addView(viewBinding5.root)
        binding.llMain.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.new_common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return commonMenuClick(item, StringConstants.intro_points)
    }


}