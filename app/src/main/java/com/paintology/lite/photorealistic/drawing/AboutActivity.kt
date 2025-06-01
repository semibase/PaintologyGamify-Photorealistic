package com.paintology.lite.photorealistic.drawing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.databinding.ActivityAboutBinding
import com.paintology.lite.photorealistic.drawing.policy.PrivacyPolicyActivity

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_about_us)

//        mBinding.aboutWebview.settings.loadsImagesAutomatically = true
//        mBinding.aboutWebview.settings.javaScriptEnabled = true
//        mBinding.aboutWebview.settings.allowFileAccessFromFileURLs = true
//        mBinding.aboutWebview.loadUrl("file:///android_assets/about.html")

        initViews()
        setListeners()

        binding.aboutHeader.performClick()
        binding.tncHeader.performClick()
        binding.policyHeader.performClick()
    }

    private fun initViews() {
        binding.tvAboutText.text = getString(R.string.about_us_text, BuildConfig.VERSION_NAME)

        termsText()
        policyText()

//        mBinding.wvTnc.settings.loadsImagesAutomatically = true
//        mBinding.wvTnc.settings.javaScriptEnabled = true
//        mBinding.wvTnc.settings.allowFileAccessFromFileURLs = true
//        mBinding.wvTnc.loadUrl("https://paintology.com/terms-of-service/")
//
//        mBinding.wvPolicy.settings.loadsImagesAutomatically = true
//        mBinding.wvPolicy.settings.javaScriptEnabled = true
//        mBinding.wvPolicy.settings.allowFileAccessFromFileURLs = true
//        mBinding.wvPolicy.loadUrl("https://paintology.com/privacy-policy/")

    }

    private fun termsText() {
        // We have updated our Privacy Policy to make it easier for you to understand. You can find more details from the Privacy Policy and Terms of Service. By continuing, you are confirming that you agree to these requirements. Thank You.
        val description =
            "We have updated our Terms of Service to make it easier for you to understand. You can find more details from the Terms of Service." //AppUtils.getSliders(this)[0].slideDescription

        val spanString = SpannableString(description)

        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(
                    this@AboutActivity,
                    PrivacyPolicyActivity::class.java
                )
                intent.putExtra("value", "terms")
                startActivity(intent)
            }
        }

        spanString.setSpan(termsAndCondition, 113, 129, 0)
        spanString.setSpan(ForegroundColorSpan(Color.BLUE), 113, 129, 0)
        spanString.setSpan(UnderlineSpan(), 113, 129, 0)

        binding.tvTncText.highlightColor = resources.getColor(android.R.color.transparent)
        binding.tvTncText.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTncText.setText(spanString, TextView.BufferType.SPANNABLE)
        binding.tvTncText.isSelected = false
    }

    private fun policyText() {
        // We have updated our Privacy Policy to make it easier for you to understand. You can find more details from the Privacy Policy and Terms of Service. By continuing, you are confirming that you agree to these requirements. Thank You.
        val description =
            "We have updated our Privacy Policy to make it easier for you to understand. You can find more details from the Privacy Policy." //AppUtils.getSliders(this)[0].slideDescription

        val spanString = SpannableString(description)

//        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
//            override fun onClick(textView: View) {
//                val intent = Intent(
//                    this@AboutActivity,
//                    PrivacyPolicyActivity::class.java
//                )
//                intent.putExtra("value", "terms")
//                startActivity(intent)
//            }
//        }

        val privacy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(
                    this@AboutActivity,
                    PrivacyPolicyActivity::class.java
                )
                intent.putExtra("value", "privacy")
                startActivity(intent)
            }
        }

//        spanString.setSpan(termsAndCondition, 130, 146, 0)
//        spanString.setSpan(ForegroundColorSpan(Color.BLUE), 130, 146, 0)
//        spanString.setSpan(UnderlineSpan(), 130, 146, 0)

        spanString.setSpan(privacy, 111, 125, 0)
        spanString.setSpan(ForegroundColorSpan(Color.BLUE), 111, 125, 0)
        spanString.setSpan(UnderlineSpan(), 111, 125, 0)

        binding.tvPolicyText.highlightColor = resources.getColor(android.R.color.transparent)
        binding.tvPolicyText.movementMethod = LinkMovementMethod.getInstance()
        binding.tvPolicyText.setText(spanString, TextView.BufferType.SPANNABLE)
        binding.tvPolicyText.isSelected = false
    }

    private fun setListeners() {
        binding.aboutHeader.setOnClickListener {
            if (binding.tvAboutText.isShown) {
                binding.tvAboutText.visibility = View.GONE
            } else {
                binding.tvAboutText.visibility = View.VISIBLE
            }
        }

        binding.tncHeader.setOnClickListener {
            if (binding.tvTncText.isShown) {
                binding.tvTncText.visibility = View.GONE
            } else {
                binding.tvTncText.visibility = View.VISIBLE
            }

//            if (mBinding.wvTnc.isShown) {
//                mBinding.wvTnc.visibility = View.GONE
//            } else {
//                mBinding.wvTnc.visibility = View.VISIBLE
//            }
        }

        binding.policyHeader.setOnClickListener {
            if (binding.tvPolicyText.isShown) {
                binding.tvPolicyText.visibility = View.GONE
            } else {
                binding.tvPolicyText.visibility = View.VISIBLE
            }

//            if (mBinding.wvPolicy.isShown) {
//                mBinding.wvPolicy.visibility = View.GONE
//            } else {
//                mBinding.wvPolicy.visibility = View.VISIBLE
//            }
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
}