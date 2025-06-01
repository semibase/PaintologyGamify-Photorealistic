package com.paintology.lite.photorealistic.drawing.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivityOnboardingExample1Binding
import com.paintology.lite.photorealistic.drawing.search.HorizontalMarginItemDecoration
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast

class OnboardingExample1Activity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityOnboardingExample1Binding
    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: Button
    private var constants = StringConstants()
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var fromMain: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityOnboardingExample1Binding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.setFinishOnTouchOutside(false)

        title = getString(R.string.help_guide)

        showToast(constants.help_open)

//        initToolbar()

        FirebaseUtils.logEvents(this@OnboardingExample1Activity, constants.help_open)

        sharedPreferences = applicationContext.getSharedPreferences("PaintologyDB", MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        if (intent.hasExtra("dashboard")) {
            fromMain = true
        }
        mViewPager = activityMainBinding.viewPager
        mViewPager.offscreenPageLimit = 5
        var list = AppUtils.getSliders(this)
        if (fromMain) {
            if (list.size >= 2) {
                list.removeAt(0)
                list.removeAt(0)
            }
        }
        mViewPager.adapter = OnboardingViewPagerAdapter(this, this, fromMain, list)
        setupViewPager(mViewPager)
        TabLayoutMediator(activityMainBinding.pageIndicator, mViewPager) { _, _ -> }.attach()
        textSkip = findViewById(R.id.btn_skip_step)
        textSkip.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@OnboardingExample1Activity,
                    constants.help_skip,
                    Toast.LENGTH_SHORT
                ).show()
            }
            FirebaseUtils.logEvents(this@OnboardingExample1Activity, constants.help_skip)
            editor?.putString("policyStatus", "1")?.apply()
            //    openActivity(GalleryDashboard::class.java)
            finish()
        }

        val btnNextStep: Button = findViewById(R.id.btn_next_step)

        btnNextStep.setOnClickListener {
            val count = (mViewPager.adapter as OnboardingViewPagerAdapter).getCount()
            val currentItem = getItem()
            if (currentItem == count - 1) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@OnboardingExample1Activity,
                        constants.help_close,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@OnboardingExample1Activity, constants.help_close)
                editor?.putString("policyStatus", "1")?.apply()
                //    openActivity(GalleryDashboard::class.java)
                finish()
            } else {
                mViewPager.setCurrentItem(currentItem + 1, true)
            }
        }

        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
//                println(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                println(position)
            }


            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@OnboardingExample1Activity,
                        "help_slide" + (position.plus(1)),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(
                    this@OnboardingExample1Activity,
                    "help_slide" + (position.plus(1))
                )
                if (getItem() == 0) {
                    btnNextStep.text = getString(R.string.continue_lbl)
                } else {
                    btnNextStep.text = getString(R.string.next)
                }

                if (getItem() > 0) {
                    textSkip.visibility = View.VISIBLE
                } else {
                    textSkip.visibility = View.GONE
                }

                val count = (mViewPager.adapter as OnboardingViewPagerAdapter).getCount()
                val currentItem = getItem()
                if (currentItem == count - 1) {
                    constants.putInt(
                        constants.onboarding_completed,
                        1,
                        this@OnboardingExample1Activity
                    )
                }
            }
        })

    }

    /*    private fun initToolbar() {
            activityMainBinding.customToolbar.apply {
                imgFav.hide()
                    imgMenu.onSingleClick {
                        openActivity(GalleryDashboard::class.java)
                        finish()
                    }
                tvTitle.text = getString(R.string.help_guide)
            }
        }*/

    private fun setupViewPager(viewPager: ViewPager2) {
        // You need to retain one page on each side so that the next and previous items are visible
        viewPager.offscreenPageLimit = 1

        // Add a PageTransformer that translates the next and previous items horizontally
        // towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
//                    page.scaleY = 1 - (0.25f * Math.abs(position))
            // If you want a fading effect uncomment the next line:
//                     page.alpha = 0.25f + (1 - Math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)

        // The ItemDecoration gives the current (centered) item horizontal margin so that
        // it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            this@OnboardingExample1Activity,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        viewPager.addItemDecoration(itemDecoration)
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        editor?.putString("policyStatus", "1")?.apply()
        super.onDestroy()
    }
}
