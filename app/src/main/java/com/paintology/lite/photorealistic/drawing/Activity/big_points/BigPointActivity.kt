package com.paintology.lite.photorealistic.drawing.Activity.big_points

import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paintology.lite.photorealistic.drawing.Activity.checkForIntroVideo
import com.paintology.lite.photorealistic.drawing.Activity.commonMenuClick
import com.paintology.lite.photorealistic.drawing.Activity.notifications.checkNotify
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.claimBigPoints
import com.paintology.lite.photorealistic.drawing.databinding.ActivityBigPointBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogBigPointBinding
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class BigPointActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBigPointBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        checkForIntroVideo(StringConstants.intro_big_points)

        initToolbar()
        fetchData()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val titleTextView = TextView(this)
        titleTextView.text = getString(R.string.pg_big_points)
        titleTextView.textSize = 20f
        titleTextView.setTypeface(null, Typeface.BOLD)
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        titleTextView.gravity = Gravity.CENTER
        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT,
            Gravity.START
        )
        binding.toolbar.addView(titleTextView, layoutParams)

    }


    private fun fetchData() {
        FirebaseFirestoreApi.getBigPoints().addOnCompleteListener {
            try {
                if (it.isSuccessful && it.result.exists() && it.result.data != null && it.result.data!!.contains(
                        "socials"
                    )
                ) {
                    val json = Gson().toJson(it.result.data!!["socials"]);

                    val turnsType = object : TypeToken<List<Social>>() {}.type
                    val data = Gson().fromJson<List<Social>>(json, turnsType)

                    if (data != null && data.isNotEmpty()) {
                        val adapter = ListAdapter(
                            this@BigPointActivity, data
                        ) { position, item ->
                            if (item.activity_key == "refer_a_friend") {
                                val bundle = Bundle()
                                bundle.putString("title", item.activity_key)
                                bundle.putString("type", "socials")
                                sendUserEventWithParam(
                                    StringConstants.big_points_do_activity,
                                    bundle
                                )
                                claimBigPoints(item.activity_key)
                                shareAppLink(item.url)
                            } else
                                showDialog(item)
                        }
                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.isNestedScrollingEnabled = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (it.isSuccessful && it.result.exists() && it.result.data != null && it.result.data!!.contains(
                        "internal_screens"
                    )
                ) {
                    val json = Gson().toJson(it.result.data!!["internal_screens"]);

                    val turnsType = object : TypeToken<List<Screen>>() {}.type
                    val data = Gson().fromJson<List<Screen>>(json, turnsType)

                    if (data != null && data.isNotEmpty()) {
                        val adapter = ScreenAdapter(
                            this@BigPointActivity, data
                        ) { position, item ->
                            val bundle = Bundle()
                            bundle.putString("title", item.activity_key)
                            bundle.putString("type", "internal_screens")
                            sendUserEventWithParam(StringConstants.big_points_do_activity, bundle)
                            claimBigPoints(item.activity_key)
                            var target = item.target_id
                            if (item.target_name == "country") {
                                target = StringConstants.constants.getString(
                                    StringConstants.constants.UserCountry,
                                    this
                                )
                            }
                            checkNotify(
                                item.target_type,
                                item.target_name,
                                target
                            )
                        }
                        binding.recyclerView2.adapter = adapter
                        binding.recyclerView2.isNestedScrollingEnabled = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun showDialog(model: Social) {
        try {
            val bundle = Bundle()
            bundle.putString("title", model.activity_key)
            bundle.putString("type", "socials")
            sendUserEventWithParam(StringConstants.big_points_open, bundle)
            val dialog = Dialog(this, R.style.NormalDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            val binding =
                DialogBigPointBinding.inflate(
                    LayoutInflater.from(this),
                    null,
                    false
                )
            dialog.setContentView(binding.root)
            var title = ""
            title += getString(R.string.ss_way_to_go) + "\n"
            title += getString(R.string.ss_you_ve_earned_20_points)

            val subtitle =
                getString(R.string.ss_dialog_des_1).format(model.points - 20) + "\n"
            "\n" +
                    getString(R.string.ss_dialog_dis_2)
            binding.tvDialogTitle.text = title
            binding.tvDialogContent.text = subtitle
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            binding.btnDo.setOnClickListener {
                val bundle1 = Bundle()
                bundle1.putString("title", model.activity_key)
                bundle1.putString("type", "socials")
                sendUserEventWithParam(StringConstants.big_points_do_activity, bundle1)
                claimBigPoints(model.activity_key + "_dialog")
                rateApp(model.url)
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareAppLink(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Paintology Drawing App")
        val app_url = " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID

        /* val app_url =
             url + StringConstants.constants.getString(StringConstants.constants.UserId, this)*/
        var text =
            "Paintology Drawing App\n\nCheck out this drawing made with the Paintology drawing app from the Google play store.\n\nA new and easy way to draw on your phone!\n\n"
        text += app_url
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun rateApp(url: String) {
        try {
            val rateIntent = rateIntentForUrl(url)
            startActivity(rateIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rateIntentForUrl(url: String): Intent {
        val packageName = packageName

        Log.d("packageName", "rateIntentForUrl: $packageName")

        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, packageName)))
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags =
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        intent.addFlags(flags)
        return intent
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.new_common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return commonMenuClick(item, StringConstants.intro_big_points)
    }


}