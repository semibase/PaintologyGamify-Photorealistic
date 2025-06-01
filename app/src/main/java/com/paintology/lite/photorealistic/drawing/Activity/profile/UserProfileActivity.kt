package com.paintology.lite.photorealistic.drawing.Activity.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.facebook.AccessToken
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.favourite.DrawingRepository
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserProfile
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity
import com.paintology.lite.photorealistic.drawing.Activity.profile.adapter.ArtAdapter
import com.paintology.lite.photorealistic.drawing.Activity.profile.dialogs.dialogChatLoading
import com.paintology.lite.photorealistic.drawing.Activity.profile.dialogs.dismissChatDialog
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail
import com.paintology.lite.photorealistic.drawing.FollowersFollowingActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityUserProfileBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogLogoutBinding
import com.paintology.lite.photorealistic.drawing.util.ChatUtils
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.parseUserProfile
import com.paintology.lite.photorealistic.drawing.util.sendUserEvent
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam
import com.squareup.picasso.Picasso


class UserProfileActivity : BaseActivity() {


    private var galleryCount = 0
    private var PostCount = 0

    private val binding by lazy {
        ActivityUserProfileBinding.inflate(layoutInflater)
    }

    var TotalSocialCount = 0

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var arr_age = mutableListOf<String>()
    var constants = StringConstants()

    private lateinit var userProfile: UserProfile

    var mGoogleSignInClient: GoogleSignInClient? = null
    var account: GoogleSignInAccount? = null
    var isLoggedIn = false
    private var islogiFromFB = false
    private var islogiFromGoogle = false

    var user_descriprion = ""

    var ll_followers: FrameLayout? = null
    var ll_following: FrameLayout? = null

    var db_firebase: FirebaseFirestore? = null
    var countryCodeNameList = HashMap<String, String>()

    // private var ageSpinnerArrayAdapter: ArrayAdapter<String>? = null

    var userID = "UserId"

    private var chatUtils: ChatUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(binding.root)

        chatUtils = ChatUtils(this)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference


        db_firebase = FirebaseFirestore.getInstance()


        apiInterface = ApiClient.getClientForRX().create(ApiInterface::class.java)

