package com.paintology.lite.photorealistic.drawing.ui.login

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.IntroActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserUpdateProfileFlag
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToastRelease
import com.paintology.lite.photorealistic.drawing.Enums.LoginType
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.updateProfileFlag
import com.paintology.lite.photorealistic.drawing.databinding.ActivityLoginBinding
import com.paintology.lite.photorealistic.drawing.onboarding.utils.Events
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import java.text.SimpleDateFormat
import java.util.Date



class LoginActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    val photoUrl = ""
    var isFirst = false

    // Google Login
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    // FB Login
    private lateinit var callbackManager: CallbackManager
    private var constants = StringConstants()

    private var progressDialog: ProgressDialog? = null

    private var fromProfile = false

    private var userCountryCode = ""


    /*This is the callback of facebook login , once user do login successfully this method will get called and do further operation respectively*/
    private val callback: FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            Log.e("TAG", "Facebook Event onSuccess FB login on success called")

            val request = GraphRequest.newMeRequest(result.accessToken) { jsonObj, _ ->

                showToast(constants.FacebookLoginSuccess)

                FirebaseUtils.logEvents(this@LoginActivity, constants.FacebookLoginSuccess)

                // Application code
                try {
                    Log.d(TAG, jsonObj!!.getString("id") + " " + jsonObj.toString())
                    var birthday = ""
                    if (jsonObj.has("birthday")) {
                        birthday = jsonObj.getString("birthday") // 01/31/1980 format
                    }
                    val fnm = if (jsonObj.has("first_name")) jsonObj.getString("first_name") else ""
                    val lnm = if (jsonObj.has("last_name")) jsonObj.getString("last_name") else ""
                    var mail = if (jsonObj.has("email")) jsonObj.getString("email") else ""
                    val gender = if (jsonObj.has("gender")) jsonObj.getString("gender") else ""
                    val fid = if (jsonObj.has("id")) jsonObj.getString("id") else ""

                    Log.e(
                        TAG,
                        "FB login Profile Info \n fnm $fnm \n lnm $lnm \n mail $mail \n gender $gender \n fid $fid \n birthday $birthday"
                    )

                    if (mail.isEmpty()) {
                        mail = "fb_$fid@gmail.com"
                    }

                    constants.putString(constants.Username, fnm, this@LoginActivity)
                    constants.putString(constants.Password, fid, this@LoginActivity)
                    constants.putString(constants.Email, mail, this@LoginActivity)
                    val model = LoginRequestModel(fid, fnm, mail, fid)

                    sendUserDataToFirebase(model, LoginType.facebook, photoUrl)

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "FB login Exception while get detail " + e.message)
                }

            }
            val parameters = Bundle()
            parameters.putString(
                "fields",
                "id, first_name, last_name, email, gender, birthday, location"
            )
            request.parameters = parameters
            request.executeAsync()
        }

        override fun onCancel() {
            Log.e(TAG, "Facebook Event OnError onCancel ")
        }

        override fun onError(error: FacebookException) {
            showToast(constants.FacebookLoginFailed)

            FirebaseUtils.logEvents(this@LoginActivity, constants.FacebookLoginFailed)

            Log.e(TAG, "Facebook Event OnError Called " + error.message, error)
        }
    }

    fun gotoUrl(url: String?) {
        try {
            val viewIntent =
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(url)
                )
            startActivity(viewIntent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun sendUserDataToFirebase(
        model: LoginRequestModel,
        loginType: LoginType,
        photoUrl: String
    ) {
        val data = Gson().toJson(model)
        Log.e(TAG, "sendUserDataToFirebase: $data \n $loginType  \n $photoUrl")

        val authProvider = when (loginType) {
            LoginType.facebook -> LoginType.facebook.toString()
            LoginType.google -> LoginType.google.toString()
            LoginType.guest -> LoginType.guest
            else -> null
        }

        authProvider?.let {
            sharedPref?.putString("authProvider", it.toString())
            sharedPref?.putString("name", model.user_name)
            sharedPref?.putString("email", model.user_email)
            sharedPref?.putString("photourl", photoUrl)
        } ?: run {
            Log.e(TAG, "Invalid login type: $loginType")
        }

        LoginUserFire(model, loginType)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        getIntentData()

        initViews()

        setListener()

        AppUtils.hideKeyboard(this)
    }

    private fun getIntentData() {

        userCountryCode = constants.getString(constants.UserCountryCode, this@LoginActivity)
        val intent = intent
        fromProfile = intent.getBooleanExtra("FromProfile", false)
        if (fromProfile) {
            mBinding.btnGuest.apply {
                text = getString(R.string.continue_as_guest)
            }
        }
    }

    private fun initViews() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Google Login
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

        googleSignInLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                val data = result.data
                println("result.resultCode == RESULT_OK")
                try {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)

                    constants.putString(
                        constants.Username,
                        if (account.displayName != null) account.displayName else "",
                        this@LoginActivity
                    )

                    constants.putString(
                        constants.Password,
                        if (account.email != null) account.email else "",
                        this@LoginActivity
                    )

                    constants.putString(
                        constants.Email,
                        if (account.email != null) account.email else "",
                        this@LoginActivity
                    )

                    val model = LoginRequestModel(
                        if (account.id != null) account.id else "",
                        if (account.displayName != null) account.displayName else "",
                        if (account.email != null) account.email else "",
                        if (account.email != null) account.email else "",
                    )
                    FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess)

                    sendUserDataToFirebase(
                        model,
                        LoginType.google,
                        account.photoUrl.toString()
                    )


                    showToast(constants.GoogleLoginSuccess)


                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.e(TAG, "Google sign in failed", e)

                    showToast(constants.GoogleLoginFailed)

                    FirebaseUtils.logEvents(this, constants.GoogleLoginFailed)
                    resetData()
                }

            }
        }

        // Initialize Facebook CallbackManager
        callbackManager = CallbackManager.Factory.create()

        /*
         * If you register the callback with LoginButton, don't need to register the callback on Login manager.
         */
        LoginManager.getInstance().registerCallback(callbackManager, callback)

        if (AppUtils.isFacebookLoginSupport(this)) {
            mBinding.btnFb.visibility = View.VISIBLE
        } else {
            mBinding.btnFb.visibility = View.GONE
        }
    }

    private fun setListener() {

        mBinding.tvIntroVideos.onSingleClick {
            openActivity(IntroActivity::class.java)
        }

        mBinding.tvTc.onSingleClick {
            KGlobal.openInBrowser(this, AppUtils.getLink(this, AppUtils.TERMS_LINK))
        }

        mBinding.tvPp.onSingleClick {
            KGlobal.openInBrowser(this, AppUtils.getLink(this, AppUtils.POLICY_LINK))
        }

        mBinding.btnGuest.setOnClickListener {
            if (fromProfile) {
                FireUtils.openDashboardScreen(this@LoginActivity)
            } else {

                FireUtils.showProgressDialog(
                    this@LoginActivity,
                    getString(R.string.ss_logging_in_please_wait)
                )

                auth.signInAnonymously().addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success")
                        val id = AppUtils.generateRandomDigits(9)
                        val userId = "Guest_$id"

                        constants.putString(
                            constants.UserId,
                            FirebaseAuth.getInstance().currentUser!!.uid.toString(),
                            this@LoginActivity
                        )

                        constants.putBoolean(constants.IsGuestUser, true, this@LoginActivity);

                        registerUser(
                            LoginType.guest.toString(),
                            userId,
                            userId,
                            "",
                            userCountryCode
                        )

                    } else {
                        FireUtils.hideProgressDialog()
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            }
        }


        mBinding.btnPaintology.setOnClickListener {
            showLoginDialog()
        }

        mBinding.btnSignupPaintology.setOnClickListener {
            showSignupDialog()
        }

        mBinding.btnGoogle.setOnClickListener {
            showToast(constants.GOOGLE_LOGIN)
            FirebaseUtils.logEvents(this, constants.GOOGLE_LOGIN)
            googleSignIn()

        }

        mBinding.btnFb.setOnClickListener {

            showToast(constants.FACEBOOK_LOGIN)

            FirebaseUtils.logEvents(this@LoginActivity, constants.FACEBOOK_LOGIN)

            LoginManager.getInstance()
                .logInWithReadPermissions(this@LoginActivity, listOf(EMAIL, PUBLIC_PROFILE))

        }
    }

    private fun showLoginDialog() {
        val dialog = BottomSheetPaintologyLogin(this, false) {
            loginUser(it)
        }
        dialog.show(supportFragmentManager, BottomSheetPaintologyLogin::class.java.simpleName)
    }

    private fun showSignupDialog() {
        val dialog = BottomSheetPaintologyLogin(this, true) {
            signupUser(it)
        }
        dialog.show(supportFragmentManager, BottomSheetPaintologyLogin::class.java.simpleName)
    }

    private fun signupUser(model: LoginRequestModel) {
        if (KGlobal.isInternetAvailable(this@LoginActivity)) {

            auth = FirebaseAuth.getInstance()

            signupUserFire(model, LoginType.paintology)

        } else
            Toast.makeText(
                this@LoginActivity,
                resources.getString(R.string.no_internet_msg),
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun loginUser(model: LoginRequestModel) {
        if (KGlobal.isInternetAvailable(this@LoginActivity)) {

            auth = FirebaseAuth.getInstance()

            LoginUserFire(model, LoginType.paintology)

        } else
            Toast.makeText(
                this@LoginActivity,
                resources.getString(R.string.no_internet_msg),
                Toast.LENGTH_SHORT
            ).show()
    }


    private fun registerUser(
        authProvider: String,
        email: String,
        name: String,
        avatar: String,
        country: String
    ) {


        FirebaseFirestoreApi.callUserInitFunction(
            authProvider,
            email,
            "",
            avatar,
            "",
            country,
            "",
            name
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                constants.putBoolean(
                    StringConstants.isNewUser,
                    true,
                    this@LoginActivity
                )

                constants.putString(StringConstants.isVideoShown, "", this@LoginActivity)


                MyApplication.get_realTimeDbUtils(this@LoginActivity).setCurrentUser();
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    FireUtils.updateToken(this@LoginActivity, it)
                }
                //  AppUtils.setLoggedIn(true)

                val result = task.result as HashMap<String, Any>
                if (result.containsKey("name")) {
                    val userName = result["name"].toString() ?: ""
                    constants.putString(constants.Username, userName, this@LoginActivity)
                }

                if (result.containsKey("country")) {
                    val userCountry = result["country"].toString() ?: ""
                    constants.putString(constants.UserCountry, userCountry, this@LoginActivity)
                }

                if (authProvider.toString().equals(LoginType.paintology)) {
                    FirebaseUtils.logEvents(this@LoginActivity, Events.EVENT_LOGIN_PAINTOLOGY)
                } else if (authProvider.toString().equals(LoginType.guest)) {
                    FirebaseUtils.logEvents(this@LoginActivity, Events.EVENT_LOGIN_GUEST)
                } else if (authProvider.toString().equals(LoginType.google)) {
                    FirebaseUtils.logEvents(this@LoginActivity, Events.EVENT_LOGIN_GOOGLE)
                } else if (authProvider.toString().equals(LoginType.facebook)) {
                    FirebaseUtils.logEvents(this@LoginActivity, Events.EVENT_LOGIN_FB)
                }

                if (authProvider != LoginType.guest.toString()) {
                    addData(result)
                } else {
                    FirebaseFirestoreApi.sendFlag()
                    AppUtils.savePurchasedProducts(
                        listOf<String>()
                    )
                    AppUtils.savePurchasedBrushes(listOf<String>())


                    FirebaseUtils.SetInstance(this)
                    FirebaseUtils.logEvents(this@LoginActivity, Events.EVENT_HOME)
                    FirebaseFirestoreApi.claimActivityPointsWithId(
                        StringConstants.default_bonus_point,
                        null
                    )
                    FireUtils.hideProgressDialog()
                    FireUtils.openDashboardScreen(this@LoginActivity)
                }
            } else {
                FireUtils.hideProgressDialog()
                // Function call failed
                showToastRelease("Authentication failed. Please check your credentials.")
                val e = task.exception
                Log.e("TAG", "Error: ", e)
            }
        }
    }

    private fun updateFlag() {
        val updateProfileFlag = UserUpdateProfileFlag(
            finish_intro = true
        )
        updateProfileFlag(
            updateProfileFlag
        ).addOnCompleteListener { }
    }

    @SuppressLint("SimpleDateFormat")
    fun addData(data: HashMap<String, Any>) {

        FirebaseFirestoreApi.sendFlag()
        AppUtils.savePurchasedProducts(
            listOf<String>()
        )
        AppUtils.savePurchasedBrushes(listOf<String>())

        FirebaseUtils.SetInstance(this)
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.default_bonus_point, null)
        val _map = java.util.HashMap<String, String>()
        try {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val currentDateandTime = sdf.format(Date())
            _map["create_date"] = currentDateandTime
        } catch (e: Exception) {
            Log.e("TAG", "saveUserDataIntoDb: " + e.message)
        }
        _map["login_type"] = data.get("auth_provider").toString() ?: ""
        _map["user_email"] = data.get("email").toString() ?: ""
        _map["user_name"] = data.get("name").toString() ?: ""
        _map["profile_pic"] = data.get("avatar").toString() ?: ""
        _map["user_id"] = constants.getString(constants.UserId, this@LoginActivity)
        _map["key"] = constants.getString(constants.UserId, this@LoginActivity)
        _map["is_online"] = "true"
        _map["is_typing"] = "false"

        constants.putString(constants.Username, _map["user_name"], this@LoginActivity)

        FirebaseDatabase.getInstance().getReference(constants.firebase_user_list)
            .child(constants.getString(constants.UserId, this@LoginActivity))
            .setValue(_map).addOnCompleteListener {
                FirebaseUtils.logEvents(this@LoginActivity, Events.EVENT_HOME)
                FireUtils.hideProgressDialog()
                FireUtils.openDashboardScreen(this@LoginActivity)
            }
    }

    private fun signupUserFire(model: LoginRequestModel, loginType: LoginType) {
        FireUtils.showProgressDialog(
            this@LoginActivity,
            getString(R.string.ss_signing_up_please_wait)
        )
        auth.createUserWithEmailAndPassword(model.user_email, model.user_password)
            .addOnCompleteListener(this) { task2 ->
                if (task2.isSuccessful) {
                    Log.e("TAG", "Registration successful: ${model.user_email}")
                    // Sign in the newly created user
                    auth.signInWithEmailAndPassword(
                        model.user_email,
                        model.user_password
                    ).addOnCompleteListener(this) { task3 ->
                        if (task3.isSuccessful) {

                            Log.e(
                                "TAG",
                                "Sign in after registration successful"
                            )
                            constants.putString(
                                constants.UserId,
                                FirebaseAuth.getInstance().currentUser!!.uid.toString(),
                                this@LoginActivity
                            )

                            constants.putBoolean(
                                constants.IsGuestUser,
                                false,
                                this@LoginActivity
                            );

                            registerUser(
                                loginType.toString(),
                                model.user_email,
                                model.user_name,
                                "",
                                userCountryCode
                            )

                            updateFlag()
                            // Proceed to the next activity or main screen
                            /*openActivity(GalleryDashboard::class.java)
                            finish()*/
                        } else {
                            FireUtils.hideProgressDialog()
                            showToastRelease(getString(R.string.fail_reg))
                        }
                    }

                } else {
                    FireUtils.hideProgressDialog()
                    showToastRelease("${getString(R.string.reg_fail)} ${task2.exception?.message}")
                }
            }
    }

    private fun LoginUserFire(model: LoginRequestModel, loginType: LoginType) {

        if (!isFirst) {
            isFirst = true
        } else {
            isFirst = false
        }
        FireUtils.showProgressDialog(
            this@LoginActivity,
            getString(R.string.ss_logging_in_please_wait)
        )
        FirebaseFirestoreApi.callAuthCheckFunction(
            loginType.toString(),
            model.user_email, false
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val result = task.result as HashMap<String, Any>
                // Extract the registered value
                val registered = result.get("registered") as? Boolean

                if (fromProfile) {
                    FirebaseAuth.getInstance().signOut()
                }

                if (registered == true) {
                    auth.signInWithEmailAndPassword(model.user_email, model.user_password)
                        .addOnCompleteListener(this) { task1 ->
                            if (task1.isSuccessful) {
                                Log.e("TAG", "Sign in successful as paintology user ")
                                // Proceed to the next activity or main screen
                                constants.putString(
                                    constants.UserId,
                                    FirebaseAuth.getInstance().currentUser!!.uid.toString(),
                                    this@LoginActivity
                                )


                                constants.putBoolean(
                                    constants.IsGuestUser,
                                    false,
                                    this@LoginActivity
                                );

                                constants.putBoolean(
                                    StringConstants.isNewUser,
                                    false,
                                    this@LoginActivity
                                )

                                if (result.containsKey("name")) {
                                    val userName = result["name"].toString() ?: ""
                                    constants.putString(
                                        constants.Username,
                                        userName,
                                        this@LoginActivity
                                    )
                                }

                                if (result.containsKey("country")) {
                                    val userCountry = result["country"].toString() ?: ""
                                    constants.putString(
                                        constants.UserCountry,
                                        userCountry,
                                        this@LoginActivity
                                    )
                                }

                                if (loginType.toString().equals(LoginType.paintology)) {
                                    FirebaseUtils.logEvents(
                                        this@LoginActivity,
                                        Events.EVENT_LOGIN_PAINTOLOGY
                                    )
                                } else if (loginType.toString().equals(LoginType.guest)) {
                                    FirebaseUtils.logEvents(
                                        this@LoginActivity,
                                        Events.EVENT_LOGIN_GUEST
                                    )
                                } else if (loginType.toString().equals(LoginType.google)) {
                                    FirebaseUtils.logEvents(
                                        this@LoginActivity,
                                        Events.EVENT_LOGIN_GOOGLE
                                    )
                                } else if (loginType.toString().equals(LoginType.facebook)) {
                                    FirebaseUtils.logEvents(
                                        this@LoginActivity,
                                        Events.EVENT_LOGIN_FB
                                    )
                                }

                                MyApplication.get_realTimeDbUtils(this@LoginActivity)
                                    .setCurrentUser();
                                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                                    FireUtils.updateToken(this@LoginActivity, it)
                                }
                                //  AppUtils.setLoggedIn(true)
                                addData(result)
                            } else {
                                if (isFirst && loginType == LoginType.facebook) {
                                    model.user_password = model.user_email
                                    LoginUserFire(model, loginType)
                                } else {
                                    FireUtils.hideProgressDialog()
                                    showToastRelease(getString(R.string.credentials))
                                }
                            }
                        }
                } else {
                    Log.e("TAG", "Registered: false \n email ${model.user_email}")
                    // If the user is not registered, create a new account
                    auth.createUserWithEmailAndPassword(model.user_email, model.user_password)
                        .addOnCompleteListener(this) { task2 ->
                            if (task2.isSuccessful) {
                                Log.e("TAG", "Registration successful: ${model.user_email}")
                                // Sign in the newly created user
                                auth.signInWithEmailAndPassword(
                                    model.user_email,
                                    model.user_password
                                ).addOnCompleteListener(this) { task3 ->
                                    if (task3.isSuccessful) {

                                        Log.e(
                                            "TAG",
                                            "Sign in after registration successful"
                                        )
                                        constants.putString(
                                            constants.UserId,
                                            FirebaseAuth.getInstance().currentUser!!.uid.toString(),
                                            this@LoginActivity
                                        )

                                        constants.putBoolean(
                                            constants.IsGuestUser,
                                            false,
                                            this@LoginActivity
                                        );

                                        registerUser(
                                            loginType.toString(),
                                            model.user_email,
                                            model.user_name,
                                            "",
                                            userCountryCode
                                        )

                                        updateFlag()
                                        // Proceed to the next activity or main screen
                                        /*openActivity(GalleryDashboard::class.java)
                                        finish()*/
                                    } else {
                                        FireUtils.hideProgressDialog()
                                        showToastRelease(getString(R.string.registration))
                                    }
                                }

                            } else {
                                FireUtils.hideProgressDialog()
                                Log.e(
                                    "TAG",
                                    "${getString(R.string.reg_fail)} ${task2.exception?.message}"
                                )
                                showToastRelease("${getString(R.string.reg_fail)} ${task2.exception?.message}")
                            }
                        }
                }
            } else {

                FireUtils.hideProgressDialog()

                if (task.exception != null) {
                    FireUtils.showCustomDialog(
                        this@LoginActivity,
                        getString(R.string.app_name),
                        task.exception!!.message.toString()
                    ) {
                    }
                }
                Log.e("TAG", "Auth check failed: ${task.exception?.message}")
            }
        }


        // Try to sign in the user
        /*auth.signInWithEmailAndPassword(model.user_email, model.user_password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT)
                        .show()
                    // Proceed to the next activity or main screen
                    openActivity(GalleryDashboard::class.java)
                } else {
                    // If sign in fails, try to create the user
                    auth.createUserWithEmailAndPassword(model.user_email, model.user_password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign up success
                                val user = auth.currentUser
                                Toast.makeText(
                                    baseContext,
                                    "Registration successful.",
                                    Toast.LENGTH_SHORT
                                ).show()


                                // Proceed to the next activity or main screen
                                openActivity(GalleryDashboard::class.java)
                            } else {
                                // If sign up fails, display a message to the user.
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed. " + task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }*/
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    fun showProgress(isFromUpload: Boolean) {
        try {
            progressDialog = ProgressDialog(this@LoginActivity)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            if (isFromUpload) {
                progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                progressDialog!!.max = 100
                progressDialog!!.progress = 0
                progressDialog!!.setMessage(getString(R.string.up_file))
            }
            progressDialog!!.setMessage(resources.getString(R.string.please_wait))
            progressDialog!!.show()
        } catch (_: java.lang.Exception) {
        }
    }

    fun hideProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    private fun resetData() {
        constants.putString(constants.Username, "", this@LoginActivity)
        constants.putString(constants.Password, "", this@LoginActivity)
        constants.putString(constants.Email, "", this@LoginActivity)
    }

    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
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

            }
            .show()
    }

    companion object {
        private const val TAG: String = "LoginActivity"
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"
    }
}