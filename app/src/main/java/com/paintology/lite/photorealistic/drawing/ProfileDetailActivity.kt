package com.paintology.lite.photorealistic.drawing

import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.databinding.ActivityProfileDetailBinding

class ProfileDetailActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityProfileDetailBinding
    private var userName: String? = null
    private var artAbility: String? = null
    private var artFav: String? = null
    private var artMedium: String? = null
    private var fbUrl: String? = null
    private var instaUrl: String? = null
    private var ytUrl: String? = null
    private var tiktokUrl: String? = null
    private var quoraUrl: String? = null
    private var twitterUrl: String? = null
    private var linkedinUrl: String? = null
    private var webUrl: String? = null
    private var otherUrl: String? = null
    private var pinterestUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



        userName = intent.getStringExtra("user_name")
        artAbility = intent.getStringExtra("art_ability")
        artFav = intent.getStringExtra("art_fav")
        artMedium = intent.getStringExtra("art_medium")
        fbUrl = intent.getStringExtra("fb_url")
        instaUrl = intent.getStringExtra("insta_url")
        ytUrl = intent.getStringExtra("yt_url")
        tiktokUrl = intent.getStringExtra("tiktok_url")
        quoraUrl = intent.getStringExtra("quora_url")
        twitterUrl = intent.getStringExtra("twitter_url")
        linkedinUrl = intent.getStringExtra("linkedin_url")
        webUrl = intent.getStringExtra("web_url")
        otherUrl = intent.getStringExtra("other_url")
        pinterestUrl = intent.getStringExtra("pinterest_url")

        if (!TextUtils.isEmpty(fbUrl)) {
            if (fbUrl?.trim()?.contentEquals(getString(R.string.prefix_facebook_url)) == true) {
                fbUrl = ""
            }
        }

        if (!TextUtils.isEmpty(instaUrl)) {
            if (instaUrl?.trim()?.contentEquals(getString(R.string.prefix_insta_url)) == true) {
                instaUrl = ""
            }
        }

        if (!TextUtils.isEmpty(ytUrl)) {
            if (ytUrl?.trim()?.contentEquals(getString(R.string.prefix_yt_url)) == true) {
                ytUrl = ""
            }
        }

        if (!TextUtils.isEmpty(tiktokUrl)) {
            if (tiktokUrl?.trim()?.contentEquals(getString(R.string.prefix_tiktok_url)) == true) {
                tiktokUrl = ""
            }
        }

        if (!TextUtils.isEmpty(quoraUrl)) {
            if (quoraUrl?.trim()?.contentEquals(getString(R.string.prefix_quora_url)) == true) {
                quoraUrl = ""
            }
        }

        if (!TextUtils.isEmpty(twitterUrl)) {
            if (twitterUrl?.trim()?.contentEquals(getString(R.string.prefix_twitter_url)) == true) {
                twitterUrl = ""
            }
        }

        if (!TextUtils.isEmpty(linkedinUrl)) {
            if (linkedinUrl?.trim()
                    ?.contentEquals(getString(R.string.prefix_linkedin_url)) == true
            ) {
                linkedinUrl = ""
            }
        }

        if (!TextUtils.isEmpty(pinterestUrl)) {
            if (pinterestUrl?.trim()
                    ?.contentEquals(getString(R.string.prefix_pinterest_url)) == true
            ) {
                pinterestUrl = ""
            }
        }


        title = String.format("%s (Details)", userName)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.tvArtAbility.text = artAbility
        mBinding.tvArtFav.text = artFav
        mBinding.tvArtMedium.text = artMedium

        mBinding.tvFbUrl.text = fbUrl
        mBinding.tvInstaUrl.text = instaUrl
        mBinding.tvYtUrl.text = ytUrl
        mBinding.tvTiktokUrl.text = tiktokUrl
        mBinding.tvQuoraUrl.text = quoraUrl
        mBinding.tvTwitterUrl.text = twitterUrl
        mBinding.tvLinkedinUrl.text = linkedinUrl
        mBinding.tvWebUrl.text = webUrl
        mBinding.tvOtherUrl.text = otherUrl
        mBinding.tvPinterestUrl.text = pinterestUrl

        mBinding.tvFbUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvInstaUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvYtUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvTiktokUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvQuoraUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvTwitterUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvLinkedinUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvWebUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvOtherUrl.autoLinkMask = Linkify.WEB_URLS
        mBinding.tvPinterestUrl.autoLinkMask = Linkify.WEB_URLS

        mBinding.tvFbUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvInstaUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvYtUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvTiktokUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvQuoraUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvTwitterUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvLinkedinUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvWebUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvOtherUrl.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvPinterestUrl.movementMethod = LinkMovementMethod.getInstance()

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