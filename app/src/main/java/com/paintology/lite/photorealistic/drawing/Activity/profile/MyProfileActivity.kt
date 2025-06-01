package com.paintology.lite.photorealistic.drawing.Activity.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import br.com.onimur.handlepathoz.HandlePathOz
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserProfile
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserUpdateProfile
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.isNetworkConnected
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail
import com.paintology.lite.photorealistic.drawing.CustomePicker.Gallery
import com.paintology.lite.photorealistic.drawing.FollowersFollowingActivity
import com.paintology.lite.photorealistic.drawing.Model.ModelArtMedium
import com.paintology.lite.photorealistic.drawing.Model.ModelArtPreference
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.ActivityMyProfileBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogProfileSavedBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogUsernameBinding
import com.paintology.lite.photorealistic.drawing.databinding.ImagePickDialogBinding
import com.paintology.lite.photorealistic.drawing.util.ChatUtils
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.LoginUtils
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.PermissionUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.parseUserProfile
import com.paintology.lite.photorealistic.drawing.util.sendUserEvent
import com.squareup.picasso.Picasso
import okio.FileNotFoundException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyProfileActivity : BaseActivity() {

    private var galleryCount = 0
    private var PostCount = 0

    private lateinit var handlePathOz: HandlePathOz
    private val binding by lazy {
        ActivityMyProfileBinding.inflate(layoutInflater)
    }

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    private var arr_country: MutableList<String>? = null
    private var arr_codes: MutableList<String>? = null

    private var arr_age: MutableList<String>? = null
    private var arr_gender: MutableList<String>? = null
    var constants = StringConstants()

    private val PICK_IMAGE_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2

    private var userProfile: UserProfile? = null
    var changedPath: String? = ""


    private var art_fav_left: LinearLayout? = null
    private var art_fav_right: LinearLayout? = null

    val lstArtFav: MutableList<ModelArtPreference> = ArrayList()

    var lst_art_ability: MutableList<String>? = null
    var lst_art_medium: MutableList<ModelArtMedium> = ArrayList()


    var mGoogleSignInClient: GoogleSignInClient? = null
    var account: GoogleSignInAccount? = null
    var isLoggedIn = false
    private var islogiFromFB = false
    private var islogiFromGoogle = false

    var user_descriprion = ""
    private var isProfileChanged = false

    var ll_followers: FrameLayout? = null
    var ll_following: FrameLayout? = null

    val Update_Post_Request = 101
    var db_firebase: FirebaseFirestore? = null
    var countryCodeNameList = HashMap<String, String>()

    private var ageSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var genderSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var mOnArtFavCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var mOnArtMediumCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private val artFavIds: MutableList<String> = ArrayList()
    private val artMediumIds: MutableList<String> = ArrayList()

    private var cameraLauncher: ActivityResultLauncher<Intent>? = null
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null

    var userID = "UserId"

    private var loggedUserId: String = ""

    private var chatUtils: ChatUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(binding.root)

        chatUtils = ChatUtils(this)
        art_fav_left = findViewById<View>(R.id.art_fav_left) as LinearLayout
        art_fav_right = findViewById<View>(R.id.art_fav_right) as LinearLayout



        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference


        db_firebase = FirebaseFirestore.getInstance()


        apiInterface = ApiClient.getClientForRX().create(ApiInterface::class.java)

        userID = constants.getString(constants.UserId, this)

        initViews()

        initToolbar()

        initListeners()

        startWork()
    }


    private fun showUserNameDialog(
        userField: String
    ) {
        if (userProfile == null)
            return

        try {
            val dialog = Dialog(this)
            val dialogUserBinding: DialogUsernameBinding =
                DialogUsernameBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialogUserBinding.getRoot())
            if (dialog.window != null) {
                dialog.window!!.setGravity(Gravity.CENTER)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            if (userField == "username") {
                if (userProfile!!.username.equals("")) {
                    dialogUserBinding.edtUserName.setText(userProfile!!.name.lowercase())
                } else {
                    dialogUserBinding.edtUserName.setText(userProfile!!.username.lowercase())
                }
            } else {
                dialogUserBinding.edtUserName.setText(userProfile!!.name)
            }
            dialogUserBinding.tvError.visibility = View.INVISIBLE
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialogUserBinding.imgCross.setOnClickListener { v -> dialog.dismiss() }
            dialogUserBinding.btnNo.setOnClickListener { v -> dialog.dismiss() }
            dialogUserBinding.btnYes.setOnClickListener { v ->
                if (dialogUserBinding.edtUserName.text!!.isNotEmpty()) {
                    dialogUserBinding.tvError.visibility = View.INVISIBLE
                    FireUtils.showProgressDialog(
                        this,
                        getString(R.string.ss_updating_please_wait, userField)
                    )
                    FirebaseFirestoreApi.updateProfileName(
                        userField,
                        dialogUserBinding.edtUserName.text.toString()
                    )
                        .addOnCompleteListener {
                            FireUtils.hideProgressDialog()
                            if (it.isSuccessful) {
                                dialog.dismiss()
                                val data = it.result.data as Map<String, Any>
                                val profile = parseUserProfile(data)
                                if (userField == "name") {
                                    userProfile!!.name = profile.name
                                    binding.tvName.text =
                                        dialogUserBinding.edtUserName.text.toString()
                                } else if (userField == "username") {
                                    userProfile!!.username = profile.username
                                    userProfile!!.social.paintology = profile.social.paintology
                                    binding.tvUsername.setText(userProfile!!.social.paintology)
                                }
                                showSuccessDialog(true, "$userField Successfully Updated")
                            } else {
                                val message = it.exception?.message.toString() ?: ""
                                dialogUserBinding.tvError.visibility = View.VISIBLE
                                dialogUserBinding.tvError.text = message
                                Toast.makeText(
                                    this@MyProfileActivity,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this@MyProfileActivity,
                        "Please Enter Username",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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

        mGoogleSignInClient = GoogleSignIn.getClient(this@MyProfileActivity, gso)

        val accessToken = AccessToken.getCurrentAccessToken()
        isLoggedIn = accessToken != null && !accessToken.isExpired
        account = GoogleSignIn.getLastSignedInAccount(this@MyProfileActivity)

        if (isLoggedIn) {
            islogiFromFB = true
            islogiFromGoogle = false
        } else if (account != null) {
            islogiFromFB = false
            islogiFromGoogle = true
        }

        if (KGlobal.isInternetAvailable(this@MyProfileActivity)) {
            fetchFromFirebase()
        } else {
            showToast(getString(R.string.no_internet_msg))
        }
    }

    private fun initViews() {

        if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
            binding.ivProfilePic.setImageResource(R.drawable.img_default_avatar)
            binding.edtUserDescription.visibility = View.GONE
            binding.edtUserDescription2!!.visibility = View.VISIBLE
            binding.edtUserDescription2!!.onSingleClick {
                FireUtils.openLoginScreen(this@MyProfileActivity, true)
            }
        } else {
            binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
            binding.edtUserDescription2!!.visibility = View.GONE
            binding.edtUserDescription.visibility = View.VISIBLE
        }
        // get user country and set
        val country = constants.getString(constants.UserCountry, this)
        binding.tvUserCountry.text = country

        lst_art_ability = mutableListOf()
        lst_art_medium = mutableListOf()

        arr_age = mutableListOf()
        arr_gender = mutableListOf()


        arr_country = mutableListOf()
        arr_codes = mutableListOf()

        ageSpinnerArrayAdapter = ArrayAdapter<String>(
            this@MyProfileActivity,
            android.R.layout.simple_spinner_item,
            arr_age!!
        )

        genderSpinnerArrayAdapter = ArrayAdapter<String>(
            this@MyProfileActivity,
            android.R.layout.simple_spinner_item,
            arr_gender!!
        )

        //selected item will look like a spinner set from XML
        ageSpinnerArrayAdapter?.setDropDownViewResource(R.layout.layout_spinner_text)
        binding.spinnerAge.adapter = ageSpinnerArrayAdapter

        genderSpinnerArrayAdapter?.setDropDownViewResource(R.layout.layout_spinner_text)
        binding.spinnerGender.adapter = genderSpinnerArrayAdapter


        // set selected spinner item color
        binding.spinnerAge.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p1 as TextView).setTextColor(resources.getColor(R.color.white))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // set spinner selected  item color
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p1 as TextView).setTextColor(resources.getColor(R.color.white))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        // set spinner selected  item color
        binding.spinnerArtAbility.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    (p1 as TextView).setTextColor(resources.getColor(R.color.white))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        // set spinner selected  item color
        binding.llPost.onSingleClick {
            gotoCommunity()
        }
        loggedUserId = constants.getString(constants.UserId, this@MyProfileActivity)
        if (constants.getBoolean(constants.IsGuestUser, this)) {
            binding.tvLogin.setText(R.string.login)
        } else {
            binding.tvLogin.setText(R.string.logout)
        }
    }

    private fun checkUrl(ediText: AppCompatEditText, ur1: Int, url2: Int): Boolean {

        if (ediText.text.toString().trim().startsWith(getString(ur1)) || ediText.text.toString()
                .trim().startsWith(getString(url2))
        ) {
            return false;
        } else {
            return true;
        }
    }

    private fun initListeners() {
        binding.apply {

            mOnArtFavCheckedChangeListener =
                CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                    try {
                        if (b) {
                            val value = compoundButton.id.toString()
                            if (!artFavIds.contains(value)) {
                                artFavIds.add(lstArtFav.get(value.toInt()).id)
                            }
                        } else {
                            artFavIds.remove(lstArtFav.get(compoundButton.id).id)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            mOnArtMediumCheckedChangeListener =
                CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                    try {
                        if (b) {
                            val value = compoundButton.id.toString()
                            if (!artMediumIds.contains(value)) {
                                artMediumIds.add(lst_art_medium.get(value.toInt()).id)
                            }
                        } else {
                            artMediumIds.remove(lst_art_medium.get(compoundButton.id).id)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }


            ivEdit.onSingleClick {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, true)
                } else {
                    showUserNameDialog("name")
                }
            }

            ivUsernameEdit.onSingleClick {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, true)
                } else {
                    showUserNameDialog("username")
                }
            }
            ivProfilePic.onSingleClick {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, true)
                } else {
                    if (BuildConfig.DEBUG) {
                        showToast(constants.my_profile_image_edit)
                    }
                    FirebaseUtils.logEvents(this@MyProfileActivity, constants.my_profile_image_edit)
                    if (PermissionUtils.checkCameraPermission(this@MyProfileActivity)) {
                        selectImage()
                    } else {
                        PermissionUtils.requestCameraPermission(this@MyProfileActivity, 2)
                    }
                }

            }

            btnAddNewPost.onSingleClick {

                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, true)
                } else {
                    if (BuildConfig.DEBUG) {
                        showToast(constants.click_my_profile_menu_post)
                    }
                    FirebaseUtils.logEvents(
                        this@MyProfileActivity,
                        constants.click_my_profile_menu_post
                    )

                    if (!isNetworkConnected()) {
                        showToast(getString(R.string.no_internet_msg))
                    }

                    val intentGallery = Intent(this@MyProfileActivity, Gallery::class.java).apply {
                        putExtra("title", "New Post")
                        putExtra("mode", 1)
                        putExtra("maxSelection", 500)
                        putExtra("isFromNewPost", true)
                    }
                    startActivity(intentGallery)
                }
            }

            btnLogout.onSingleClick {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, false)
                } else {
                    if (BuildConfig.DEBUG) {
                        showToast(constants.click_my_profile_menu_logout)
                    }
                    FirebaseUtils.logEvents(
                        this@MyProfileActivity,
                        constants.click_my_profile_menu_logout
                    )
                    LoginUtils(
                        this@MyProfileActivity,
                        mGoogleSignInClient,
                        sharedPref
                    ).confirmLogout(
                        islogiFromFB,
                        islogiFromGoogle
                    )
                }
            }


            llFollowers.onSingleClick {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, false)
                } else {
                    sendUserEvent(constants.my_profile_followers_click)
                    val intent =
                        Intent(this@MyProfileActivity, FollowersFollowingActivity::class.java)
                    intent.putExtra("data", userID)
                    intent.putExtra(
                        "user_name", constants.getString(
                            constants.Username,
                            this@MyProfileActivity
                        )
                    )
                    intent.putExtra(
                        "from_followers",
                        true
                    )
                    intent.putExtra(
                        "fromOtherUserProfile",
                        !(userID.equals(
                            constants.getString(
                                constants.UserId,
                                this@MyProfileActivity
                            )
                        ))
                    )
                    startActivity(intent)
                }
            }

            llFollowing.onSingleClick {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    FireUtils.openLoginScreen(this@MyProfileActivity, false)
                } else {
                    sendUserEvent(constants.my_profile_following_click)
                    val intent =
                        Intent(this@MyProfileActivity, FollowersFollowingActivity::class.java)
                    intent.putExtra("data", userID)
                    intent.putExtra(
                        "user_name", constants.getString(
                            constants.Username,
                            this@MyProfileActivity
                        )
                    )
                    intent.putExtra("from_followers", false)
                    intent.putExtra(
                        "fromOtherUserProfile", !(userID.equals(
                            constants.getString(
                                constants.UserId,
                                this@MyProfileActivity
                            )
                        ))
                    )
                    startActivity(intent)
                }
            }

            btnSave.onSingleClick {
                if (!KGlobal.checkInternet(this@MyProfileActivity)) {
                    KGlobal.showNetworkError(this@MyProfileActivity)
                    return@onSingleClick
                }
                updateProfile()
            }


            cameraLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    try {
                        val bitmap = data?.extras?.get("data") as Bitmap
                        val bytes = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                        Log.e("Activity", "Pick from Camera::>>> ")

                        val rootFolderPath =
                            KGlobal.getProfileFolderPath(this@MyProfileActivity)

                        val timeStamp = SimpleDateFormat(
                            "yyyyMMdd_HHmmss",
                            Locale.getDefault()
                        ).format(Date())
                        val destination = File(rootFolderPath, "IMG_$timeStamp.jpg")
                        var fo: FileOutputStream? = null
                        try {
                            destination.createNewFile()
                            fo = FileOutputStream(destination)
                            fo.write(bytes.toByteArray())
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            fo?.close()
                        }

                        changedPath = destination.absolutePath
                        binding.ivProfilePic.setImageBitmap(bitmap)
                        isProfileChanged = true

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            galleryLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val selectedImage: Uri? = data?.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this@MyProfileActivity.contentResolver,
                            selectedImage
                        )
                        val bytes = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                        val rootFolderPath =
                            KGlobal.getProfileFolderPath(this@MyProfileActivity)

                        val timeStamp = SimpleDateFormat(
                            "yyyyMMdd_HHmmss",
                            Locale.getDefault()
                        ).format(Date())
                        val destination = File(rootFolderPath, "IMG_$timeStamp.jpg")
                        var fo: FileOutputStream? = null
                        try {
                            destination.createNewFile()
                            fo = FileOutputStream(destination)
                            fo!!.write(bytes.toByteArray())
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            fo?.close()
                        }

                        changedPath = destination.absolutePath
                        binding.ivProfilePic.setImageBitmap(bitmap)
                        isProfileChanged = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun updateProfile() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(
                this@MyProfileActivity,
                constants.my_profile_save,
                Toast.LENGTH_SHORT
            ).show()
        }
        FirebaseUtils.logEvents(this@MyProfileActivity, constants.my_profile_save)

        if (binding.edtFacebookUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtFacebookUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtFacebookUrl,
                R.string.prefix_facebook_url,
                R.string.prefix_facebook_url_2
            ) || !URLUtil.isValidUrl(binding.edtFacebookUrl.text.toString().trim()))
        ) {
            binding.edtFacebookUrl.error = "Invalid URL"
            binding.edtFacebookUrl.requestFocus()
        } else if (binding.edtInstaUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtInstaUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtInstaUrl,
                R.string.prefix_insta_url,
                R.string.prefix_insta_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtInstaUrl.text.toString().trim()))
        ) {
            binding.edtInstaUrl.error = "Invalid URL"
            binding.edtInstaUrl.requestFocus()
        } else if (binding.edtYoutubeUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtYoutubeUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtYoutubeUrl,
                R.string.prefix_yt_url,
                R.string.prefix_yt_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtYoutubeUrl.text.toString().trim()))
        ) {
            binding.edtYoutubeUrl.error = "Invalid URL"
            binding.edtYoutubeUrl.requestFocus()
        } else if (binding.edtLinkedlnUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtLinkedlnUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtLinkedlnUrl,
                R.string.prefix_linkedin_url,
                R.string.prefix_linkedin_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtLinkedlnUrl.text.toString().trim()))
        ) {
            binding.edtLinkedlnUrl.error = "Invalid URL"
            binding.edtLinkedlnUrl.requestFocus()
        } else if (binding.edtTwitterUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtTwitterUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtTwitterUrl,
                R.string.prefix_twitter_url,
                R.string.prefix_twitter_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtTwitterUrl.text.toString().trim()))
        ) {
            binding.edtTwitterUrl.error = "Invalid URL"
            binding.edtTwitterUrl.requestFocus()
        } else if (!binding.etdWebsiteUrl.text.toString().trim()
                .isEmpty() && (!Patterns.WEB_URL.matcher(
                binding.etdWebsiteUrl.text.toString().trim()
            ).matches()
                    || !URLUtil.isValidUrl(binding.etdWebsiteUrl.text.toString().trim()))
        ) {
            binding.etdWebsiteUrl.error = "Invalid URL"
            binding.etdWebsiteUrl.requestFocus()
        } else if (binding.edtQouraUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtQouraUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtQouraUrl,
                R.string.prefix_quora_url,
                R.string.prefix_quora_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtQouraUrl.text.toString().trim()))
        ) {
            binding.edtQouraUrl.error = "Invalid URL"
            binding.edtQouraUrl.requestFocus()
        } else if (!binding.edtOtherUrl.text.toString().trim()
                .isEmpty() && (!Patterns.WEB_URL.matcher(binding.edtOtherUrl.text.toString().trim())
                .matches()
                    || !URLUtil.isValidUrl(binding.edtOtherUrl.text.toString().trim()))
        ) {
            binding.edtOtherUrl.error = "Invalid URL"
            binding.edtOtherUrl.requestFocus()
        } else if (binding.edtTwitterUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtTwitterUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtTwitterUrl,
                R.string.prefix_twitter_url,
                R.string.prefix_twitter_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtTwitterUrl.text.toString().trim()))
        ) {
            binding.edtTwitterUrl.error = "Invalid URL"
            binding.edtTwitterUrl.requestFocus()
        } else if (binding.edtPineterestUrl.text.toString().trim()
                .isNotEmpty() && (!Patterns.WEB_URL.matcher(
                binding.edtPineterestUrl.text.toString().trim()
            ).matches() || checkUrl(
                binding.edtPineterestUrl,
                R.string.prefix_pinterest_url,
                R.string.prefix_pinterest_url_2
            )
                    || !URLUtil.isValidUrl(binding.edtPineterestUrl.text.toString().trim()))
        ) {
            binding.edtPineterestUrl.error = "Invalid URL"
            binding.edtPineterestUrl.requestFocus()
        } else {
            updateProfilePicture()
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix: Matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1F, 1F)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1F, -1F)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun updateProfilePicture() {

        FireUtils.showProgressDialog(this, getString(R.string.ss_updating_profile_data))

//        showProgressDialog()
        if (MyApplication.get_realTimeDbUtils(this).currentUser != null) {
            var file: File? = File("")
            if (isProfileChanged && changedPath != null) {
                file = File(changedPath)
                val ref = storageReference?.child(
                    "users/" + constants.getString(
                        constants.UserId,
                        this@MyProfileActivity
                    ) + "/avatars/avtar.png"
                )
                ref!!.putFile(Uri.fromFile(file))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            ref.downloadUrl.addOnSuccessListener {
                                updateUserInfoFirebase(it.toString())
                            }.addOnFailureListener {
                                Log.e("TAGRR", it.message.toString())
                                updateUserInfoFirebase()
                            }
                        } else {
                            Log.e("TAGRR", it.exception.toString())
                            updateUserInfoFirebase()
                        }
                    }
            } else {
                updateUserInfoFirebase()
            }
        }
    }

    private fun updateUserInfoFirebase(profileUrl: String? = null) {
        if (MyApplication.get_realTimeDbUtils(this).currentUser != null && userProfile != null) {
            userProfile!!.bio = binding.edtUserDescription.text.toString()
            userProfile!!.age = binding.spinnerAge.selectedItem.toString()
            userProfile!!.gender = binding.spinnerGender.selectedItem.toString().lowercase()
            if (profileUrl != null) {
                MyApplication.get_realTimeDbUtils(this).getDbReference()
                    .child(constants.firebase_user_list).child(
                        constants.getString(
                            constants.UserId,
                            this@MyProfileActivity
                        )
                    ).child("profile_pic").setValue(profileUrl)
                userProfile!!.avatar = profileUrl
            }

            userProfile!!.social.facebook = binding.edtFacebookUrl.text.toString().trim()
            userProfile!!.social.instagram = binding.edtInstaUrl.text.toString().trim()
            userProfile!!.social.twitter = binding.edtTwitterUrl.text.toString().trim()
            userProfile!!.social.linkedin = binding.edtLinkedlnUrl.text.toString().trim()
            userProfile!!.social.youtube = binding.edtYoutubeUrl.text.toString().trim()
            userProfile!!.social.quora = binding.edtQouraUrl.text.toString().trim()
            userProfile!!.social.tiktok = binding.edtTiktokUrl.text.toString().trim()
            userProfile!!.social.website = binding.etdWebsiteUrl.text.toString().trim()
            userProfile!!.social.pinterest = binding.edtPineterestUrl.text.toString().trim()
            userProfile!!.social.other = binding.edtOtherUrl.text.toString().trim()

            if (userProfile!!.social.facebook == getString(R.string.prefix_facebook_url)) {
                userProfile!!.social.facebook = ""
            }

            if (userProfile!!.social.instagram == getString(R.string.prefix_insta_url)) {
                userProfile!!.social.instagram = ""
            }

            if (userProfile!!.social.twitter == getString(R.string.prefix_twitter_url)) {
                userProfile!!.social.twitter = ""
            }

            if (userProfile!!.social.linkedin == getString(R.string.prefix_linkedin_url)) {
                userProfile!!.social.linkedin = ""
            }

            if (userProfile!!.social.youtube == getString(R.string.prefix_yt_url)) {
                userProfile!!.social.youtube = ""
            }

            if (userProfile!!.social.quora == getString(R.string.prefix_quora_url)) {
                userProfile!!.social.quora = ""
            }

            if (userProfile!!.social.tiktok == getString(R.string.prefix_tiktok_url)) {
                userProfile!!.social.tiktok = ""
            }

            if (userProfile!!.social.pinterest == getString(R.string.prefix_pinterest_url)) {
                userProfile!!.social.pinterest = ""
            }

            userProfile!!.preferences.art.ability =
                binding.spinnerArtAbility.selectedItem.toString()

            var arrayFav: MutableList<HashMap<*, *>> = ArrayList()
            lstArtFav.filter {
                artFavIds.contains(it.id)
            }.forEach {
                val data = hashMapOf(
                    "id" to it.id,
                    "name" to it.name
                )
                arrayFav.add(data)
            }
            userProfile!!.preferences.art.favorites = arrayFav

            var arrayMed: MutableList<HashMap<*, *>> = ArrayList()
            lst_art_medium.filter {
                artMediumIds.contains(it.id)
            }.forEach {
                val data = hashMapOf(
                    "id" to it.id,
                    "name" to it.name
                )
                arrayMed.add(data)
            }
            userProfile!!.preferences.art.mediums = arrayMed

            val userUpProfile = UserUpdateProfile(
                country = userProfile!!.country,
                bio = userProfile!!.bio,
                name = userProfile!!.name,
                age = userProfile!!.age,
                gender = userProfile!!.gender,
                avatar = userProfile!!.avatar,
                social = userProfile!!.social,
                preferences = userProfile!!.preferences
            )

            FirebaseFirestoreApi.updateProfile(
                userUpProfile
            ).addOnCompleteListener {
                FireUtils.hideProgressDialog()
                if (it.isSuccessful) {
                    showToast("success")
                    showSuccessDialog(message = getString(R.string.profile_success))
                } else {
                    Log.e("TAG", it.exception.toString())
                    showToast("error")
                    showSuccessDialog(message = getString(R.string.profile_error))
                }
            }
        } else {
            FireUtils.hideProgressDialog()
        }
    }


    private fun fetchFromFirebase() {
        FireUtils.showProgressDialog(this, getString(R.string.ss_fetching_profile_data))
        FirebaseFirestoreApi.fetchProfilePrefsData()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val data = it.result.data as HashMap<*, *>

                    try {
                        val arrayFav = data.get("abilities")
                                as List<Map<String, HashMap<*, *>>>
                        arrayFav.forEach {
                            lst_art_ability!!.add(it.get("name").toString())
                        }
                        val artAbilityAdapter =
                            ArrayAdapter<String>(
                                this@MyProfileActivity,
                                android.R.layout.simple_spinner_item,
                                lst_art_ability!!
                            )

                        artAbilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerArtAbility.setAdapter(artAbilityAdapter)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        val arrayArt = data.get("arts")
                                as List<Map<String, HashMap<*, *>>>
                        arrayArt.forEach {
                            val artPreference = ModelArtPreference(
                                it.get("id").toString(),
                                it.get("name").toString()
                            )
                            lstArtFav.add(artPreference)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    try {
                        val arrayMed = data.get("mediums")
                                as List<Map<String, HashMap<*, *>>>
                        arrayMed.forEach {
                            val artMedium =
                                ModelArtMedium(it.get("id").toString(), it.get("name").toString())
                            lst_art_medium!!.add(artMedium)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        val arraycons = data.get("countries")
                                as List<Map<String, HashMap<*, *>>>
                        arraycons.forEach {
                            arr_codes!!.add(it.get("code").toString())
                            arr_country!!.add(it.get("name").toString())
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

        FireUtils.hideProgressDialog()

        try {
            this.userProfile = userProfile

            binding.toolbar.ivBtnShare.visibility = View.VISIBLE
            binding.toolbar.ivBtnShare.onSingleClick {
                shareApp()
            }

            binding.tvName.text = userProfile.name

            if (userProfile.gender.equals("male", ignoreCase = true)
            ) {
                constants.putString(
                    constants.UserGender,
                    constants.MALE,
                    this@MyProfileActivity
                )
            } else if (userProfile.gender.equals("female", ignoreCase = true)
            ) {
                constants.putString(
                    constants.UserGender,
                    constants.FEMALE,
                    this@MyProfileActivity
                )
            } else {
                constants.putString(
                    constants.UserGender,
                    constants.MALE,
                    this@MyProfileActivity
                )
            }

            if (userProfile.avatar.isNotEmpty()
            ) {
                Picasso.get()
                    .load(userProfile.avatar)
                    .into(binding.ivProfilePic)
                constants.putString(
                    constants.ProfilePicsUrl,
                    userProfile.avatar,
                    this@MyProfileActivity
                )
            } else {
                if (constants.getBoolean(constants.IsGuestUser, this@MyProfileActivity)) {
                    binding.ivProfilePic.setImageResource(R.drawable.img_default_avatar)
                } else {
                    binding.ivProfilePic.setImageResource(R.drawable.profile_icon)
                }
                constants.putString(
                    constants.ProfilePicsUrl,
                    "",
                    this@MyProfileActivity
                )
            }

            if (userProfile.bio.isNotEmpty()) {
                binding.edtUserDescription.setText(userProfile.bio)
                binding.edtUserDescription2!!.setText(userProfile.bio)
                user_descriprion = userProfile.bio
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
            if (country != "" && countryCodeNameList.contains(country)) {
                binding.cvCountry.visibility = View.GONE
                binding.tvUserCountry.text = countryCodeNameList[country]
                setProfileFlag(country)
            }

            arr_gender!!.add("Male")
            arr_gender!!.add("Female")
            genderSpinnerArrayAdapter!!.notifyDataSetChanged()

            if (userProfile.gender.isNotEmpty()
            ) binding.spinnerGender.setSelection(
                if (userProfile.gender.equals("male", ignoreCase = true)
                ) 0 else 1
            )
            else binding.spinnerGender.setSelection(0)


            try {
                val statMap =
                    userProfile.statistic

                binding.tvTotalFollowers.text = statMap.totalFollowers.toString() + ""
                binding.tvTotalFollowing.text = statMap.totalFollowing.toString() + ""

                binding.tvSubHeader?.text =
                    "Explore your " + statMap.totalPosts.toString() + " Drawings"

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                val socialMap = userProfile.social
                if (!TextUtils.isEmpty(socialMap.facebook)) {
                    binding.edtFacebookUrl.setText(socialMap.facebook)
                } else {
                    binding.edtFacebookUrl.setText(getString(R.string.prefix_facebook_url))
                }

                if (!TextUtils.isEmpty(socialMap.instagram)) {
                    binding.edtInstaUrl.setText(socialMap.instagram)
                } else {
                    binding.edtInstaUrl.setText(getString(R.string.prefix_insta_url))
                }

                if (!TextUtils.isEmpty(socialMap.pinterest)) {
                    binding.edtPineterestUrl.setText(socialMap.pinterest)
                } else {
                    binding.edtPineterestUrl.setText(getString(R.string.prefix_pinterest_url))
                }

                if (!TextUtils.isEmpty(socialMap.youtube)) {
                    binding.edtYoutubeUrl.setText(socialMap.youtube)
                } else {
                    binding.edtYoutubeUrl.setText(getString(R.string.prefix_yt_url))
                }

                if (!TextUtils.isEmpty(socialMap.linkedin)) {
                    binding.edtLinkedlnUrl.setText(socialMap.linkedin)
                } else {
                    binding.edtLinkedlnUrl.setText(getString(R.string.prefix_linkedin_url))
                }

                if (!TextUtils.isEmpty(socialMap.twitter)) {
                    binding.edtTwitterUrl.setText(socialMap.twitter)
                } else {
                    binding.edtTwitterUrl.setText(getString(R.string.prefix_twitter_url))
                }

                if (!TextUtils.isEmpty(socialMap.website)) {
                    binding.etdWebsiteUrl.setText(socialMap.website)
                }

                if (!TextUtils.isEmpty(socialMap.quora)) {
                    binding.edtQouraUrl.setText(socialMap.quora)
                } else {
                    binding.edtQouraUrl.setText(getString(R.string.prefix_quora_url))
                }

                if (!TextUtils.isEmpty(socialMap.other)) {
                    binding.edtOtherUrl.setText(socialMap.other)
                }

                if (!TextUtils.isEmpty(socialMap.tiktok)) {
                    binding.edtTiktokUrl.setText(socialMap.tiktok)
                } else {
                    binding.edtTiktokUrl.setText(getString(R.string.prefix_tiktok_url))
                }

                if (!TextUtils.isEmpty(socialMap.paintology)) {
                    binding.tvUsername.setText(socialMap.paintology)
                }


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


            for (i in 6..96) {
                arr_age!!.add(i.toString())
            }

            ageSpinnerArrayAdapter!!.notifyDataSetChanged()




            if (userProfile.age.isNotEmpty()
            ) {
                binding.spinnerAge.setSelection(
                    arr_age!!.indexOf(
                        userProfile.age
                    )
                )
            } else {
                binding.spinnerAge.setSelection(0)
            }

            val prefs = userProfile.preferences

            try {
                if (prefs.art.ability.isNotEmpty()
                ) {
                    val index =
                        lst_art_ability!!.indexOf(prefs.art.ability)
                    binding.spinnerArtAbility.setSelection(index)
                    constants.putString(
                        constants.userAbilityFromPref,
                        lst_art_ability!!.get(index),
                        this@MyProfileActivity
                    )
                } else {
                    binding.spinnerArtAbility.setSelection(lst_art_ability!!.size - 1)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {

                prefs.art.favorites.forEach {
                    it.let { it1 -> artFavIds.add(it1.get("id").toString()) }
                }

                for (i in lstArtFav.indices) {
                    val preference = lstArtFav[i]
                    val checkBox = CheckBox(this@MyProfileActivity)
                    checkBox.id = i
                    checkBox.text = preference.name
                    checkBox.buttonTintList = ColorStateList.valueOf(Color.WHITE)
                    checkBox.setTextColor(
                        ContextCompat.getColor(
                            this@MyProfileActivity,
                            R.color.white
                        )
                    )
                    if (artFavIds.contains(preference.id)) {
                        checkBox.isChecked = true
                    }
                    checkBox.setOnCheckedChangeListener(mOnArtFavCheckedChangeListener)
                    checkBox.isEnabled =
                        userID.equals(constants.getString(constants.UserId, this@MyProfileActivity))
                    if (i % 2 == 0) {
                        art_fav_left?.addView(checkBox)
                    } else {
                        art_fav_right?.addView(checkBox)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                prefs.art.mediums.forEach {
                    it.let { it1 -> artMediumIds.add(it1.get("id").toString()) }
                }

                for (i in lst_art_medium!!.indices) {
                    val preference = lst_art_medium!![i]
                    val checkBox = CheckBox(this@MyProfileActivity)
                    checkBox.id = i
                    checkBox.text = preference.name

                    checkBox.buttonTintList = ColorStateList.valueOf(Color.WHITE)
                    checkBox.setTextColor(
                        ContextCompat.getColor(
                            this@MyProfileActivity,
                            R.color.white
                        )
                    )
                    if (artMediumIds.contains(preference.id)) {
                        checkBox.isChecked = true
                    }
                    checkBox.isEnabled =
                        userID == constants.getString(constants.UserId, this@MyProfileActivity)

                    checkBox.setOnCheckedChangeListener(
                        mOnArtMediumCheckedChangeListener
                    )

                    if (i % 2 == 0) {
                        binding.artMediumLeft.addView(checkBox)
                    } else {
                        binding.artMediumRight.addView(checkBox)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setProfileFlag(countryCode: String) {
        val flag = countryCode.lowercase()

        val imageLoader = ImageLoader.Builder(this@MyProfileActivity)
            .componentRegistry { add(SvgDecoder(this@MyProfileActivity)) }
            .build()

        val request = ImageRequest.Builder(this)
            .crossfade(true)
            .crossfade(500)
            .data("https://raw.githubusercontent.com/lipis/flag-icons/main/flags/4x3/$flag.svg")
            .target(binding.imgCountry)
            .build()

        imageLoader.enqueue(request)

    }

    private fun selectImage() {
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding: ImagePickDialogBinding =
            ImagePickDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            llCamera.setOnClickListener { view ->
                dialog.dismiss()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher?.launch(intent)
            }

            llGallery.setOnClickListener { view ->
                dialog.dismiss()
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher?.launch(pickPhoto)
            }
        }

        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    PICK_IMAGE_CAMERA -> {
                        try {
                            val bitmap = data?.extras?.get("data") as Bitmap
                            val bytes = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                            Log.e("Activity", "Pick from Camera::>>> ")

                            val rootFolderPath =
                                KGlobal.getProfileFolderPath(this@MyProfileActivity)

                            val timeStamp = SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.getDefault()
                            ).format(Date())
                            val destination = File(rootFolderPath, "IMG_$timeStamp.jpg")
                            var fo: FileOutputStream? = null
                            try {
                                destination.createNewFile()
                                fo = FileOutputStream(destination)
                                fo.write(bytes.toByteArray())
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                fo?.close()
                            }

                            changedPath = destination.absolutePath
                            binding.ivProfilePic.setImageBitmap(bitmap)
                            isProfileChanged = true

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    PICK_IMAGE_GALLERY -> {
                        val selectedImage: Uri? = data?.data
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedImage
                            )
                            val bytes = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                            Log.e("Activity", "Pick from Gallery::>>> ")

                            val rootFolderPath =
                                KGlobal.getProfileFolderPath(this@MyProfileActivity)

                            val timeStamp = SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.getDefault()
                            ).format(Date())
                            val destination = File(rootFolderPath, "IMG_$timeStamp.jpg")
                            var fo: FileOutputStream? = null
                            try {
                                destination.createNewFile()
                                fo = FileOutputStream(destination)
                                fo.write(bytes.toByteArray())
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                fo?.close()
                            }

                            changedPath = destination.absolutePath
                            binding.ivProfilePic.setImageBitmap(bitmap)
                            isProfileChanged = true

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    Update_Post_Request -> {
                        if (data != null) {
                            val num_of_delet = data.getIntExtra("num_of_delet", 0)
                            val total =
                                (binding.tvTotalPost.text.toString().toInt() - num_of_delet)
                            binding.tvTotalPost.text = total.toString()
                        } else
                            showToast("Data Not Found")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TAGGG", "Exception at onResult ${e.message}")
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, proj, null, null, null)
        cursor?.use {
            val column_index: Int = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            it.moveToFirst()
            return it.getString(column_index)
        }
        return null
    }

    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val column_index: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(column_index)
        }
        return null
    }

    private fun handleError(t: Throwable) {
        Log.e("handleError", "Error: " + t.message)
        showToast("ERROR IN FETCHING API RESPONSE. Try again")
        FireUtils.hideProgressDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (BuildConfig.DEBUG) {
                showToast(constants.allow_storage_permission)
            }
            FirebaseUtils.logEvents(this, constants.allow_storage_permission)
            if (PermissionUtils.checkCameraPermission(this)) {
                selectImage()
            } else {
                PermissionUtils.requestCameraPermission(this, 2)
            }
        } else {
            try {
                val permission = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    showToast(resources.getString(R.string.storage_permission_msg))
                    if (BuildConfig.DEBUG) {
                        showToast(constants.deny_storage_permission)
                    }
                    FirebaseUtils.logEvents(this, constants.deny_storage_permission)
                } else {
                    if (BuildConfig.DEBUG) {
                        showToast(constants.allow_storage_permission)
                    }
                    FirebaseUtils.logEvents(this, constants.allow_storage_permission)
                    if (BuildConfig.DEBUG) {
                        showToast(constants.my_profile_image_edit)
                    }
                    FirebaseUtils.logEvents(this, constants.my_profile_image_edit)

                    if (PermissionUtils.checkCameraPermission(this)) {
                        selectImage()
                    } else {
                        PermissionUtils.requestCameraPermission(this, 2)
                    }
                }
            } catch (e: Exception) {
                Log.e(MyProfileActivity::class.java.name, e.message!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_add_post -> {
                if (BuildConfig.DEBUG) {
                    showToast(constants.click_my_profile_menu_post)
                }
                FirebaseUtils.logEvents(
                    this@MyProfileActivity,
                    constants.click_my_profile_menu_post
                )
                if (!KGlobal.isInternetAvailable(this@MyProfileActivity)) {
                    showToast(resources.getString(R.string.no_internet_msg))
                    return true
                }
                val intent = Intent(this@MyProfileActivity, Gallery::class.java)
                intent.putExtra("title", "New Post")
                intent.putExtra("mode", 1)
                intent.putExtra("maxSelection", 500)
                intent.putExtra("isFromNewPost", true)
                startActivity(intent)
                return true
            }

            R.id.action_feedback -> {
                if (BuildConfig.DEBUG) {
                    showToast(constants.click_my_profile_menu_feedback)
                }
                FirebaseUtils.logEvents(
                    this@MyProfileActivity,
                    constants.click_my_profile_menu_feedback
                )
                FireUtils.showFeedbackDialog(this)
                return true
            }

            R.id.action_google_play -> {
                if (BuildConfig.DEBUG) {
                    showToast(constants.click_my_profile_menu_googleplay_click)
                }
                FirebaseUtils.logEvents(
                    this@MyProfileActivity,
                    constants.click_my_profile_menu_googleplay_click
                )
                try {
                    val url =
                        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    // Handle exception
                }
                return true
            }

            R.id.action_logout -> {
                if (BuildConfig.DEBUG) {
                    showToast(constants.click_my_profile_menu_logout)
                }
                FirebaseUtils.logEvents(
                    this@MyProfileActivity,
                    constants.click_my_profile_menu_logout
                )
                LoginUtils(this, mGoogleSignInClient, sharedPref).confirmLogout(
                    islogiFromFB,
                    islogiFromGoogle
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    fun shareApp() {
        if (userProfile == null)
            return
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("text/plain")
            i.putExtra(
                Intent.EXTRA_SUBJECT,
                resources.getString(R.string.app_share_title)
            )
            var sAux =
                "Check out this great app for learning to draw on your phone and this person and their drawing..\n\n${userProfile!!.name} : \n\n${userProfile!!.social.paintology}"
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


    @SuppressLint("SetTextI18n")
    fun showSuccessDialog(
        isShare: Boolean = false,
        message: String
    ) {
        try {
            val dialog = Dialog(this)
            val binding: DialogProfileSavedBinding =
                DialogProfileSavedBinding.inflate(LayoutInflater.from(this), null, false)
            dialog.setContentView(binding.getRoot())
            binding.tvDialogContent.text = message
            binding.imgCross.onSingleClick {
                dialog.dismiss()
            }
            if (isShare) {
                binding.llShare.visibility = View.VISIBLE
                binding.btnUnlock.visibility = View.GONE
                binding.btnShare.onSingleClick {
                    dialog.setOnDismissListener {
                        shareApp()
                    }
                    dialog.dismiss()
                }
            } else {
                binding.llShare.visibility = View.GONE
                binding.btnUnlock.visibility = View.VISIBLE
            }
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            dialog.show()
            binding.btnUnlock.setOnClickListener { v ->
                dialog.dismiss()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun gotoGallery(drawing: NewDrawing) {
        startDrawingActivity(
            drawing,
            DrawingActivity::class.java,
            false,
            drawing.type
        )
    }

    fun gotoCommunity() {
        sendUserEvent(constants.my_profile_posts_click)
        val _intent = Intent(
            this@MyProfileActivity,
            CommunityDetail::class.java
        )
        _intent.setAction("isFromProfile")
        _intent.putExtra("user_id", userID)
        _intent.putExtra("user_name", binding.tvName.text.toString())
        startActivity(_intent)
    }


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
                                .error(R.drawable.feed_thumb_default)
                                .placeholder(R.drawable.feed_thumb_default)
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
                            FireUtils.hideProgressDialog()
                            return@addOnCompleteListener
                        }
                    }
                }
                hidePosts()
            }
    }

    @SuppressLint("SetTextI18n")
    fun hidePosts() {

        binding.tvTotalPost.text = (galleryCount + PostCount).toString()

        FireUtils.hideProgressDialog()
        if (binding.ivGalleryPost.visibility == View.GONE) {
            binding.llPosts.visibility = View.GONE
            binding.llNoPosts?.visibility = View.VISIBLE
            binding.tvNopostHeader?.text = "Your Gallery"
            binding.tvNopostSubHeader?.text =
                "You have no drawings in Gallery or posts in Community"
        } else {
            binding.ivCommunityPost.visibility = View.GONE
            binding.viewSpace?.visibility = View.GONE
            binding.viewSpace2?.visibility = View.GONE
        }
    }

}