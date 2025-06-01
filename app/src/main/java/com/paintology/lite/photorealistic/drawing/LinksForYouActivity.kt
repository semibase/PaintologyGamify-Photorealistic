package com.paintology.lite.photorealistic.drawing

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityLinksForYouBinding
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants

class LinksForYouActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLinksForYouBinding
    private var showExitDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLinksForYouBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.sites)

        val intent = intent
        showExitDialog = intent.getBooleanExtra("showExitDialog", false)

        mBinding.llPaintologyWebsite.setOnClickListener {
            try {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@LinksForYouActivity,
                        "sites_screen_paintology_website",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@LinksForYouActivity, "sites_screen_paintology_website")

                websiteRewardPoint()

                KGlobal.openInBrowser(this@LinksForYouActivity, "https://paintology.com/tutorials/")
            } catch (e: Exception) {
                Log.e(GalleryDashboard::class.java.name, e.message!!)
            }
        }

        mBinding.llYoutubePaintology.setOnClickListener {
            try {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@LinksForYouActivity,
                        "sites_screen_youtube_paintology",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@LinksForYouActivity, "sites_screen_youtube_paintology")

                youtubeRewardPoint()

                KGlobal.openInBrowser(
                    this@LinksForYouActivity,
                    "https://www.youtube.com/c/paintology"
                )
            } catch (e: java.lang.Exception) {
                Log.e(GalleryDashboard::class.java.name, e.message!!)
            }
        }

        mBinding.llLearnDrawingPainting.setOnClickListener {
            try {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@LinksForYouActivity,
                        "sites_screen_learn_drawing",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@LinksForYouActivity, "sites_screen_learn_drawing")

                learnDrawingRewardPoint()

                KGlobal.openInBrowser(
                    this@LinksForYouActivity,
                    "https://paintology.quora.com/"
                )
            } catch (e: java.lang.Exception) {
                Log.e(GalleryDashboard::class.java.name, e.message!!)
            }
        }

        mBinding.llAppsByPaintology.setOnClickListener {
            try {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@LinksForYouActivity,
                        "sites_screen_apps_by_paintology",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(this@LinksForYouActivity, "sites_screen_apps_by_paintology")
//                    KGlobal.openInBrowser(GalleryDashboard.this, "https://play.google.com/store/apps/developer?id=Paintology&hl=en");

                appsRewardPoint()

                KGlobal.openInBrowser(
                    this@LinksForYouActivity,
                    "https://play.google.com/store/apps/developer?id=Paintology"
                )
            } catch (e: java.lang.Exception) {
                Log.e(GalleryDashboard::class.java.name, e.message!!)
            }
        }

        mBinding.sitesBanner1.setOnClickListener {
            try {
                quoraRewardPoint()

                val url_1 = "https://paintology.quora.com"
                KGlobal.openInBrowser(this@LinksForYouActivity, url_1)
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@LinksForYouActivity,
                        StringConstants.sites_banner_quora,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(
                    this@LinksForYouActivity,
                    StringConstants.sites_banner_quora
                )
            } catch (e: ActivityNotFoundException) {
                Log.e("TAGGG", "Exception at view " + e.message)
            } catch (e: java.lang.Exception) {
                Log.e("TAGG", "Exception " + e.message)
            }
        }

        mBinding.sitesBanner2.setOnClickListener {
            try {

                googleClassRewardPoint()

//                val url_1 = "https://forms.gle/ozsKJGYPZ9X8F5YX8"
                val url_1 =
                    "https://docs.google.com/forms/d/e/1FAIpQLSfZqV9ENX4S5Z8_fRr45v541VM1S-gmJIcxEc2JrhAYt0RbRg/viewform"
                KGlobal.openInBrowser(this@LinksForYouActivity, url_1)
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@LinksForYouActivity,
                        StringConstants.sites_banner_google,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(
                    this@LinksForYouActivity,
                    StringConstants.sites_banner_google
                )
            } catch (e: ActivityNotFoundException) {
                Log.e("TAGGG", "Exception at view " + e.message)
            } catch (e: java.lang.Exception) {
                Log.e("TAGG", "Exception " + e.message)
            }
        }
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

    override fun onBackPressed() {
        if (showExitDialog) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.exit_app_msg))
                .setPositiveButton(
                    getString(R.string.quit)
                ) { dialogInterface, i ->
                    MyApplication.setAppUsedCountSeen(false)
                    finishAffinity()
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, i ->
                    startActivity(Intent(this@LinksForYouActivity, GalleryDashboard::class.java))
                    finish()
                }
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun websiteRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.paintology_website, null)
     /*   if (FirebaseAuth.getInstance().currentUser != null) {
            val rewardSetup = AppUtils.getRewardSetup(this@LinksForYouActivity)
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
            val rewardSetup = AppUtils.getRewardSetup(this@LinksForYouActivity)
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
            val rewardSetup = AppUtils.getRewardSetup(this@LinksForYouActivity)
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
        /*if (FirebaseAuth.getInstance().currentUser != null) {
            val rewardSetup = AppUtils.getRewardSetup(this@LinksForYouActivity)
            if (rewardSetup != null) {
                updateIncreasableRewardValue(
                    "apps_by_paintology",
                    rewardSetup.apps_by_paintology ?: 0,
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            }
        }*/
    }

    private fun googleClassRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.google_classroom, null)
     /*   if (FirebaseAuth.getInstance().currentUser != null) {
            val rewardSetup = AppUtils.getRewardSetup(this@LinksForYouActivity)
            if (rewardSetup != null) {
                updateIncreasableRewardValue(
                    "google_classroom",
                    rewardSetup.google_classroom ?: 0,
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            }
        }*/
    }

    private fun quoraRewardPoint() {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.quora_paintology, null)
      /*  if (FirebaseAuth.getInstance().currentUser != null) {
            val rewardSetup = AppUtils.getRewardSetup(this@LinksForYouActivity)
            if (rewardSetup != null) {
                updateIncreasableRewardValue(
                    "quora_paintology",
                    rewardSetup.quora_paintology ?: 0,
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            }
        }*/
    }

}