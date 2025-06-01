package com.paintology.lite.photorealistic.drawing.minipaint

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.Activity.checkForIntroVideo
import com.paintology.lite.photorealistic.drawing.Activity.commonMenuClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.DashboardScreen.ImportImagesActivity
import com.paintology.lite.photorealistic.drawing.Model.BannerModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityMyResourcesBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBannerResourcesBinding
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard
import com.paintology.lite.photorealistic.drawing.gallery.MyMoviesActivity
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.squareup.picasso.Picasso

class MyResources : AppCompatActivity(), View.OnClickListener {

    var constants = StringConstants()
    var target_name = ""

    private lateinit var mBinding: ActivityMyResourcesBinding
    private lateinit var data: List<BannerModel>

    //    private com.facebook.ads.AdView fAdView;
    private var showExitDialog = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMyResourcesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        checkForIntroVideo(StringConstants.intro_resources)


        setSupportActionBar(mBinding.toolbar)

        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        } catch (e: Exception) {

        }

        setTitle(R.string.my_resources)
        fetchData()
        val intent = intent
        showExitDialog = intent.getBooleanExtra("showExitDialog", false)
        if (getIntent() != null && getIntent().hasExtra("target_name")) {
            target_name = getIntent().getStringExtra("target_name") ?: ""
            when (target_name) {
                "import" -> onClick(mBinding.llImportImage)
                "my movies" -> onClick(mBinding.llMyMovies)
            }
        }

    }

    private fun fetchData() {
        data = AppUtils.getBanners(this)
        for (i in data.indices) {
            val bannerBinding = LayoutBannerResourcesBinding.inflate(layoutInflater)
            Picasso.get().load(Uri.parse(data[i].bannerImageUrl)).into(bannerBinding.ivOwnAdv)
            bannerBinding.cvAdv.onSingleClick {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@MyResources,
                            constants.ad_XX_resources_banner_click.replace("XX", i.toString()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(
                        this@MyResources,
                        constants.ad_XX_resources_banner_click.replace("XX", i.toString())
                    )
                    KGlobal.openInBrowser(this@MyResources, data[i].bannerLInk)
                } catch (e: ActivityNotFoundException) {
                    Log.e("TAGGG", "Exception at view " + e.message)
                } catch (e: Exception) {
                    Log.e("TAGG", "Exception " + e.message)
                }
            }
            mBinding.llAds.addView(bannerBinding.root)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_paintology_website -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@MyResources,
                            "sites_screen_paintology_website",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this@MyResources, "sites_screen_paintology_website")

                    websiteRewardPoint()

                    KGlobal.openInBrowser(
                        this@MyResources,
                        constants.getString(StringConstants.paintology_website, this)
                    )
                } catch (e: Exception) {
                    Log.e(GalleryDashboard::class.java.name, e.message!!)
                }
            }

            R.id.ll_youtube_paintology -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@MyResources,
                            "sites_screen_youtube_paintology",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this@MyResources, "sites_screen_youtube_paintology")

                    youtubeRewardPoint()

                    KGlobal.openInBrowser(
                        this@MyResources,
                        constants.getString(StringConstants.youtube_paintology, this)
                    )
                } catch (e: java.lang.Exception) {
                    Log.e(GalleryDashboard::class.java.name, e.message!!)
                }
            }

            R.id.ll_learn_drawing_painting -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@MyResources,
                            "sites_screen_learn_drawing",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this@MyResources, "sites_screen_learn_drawing")

                    learnDrawingRewardPoint()

                    KGlobal.openInBrowser(
                        this@MyResources,
                        constants.getString(StringConstants.learn_drawing_painting, this)
                    )
                } catch (e: java.lang.Exception) {
                    Log.e(GalleryDashboard::class.java.name, e.message!!)
                }
            }

            R.id.ll_apps_by_paintology -> {
                try {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@MyResources,
                            "sites_screen_apps_by_paintology",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this@MyResources, "sites_screen_apps_by_paintology")
//                    KGlobal.openInBrowser(GalleryDashboard.this, "https://play.google.com/store/apps/developer?id=Paintology&hl=en");

                    appsRewardPoint()

                    KGlobal.openInBrowser(
                        this@MyResources,
                        constants.getString(StringConstants.apps_by_paintology, this)
                    )
                } catch (e: java.lang.Exception) {
                    Log.e(GalleryDashboard::class.java.name, e.message!!)
                }
            }

            R.id.ll_daily_blog -> try {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@MyResources,
                        constants.MYRES_SCREEN_DAILY_BLOG,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@MyResources, constants.MYRES_SCREEN_DAILY_BLOG)
                if (KGlobal.isInternetAvailable(this@MyResources)) {
                    blogRewardPoint()
                    KGlobal.openInBrowser(
                        this@MyResources,
                        constants.getString(StringConstants.daily_blog, this)
                    )
                } else Toast.makeText(
                    this,
                    resources.getString(R.string.no_internet_msg),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: ActivityNotFoundException) {
                Log.e("TAGGG", "Exception at view " + e.message)
            } catch (e: Exception) {
                Log.e("TAGG", "Exception " + e.message)
            }

            R.id.ll_import_image -> {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@MyResources,
                        constants.MYRES_SCREEN_IMPORT_IMAGES,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@MyResources, constants.MYRES_SCREEN_IMPORT_IMAGES)
                startActivity(Intent(this, ImportImagesActivity::class.java))
            }

            R.id.ll_my_movies -> {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@MyResources,
                        constants.MYRES_SCREEN_MY_MOVIES,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@MyResources, constants.MYRES_SCREEN_MY_MOVIES)
                startActivity(Intent(this@MyResources, MyMoviesActivity::class.java))
            }

            R.id.ll_video_tuto -> {
                try {
                    if (KGlobal.isInternetAvailable(this@MyResources)) {
                        onlineTutorialRewardPoint()
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@MyResources,
                                constants.MYRES_SCREEN_ONLINE_TUTORIALS,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@MyResources,
                            constants.MYRES_SCREEN_ONLINE_TUTORIALS
                        )
                        KGlobal.openInBrowser(
                            this@MyResources,
                            constants.getString(StringConstants.online_tutorials, this)
                        )
                    } else Toast.makeText(
                        this,
                        resources.getString(R.string.no_internet_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: ActivityNotFoundException) {
                    Log.e("TAGGG", "Exception at view " + e.message)
                } catch (e: Exception) {
                    Log.e("TAGG", "Exception " + e.message)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (showExitDialog) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.exit_app_msg))
                .setPositiveButton(
                    getString(R.string.quit)
                ) { dialogInterface: DialogInterface?, i: Int ->
                    MyApplication.setAppUsedCountSeen(false)
                    finishAffinity()
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface: DialogInterface?, i: Int ->
                    startActivity(Intent(this@MyResources, GalleryDashboard::class.java))
                    finish()
                }
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun onlineTutorialRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.online_tutorial, null)
        /* if (FirebaseAuth.getInstance().currentUser != null) {
             val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
             if (rewardSetup != null) {
                 updateIncreasableRewardValue(
                     "online_tutorial",
                     rewardSetup.online_tutorial ?: 0,
                     FirebaseAuth.getInstance().currentUser!!.uid
                 )
             }
         }*/
    }

    private fun blogRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.blog_website, null)
        /*   if (FirebaseAuth.getInstance().currentUser != null) {
               val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
               if (rewardSetup != null) {
                   updateIncreasableRewardValue(
                       "blog_website",
                       rewardSetup.blog_website ?: 0,
                       FirebaseAuth.getInstance().currentUser!!.uid
                   )
               }
           }*/
    }

    private fun myMoviesPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.my_movies, null)
        /* if (FirebaseAuth.getInstance().currentUser != null) {
             val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
             if (rewardSetup != null) {
                 updateIncreasableRewardValue(
                     "my_movies",
                     rewardSetup.my_movies ?: 0,
                     FirebaseAuth.getInstance().currentUser!!.uid
                 )
             }
         }*/
    }

    private fun websiteRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.paintology_website, null)
        /*    if (FirebaseAuth.getInstance().currentUser != null) {
                val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
                if (rewardSetup != null) {
                    updateIncreasableRewardValue(
                        "paintology_website",
                        rewardSetup.paintology_website ?: 0,
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                }
            }*/
    }

    private fun youtubeRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.paintology_youtube, null)
        /*  if (FirebaseAuth.getInstance().currentUser != null) {
              val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
              if (rewardSetup != null) {
                  updateIncreasableRewardValue(
                      "paintology_youtube",
                      rewardSetup.paintology_youtube ?: 0,
                      FirebaseAuth.getInstance().currentUser!!.uid
                  )
              }
          }*/
    }

    private fun learnDrawingRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.learn_drawing, null)
        /*if (FirebaseAuth.getInstance().currentUser != null) {
            val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
            if (rewardSetup != null) {
                updateIncreasableRewardValue(
                    "learn_drawing",
                    rewardSetup.learn_drawing ?: 0,
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            }
        }*/
    }

    private fun appsRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.apps_by_paintology, null)
        /* if (FirebaseAuth.getInstance().currentUser != null) {
             val rewardSetup = AppUtils.getRewardSetup(this@MyResources)
             if (rewardSetup != null) {
                 updateIncreasableRewardValue(
                     "apps_by_paintology",
                     rewardSetup.apps_by_paintology ?: 0,
                     FirebaseAuth.getInstance().currentUser!!.uid
                 )
             }
         }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.new_common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return commonMenuClick(item, StringConstants.intro_resources)
    }


}