        if (intent != null && intent.hasExtra(StringConstants.SelectedUserId)) {
            userID = intent.getStringExtra(StringConstants.SelectedUserId).toString()
            if (!userID.equals("") && !FireUtils.isInteger(userID)) {
                setupUserActivity()

                startWork()


                initToolbar()

                initListeners()
            } else {
                fetchKey()
            }
        }
    }

    private fun fetchKey() {
        FireUtils.hideProgressDialog()
        ChatUtils.showCustomDialog(
            this,
            "User not found !",
            { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                finish()
            });
    }

    private fun startWork() {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@UserProfileActivity, gso)

        val accessToken = AccessToken.getCurrentAccessToken()
        isLoggedIn = accessToken != null && !accessToken.isExpired
        account = GoogleSignIn.getLastSignedInAccount(this@UserProfileActivity)

        if (isLoggedIn) {
            islogiFromFB = true
            islogiFromGoogle = false
        } else if (account != null) {
            islogiFromFB = false
            islogiFromGoogle = true
        }

        if (KGlobal.isInternetAvailable(this@UserProfileActivity)) {
            fetchFromFirebase()
        } else {
            showToast(getString(R.string.no_internet_msg))
        }
    }

    private fun setupUserActivity() {
        val uid = constants.getString(constants.UserId, this@UserProfileActivity)

        if (uid.isEmpty()) {
            // Handle the case where UID is empty
            Toast.makeText(this, "User ID is invalid", Toast.LENGTH_SHORT).show()
            fetchKey()
            return
        }

        FirebaseFirestoreApi.getUserFromFollowing(uid, userID).addOnSuccessListener {
            if (it.data != null) {
                binding.tvFollow.text = "UnFollow"
                binding.ivFollow.setImageResource(R.drawable.minus)
            } else {
                binding.tvFollow.text = "Follow"
                binding.ivFollow.setImageResource(R.drawable.plus)
            }
        }

        binding.btnFollow.onSingleClick {
            if (constants.getBoolean(constants.IsGuestUser, this)) {
                FireUtils.openLoginScreen(this@UserProfileActivity, true)
            } else if (binding.tvFollow.text.toString().equals("follow", ignoreCase = true)) {
                // FireUtils.showProgressDialog(this@UserProfileActivity, "Following Please Wait...")
                dialogChatLoading(getString(R.string.following_please_wait))
                val bundle = Bundle()
                bundle.putString("user_id", userID)
                sendUserEventWithParam(StringConstants.user_follow, bundle)
                FirebaseFirestoreApi.followUser(userID).addOnCompleteListener {
                    //  FireUtils.hideProgressDialog()
                    dismissChatDialog()
                    if (it.isSuccessful) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@UserProfileActivity,
                                constants.user_profile_follow_sucess,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@UserProfileActivity,
                            constants.user_profile_follow_sucess
                        )
                        binding.tvFollow.text = "UnFollow"
                        binding.ivFollow.setImageResource(R.drawable.minus)
                        binding.tvTotalFollowers.text =
                            (binding.tvTotalFollowers.text.toString().toInt() + 1).toString()
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@UserProfileActivity,
                                constants.user_profile_follow_fail,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@UserProfileActivity,
                            constants.user_profile_follow_fail
                        )
                    }
                }
            } else {
                showUnfollowDialog(binding.tvName.text.toString())
            }
        }

        binding.btnChat.onSingleClick {
            if (constants.getBoolean(constants.IsGuestUser, this)) {
                FireUtils.openLoginScreen(this@UserProfileActivity, true)
            } else {
                chatUtils?.openChatScreen(userID, binding.tvName.text.toString())
            }
        }


    }

    fun gotoGallery(drawing: NewDrawing) {
        startDrawingActivity(
            drawing,
            DrawingActivity::class.java,
            false,
            drawing.type
        )
    }

    fun gotoCommunity() {
        sendUserEvent(constants.user_profile_posts_clicks)
        val _intent = Intent(
            this@UserProfileActivity,
            CommunityDetail::class.java
        )
        _intent.setAction("isFromProfile")
        _intent.putExtra("user_id", userID)
        _intent.putExtra("user_name", binding.tvName.text.toString())
        startActivity(_intent)
    }

    @SuppressLint("SetTextI18n")
    fun fetchGalleryPosts() {

        val filterBy = "author.user_id:=$userID"
        val filters = filterBy?.let {
            hashMapOf("filter_by" to it)
        }

        val sortBy = "created_at:desc"
        val sorts = sortBy?.let {
            hashMapOf("sort_by" to it)
        }

        FirebaseFirestoreApi.fetchDrawingList(1, 1, filters, sorts)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data as HashMap<*, *>;
                    val dList = data.get("data") as List<*>


                    try {
                        val page = data.get("page") as HashMap<*, *>
                        val tot = page["total_elements"].toString().toInt() ?: 0
                        if (tot > 0) {
                            binding.tvShowGallery.setText("Gallery ($tot)")
                        }
                        galleryCount = tot
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (dList.isNotEmpty()) {
                        val item = dList[0] as HashMap<String, Any>
                        val drawing = parseDrawing(item)
                        if (drawing.images.content.isNotEmpty()) {
                            Picasso.get().load(Uri.parse(drawing.images.content))
                                .placeholder(R.drawable.feed_thumb_default)
                                .error(R.drawable.feed_thumb_default)
                                .into(binding.ivGalleryPost)
                            binding.ivGalleryPost.visibility = View.VISIBLE
                            binding.ivGalleryPost.onSingleClick {
                                gotoGallery(drawing)
                            }
                            binding.tvShowGallery.onSingleClick {
                                gotoGallery(drawing)
                            }
                            fetchCommunity()
                            return@addOnCompleteListener
                        }
                    }
                }
                binding.ivGalleryPost.visibility = View.GONE
                binding.tvShowGallery.visibility = View.GONE
                binding.viewSpace?.visibility = View.GONE
                binding.viewSpace2?.visibility = View.GONE
                fetchCommunity()
            }
    }

    private fun parseDrawing(data: Map<String, Any>): NewDrawing {
        val id = data?.get("id") as? String ?: ""
        val title = data?.get("title") as? String ?: ""
        val description = data?.get("description") as? String ?: ""
        val createdAt = data?.get("created_at") as? String ?: ""
        val type = data?.get("type") as? String ?: ""
        val referenceId = data?.get("reference_id") as? String ?: ""
        val tags = data?.get("tags") as? List<String> ?: emptyList()

        val imagesData = data?.get("images") as? Map<String, Any>
        val images = Images(content = imagesData?.get("content") as? String ?: "")

        val metadataData = data?.get("metadata") as? Map<String, Any>
        val metadata = Metadata(
            path = metadataData?.get("path") as? String ?: "",
            parentFolderPath = metadataData?.get("parent_folder_path") as? String ?: "",
            tutorialId = metadataData?.get("tutorial_id") as? String ?: ""
        )


        val statisticsData = data?.get("statistic") as? Map<String, Any>
        val statistics =Statistic(
            comments = statisticsData?.get("comments") as? Int,
            likes = statisticsData?.get("likes") as? Int ?: 0,
            ratings = statisticsData?.get("ratings") as? Int ?: 0,
            reviewsCount = statisticsData?.get("reviews_count") as? Int ?: 0,
            shares = statisticsData?.get("shares") as? Int ?: 0,
            views = statisticsData?.get("views") as? Int ?: 0
        )

        val authorData = data?.get("author") as? Map<String, Any>
        val author = Author(
            userId = authorData?.get("user_id") as? String ?: "",
            name = authorData?.get("name") as? String ?: "",
            avatar = authorData?.get("avatar") as? String ?: "",
            country = authorData?.get("country") as? String,
            level = authorData?.get("level") as? String
        )
        val linksData = data?.get("links") as? Map<String, Any>
        val links = Links(youtube = linksData?.get("youtube") as? String ?: "")

        return NewDrawing(
            id = id,
            title = title,
            description = description,
            createdAt = createdAt,
            type = type,
            tags = tags,
            images = images,
            links = links,
            metadata = metadata,
            statistic = statistics,
            author = author,
            referenceId = referenceId
        )
    }

    @SuppressLint("SetTextI18n")
    fun fetchCommunity() {
        val filterBy = "author.user_id:=$userID"
        val filters = filterBy?.let {
            hashMapOf("filter_by" to it)
        }

        val sortBy = "created_at:desc"
        val sorts = sortBy?.let {
            hashMapOf("sort_by" to it)
        }

        FirebaseFirestoreApi.fetchCommunityList(1, 1, filters, sorts)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data as HashMap<*, *>;
                    val dList = data.get("data") as List<*>


                    try {
                        val page = data.get("page") as HashMap<*, *>
                        val tot = page["total_elements"].toString().toInt() ?: 0
                        if (tot > 0) {
                            binding.tvShowCommunity.setText("Community ($tot)")
                        }
                        PostCount = tot
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    binding.tvTotalPost.text = (galleryCount + PostCount).toString()

                    if (dList.isNotEmpty()) {
                        val item = dList[0] as HashMap<*, *>
                        val images = item.get("images") as HashMap<*, *>
                        if (images.containsKey("content")) {
                            Picasso.get().load(Uri.parse(images["content"].toString()))
                                .placeholder(R.drawable.feed_thumb_default)
                                .error(R.drawable.feed_thumb_default)
                                .into(binding.ivCommunityPost)
                            binding.ivCommunityPost.visibility = View.VISIBLE
                            binding.ivCommunityPost.onSingleClick {
                                gotoCommunity()
                            }
                            binding.tvShowCommunity.onSingleClick {
                                gotoCommunity()
                            }
                            return@addOnCompleteListener
                        }
                    }
                }
                hidePosts()
            }
    }

    @SuppressLint("SetTextI18n")
    fun hidePosts() {
        FireUtils.hideProgressDialog()

        binding.tvTotalPost.text = (galleryCount + PostCount).toString()


        if (binding.ivGalleryPost.visibility == View.GONE) {
            binding.llPosts.visibility = View.GONE
            binding.llNoPosts.visibility = View.VISIBLE
            binding.tvNopostHeader.text = userProfile.name + " Gallery"
            binding.tvNopostSubHeader.text =
                userProfile.name + " has no drawings in Gallery or posts in Community"
        } else {
            binding.ivCommunityPost.visibility = View.GONE
            binding.viewSpace.visibility = View.GONE
            binding.viewSpace2.visibility = View.GONE
        }
    }

    fun showUnfollowDialog(Username: String) {
        val dialog = Dialog(this@UserProfileActivity)
        val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialogBinding.tvMessage.text = getString(R.string.ss_do_you_want_to_unfollow, Username)
        dialogBinding.btnSeePost.text = getString(R.string.unfollow)
        dialogBinding.imgCross.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnOk.text = getString(R.string.cancel)
        dialogBinding.btnOk.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSeePost.setOnClickListener {
            dialog.setOnDismissListener {
                dialogChatLoading(getString(R.string.unfollowing_please_wait))
                val bundle = Bundle()
                bundle.putString("user_id", userID)
                sendUserEventWithParam(StringConstants.user_unfollow, bundle)
                // FireUtils.showProgressDialog(this@UserProfileActivity, "UnFollowing Please Wait...")
                FirebaseFirestoreApi.unfollowUser(userID).addOnCompleteListener {
                    // FireUtils.hideProgressDialog()
                    dismissChatDialog()
                    if (it.isSuccessful) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@UserProfileActivity,
                                constants.user_profile_unfollow_success,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@UserProfileActivity,
                            constants.user_profile_unfollow_success
                        )

                        binding.tvFollow.text = "Follow"
                        binding.ivFollow.setImageResource(R.drawable.plus)
                        binding.tvTotalFollowers.text =
                            (binding.tvTotalFollowers.text.toString().toInt() - 1).toString()

                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@UserProfileActivity,
                                constants.user_profile_unfollow_fail,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@UserProfileActivity,
                            constants.user_profile_unfollow_fail
                        )
                    }
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }



    fun shareApp() {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("text/plain")
            i.putExtra(
                Intent.EXTRA_SUBJECT,
                resources.getString(R.string.app_share_title)
            )
            var sAux =
                "Check out this great app for learning to draw on your phone and this person and their drawing..\n\n${userProfile.name} : \n\n${userProfile.social.paintology}"
            sAux += "\n\nYou can download the app from the Google play store…\n" +
                    "\n" +
                    "https://play.google.com/store/apps/details?id=com.paintology.lite\n" +
                    "\n" +
                    "thanks!\n"
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            startActivity(Intent.createChooser(i, "Choose One"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initListeners() {
        binding.apply {

            ivShare.onSingleClick {
                shareApp()
            }

            llPost.onSingleClick {
                gotoCommunity()
            }
            llFollowers.onSingleClick {
                sendUserEvent(constants.user_profile_followers_clicks)
                val intent =
                    Intent(this@UserProfileActivity, FollowersFollowingActivity::class.java)
                intent.putExtra("data", userID)
                intent.putExtra(
                    "from_followers",
                    true
                )
                intent.putExtra(
                    "user_name",
                    userProfile.name
                )
                intent.putExtra(
                    "fromOtherUserProfile",
                    !(userID.equals(
                        constants.getString(
                            constants.UserId,
                            this@UserProfileActivity
                        )
                    ))
                )
                startActivity(intent)
            }

            llFollowing.onSingleClick {
                sendUserEvent(constants.user_profile_following_clicks)
                val intent =
                    Intent(this@UserProfileActivity, FollowersFollowingActivity::class.java)
                intent.putExtra("data", userID)
                intent.putExtra("from_followers", false)
                intent.putExtra(
                    "user_name",
                    userProfile.name
                )
                intent.putExtra(
                    "fromOtherUserProfile", !(userID.equals(
                        constants.getString(
                            constants.UserId,
                            this@UserProfileActivity
                        )
                    ))
                )
                startActivity(intent)

            }
        }
    }

    private fun fetchFromFirebase() {
        FireUtils.showProgressDialog(this, getString(R.string.ss_fetching_profile_data))
        FirebaseFirestoreApi.fetchProfilePrefsData()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data as HashMap<*, *>
                    try {
                        val arraycons = data.get("countries")
                                as List<Map<String, HashMap<*, *>>>
                        arraycons.forEach {
                            countryCodeNameList.put(
                                it.get("code").toString(),
                                it.get("name").toString()
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    fetchProfileData()
                    fetchGalleryPosts()
                } else {
                    FireUtils.hideProgressDialog()
                }
            }
    }

    fun fetchProfileData() {
        FirebaseFirestoreApi.userProfileFunction(userID)
            .addOnSuccessListener { result ->
                val data = result.data as Map<String, Any>
                setProfileData(parseUserProfile(data))
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error calling function", e)
                fetchKey()
            }
    }

    @SuppressLint("SetTextI18n")
    fun setProfileData(userProfile: UserProfile) {


        Log.e("User Id", userProfile.toString())


        FireUtils.hideProgressDialog()

        try {
            this.userProfile = userProfile

            binding.tvName.text = userProfile.name

            binding.tvHeader?.text = userProfile.name + "'s Art Collection"

            if (userProfile.gender.equals("male", ignoreCase = true)
            ) {
                binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
            } else if (userProfile.gender.equals("female", ignoreCase = true)
            ) {
                binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
            } else {
                binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
            }

            if (userProfile.avatar.isNotEmpty()
            ) {
                Picasso.get()
                    .load(userProfile.avatar)
                    .into(binding.ivProfilePic)
            } else {
                if (userProfile.gender.equals("male", ignoreCase = true)
                ) {
                    binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
                } else if (userProfile.gender.equals("female", ignoreCase = true)
                ) {
                    binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
                } else {
                    binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
                }
            }

            if (userProfile.bio.isNotEmpty()) {
                binding.tvUserDescription?.text = userProfile.bio
                user_descriprion = userProfile.bio
            } else {
                binding.tvUserDescription?.visibility = View.GONE
            }

            val level = userProfile.level
            if (level.isNotEmpty()) {
                binding.tvLevel.setText(level)
            } else {
                binding.tvLevel.setText("Beginner 1")
            }

            val points = userProfile.points
            binding.tvTotalPoints.setText(points.toString() + " Pts")

            val country = userProfile.country
            if (!country.equals("") && countryCodeNameList.contains(country)) {
                binding.tvUserCountry.setText(countryCodeNameList.get(country))
                setProfileFlag(country)
            } else {
                // binding.llCountry.visibility = View.GONE
            }

//            if (userProfile.gender.isNotEmpty()
//            ) binding.GenderTxt
//                ?.setSelection(
//                    if (userProfile.gender.equals("male", ignoreCase = true)
//                    ) 0 else 1
//                )
//            else binding.GenderTxt?.setSelection(0)

            if (userProfile.gender.isNotEmpty()) {
                Toast.makeText(this, userProfile.gender.toString(), Toast.LENGTH_SHORT).show()
                binding.GenderTxt!!.text = userProfile.gender
            } else {
                Toast.makeText(this, "Male", Toast.LENGTH_SHORT).show()

                binding.GenderTxt!!.text = "Male"

            }

            try {
                val statMap =
                    userProfile.statistic

                binding.tvTotalFollowers.text = statMap.totalFollowers.toString() + ""
                binding.tvTotalFollowing.text = statMap.totalFollowing.toString() + ""

                binding.tvSubHeader?.text =
                    "Explore their " + statMap.totalPosts.toString() + " Drawings and get inspired"
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                val socialMap = userProfile.social

                var mediaAlpha = 0.3f

                if (!TextUtils.isEmpty(socialMap.facebook)) {
                    TotalSocialCount += 1
                    binding.llFacebook.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_facebook_click)
                        gotoUrl(socialMap.facebook)
                    }
                } else {
                    binding.tvFacebook.alpha = mediaAlpha
                    binding.ivFacebook.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.instagram)) {
                    TotalSocialCount += 1

                    binding.llInstagram.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_Instagram_click)
                        gotoUrl(socialMap.instagram)
                    }
                } else {
                    binding.tvInstagram.alpha = mediaAlpha
                    binding.ivInstagram.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.youtube)) {
                    TotalSocialCount += 1
                    binding.llYoutube.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_Youtube_click)
                        gotoUrl(socialMap.youtube)
                    }
                } else {
                    binding.tvYoutube.alpha = mediaAlpha
                    binding.ivYoutube.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.linkedin)) {
                    TotalSocialCount += 1
                    binding.llLinkedin.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_Linkedin_click)
                        gotoUrl(socialMap.linkedin)
                    }
                } else {
                    binding.tvLinkedin.alpha = mediaAlpha
                    binding.ivLinkedin.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.twitter)) {
                    TotalSocialCount += 1
                    binding.llTwitter.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_Twitter_click)
                        gotoUrl(socialMap.website)
                    }
                } else {
                    binding.tvTwitter.alpha = mediaAlpha
                    binding.ivTwitter.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.website)) {
                    TotalSocialCount += 1
                    binding.llWebsite.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_Website_click)
                        gotoUrl(socialMap.website)
                    }
                } else {
                    binding.tvWebsite.alpha = mediaAlpha
                    binding.ivWebsite.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.quora)) {
                    TotalSocialCount += 1
                    binding.llQuora.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_Quora_click)
                        gotoUrl(socialMap.quora)
                    }
                } else {
                    binding.tvQuora.alpha = mediaAlpha
                    binding.ivQuora.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.pinterest)) {
                    TotalSocialCount += 1
                    binding.llPinterest.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_pinterest_click)
                        gotoUrl(socialMap.pinterest)
                    }
                } else {
                    binding.tvPinterest.alpha = mediaAlpha
                    binding.ivPinterest.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.other)) {
                    TotalSocialCount += 1
                    binding.llOther.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_other_click)
                        gotoUrl(socialMap.other)
                    }
                } else {
                    binding.tvOther.alpha = mediaAlpha
                    binding.ivOther.alpha = mediaAlpha
                }

                if (!TextUtils.isEmpty(socialMap.tiktok)) {
                    TotalSocialCount += 1
                    binding.llTiktok.onSingleClick {
                        sendUserEvent(StringConstants.user_profile_tiktok_click)
                        gotoUrl(socialMap.tiktok)
                    }
                } else {
                    binding.tvTiktok.alpha = mediaAlpha
                    binding.ivTiktok.alpha = mediaAlpha
                }

                binding.totalSocialCount.text = "Social Media Links ($TotalSocialCount)"

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }





            for (i in 6..96) {
                arr_age!!.add(i.toString())
            }

            val prefs = userProfile.preferences

            try {
                if (prefs.art.ability.isNotEmpty()
                ) {
                    binding.tvUserAbility?.text = prefs.art.ability
                } else {
                    binding.tvUserAbility.text = resources.getString(R.string.other)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


            try {
                var artMeds = arrayListOf<String>()
                prefs.art.mediums.forEach {
                    it.let { it1 -> artMeds.add(it1.get("name").toString()) }
                }
                if (artMeds.size <= 0) {
                    binding.rvArtFavorites?.visibility = View.GONE
                    binding.llNoArts?.visibility = View.VISIBLE
                    binding.tvNoartHeader?.text =
                        userProfile.name + " has not yet chosen an art favorite"
                } else {
                    val layoutManager = FlexboxLayoutManager(this)
                    layoutManager.setFlexWrap(FlexWrap.WRAP)
                    layoutManager.setFlexDirection(FlexDirection.ROW)
                    layoutManager.setJustifyContent(JustifyContent.FLEX_START)
                    layoutManager.setAlignItems(AlignItems.FLEX_START)

                    val adapter = ArtAdapter(this, artMeds)
                    binding.rvArtFavorites?.layoutManager = layoutManager
                    binding.rvArtFavorites?.adapter = adapter
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            var repo = DrawingRepository(this@UserProfileActivity)

            binding.addUserFav?.setOnClickListener {

                repo.insertUserProfile(
                    userID,
                    userProfile.name,
                    userProfile.bio,
                    userProfile.avatar,
                    userProfile.country
                )

            }


            try {
                var artFavs = arrayListOf<String>()
                prefs.art.favorites.forEach {
                    it.let { it1 -> artFavs.add(it1.get("name").toString()) }
                }
                if (artFavs.size <= 0) {
                    binding.rvArtMediums?.visibility = View.GONE
                    binding.llNoMediums?.visibility = View.VISIBLE
                    binding.tvNomedHeader?.text =
                        userProfile.name + " has not yet chosen an art favorite"
                } else {

                    val layoutManager2 = FlexboxLayoutManager(this)
                    layoutManager2.setFlexWrap(FlexWrap.WRAP)
                    layoutManager2.setFlexDirection(FlexDirection.ROW)
                    layoutManager2.setJustifyContent(JustifyContent.FLEX_START)
                    layoutManager2.setAlignItems(AlignItems.FLEX_START)

                    val adapter2 = ArtAdapter(this, artFavs)
                    binding.rvArtMediums?.layoutManager = layoutManager2
                    binding.rvArtMediums?.adapter = adapter2
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun gotoUrl(url: String) {
        try {
            val viewIntent =
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(url)
                )
            startActivity(viewIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setProfileFlag(countryCode: String) {
        val flag = countryCode.lowercase()

        val imageLoader = ImageLoader.Builder(this@UserProfileActivity)
            .componentRegistry { add(SvgDecoder(this@UserProfileActivity)) }
            .build()

        val request = ImageRequest.Builder(this)
            .crossfade(true)
            .crossfade(500)
            .data("https://raw.githubusercontent.com/lipis/flag-icons/main/flags/4x3/$flag.svg")
            .target(binding.imgCountry)
            .build()

        imageLoader.enqueue(request)

    }

    private fun initToolbar() {
        binding.toolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.profile)
            imgFav.hide()
        }
    }
}