package com.paintology.lite.photorealistic.drawing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.paintology.lite.photorealistic.drawing.Enums.LoginType
import com.paintology.lite.photorealistic.drawing.Model.LoginRequestModel
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySignupBinding
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.SendDeviceToken
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    // Google Login
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var account: GoogleSignInAccount? = null
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    // FB Login
    private lateinit var callbackManager: CallbackManager
    private var constants = StringConstants()

    /*This is the callback of facebook login , once user do login successfully this method will get called and do further operation respectively*/
    private val callback: FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            Log.e("TAGG", "Facebook Event onSuccess FB login onsuccess called")
            val request = GraphRequest.newMeRequest(
                result.accessToken
            ) { jsonObj, response ->
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@SignupActivity,
                        constants.FacebookLoginSuccess,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FirebaseUtils.logEvents(
                    this@SignupActivity,
                    constants.FacebookLoginSuccess
                )

                handleFacebookAccessToken(result.accessToken, jsonObj)

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
            Log.e("TAGGG", "Facebook Event OnError onCancel ")
        }

        override fun onError(error: FacebookException) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@SignupActivity,
                    constants.FacebookLoginFailed,
                    Toast.LENGTH_SHORT
                ).show()
            }
            FirebaseUtils.logEvents(this@SignupActivity, constants.FacebookLoginFailed)
            Log.e("TAGGG", "Facebook Event OnError Called " + error.message, error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        setListener()

        AppUtils.hideKeyboard(this)
    }

    private fun initViews() {
        val text = binding.tvDontHaveAccount.text
        val start: Int = text.toString().lastIndexOf(" ") + 1
        val end: Int = text.length

        //create your spannable
        val spannable = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {

                val fromPaintor = intent.getBooleanExtra("fromPaintor", false)

                val intent = Intent(
                    this@SignupActivity,
                    LoginActivity::class.java
                )
                if (fromPaintor) {
                    intent.putExtra("title", getString(R.string.login_heading_community))
                    intent.putExtra("fromPaintor", true)
                }

                startActivity(intent)
                finish()
            }
        }

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvDontHaveAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDontHaveAccount.text = spannable

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Google Login
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@SignupActivity, gso)

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                val data = result.data

                try {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@SignupActivity,
                            constants.GoogleLoginSuccess,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess)
                    account.idToken?.let { firebaseAuthWithGoogle(it, account) }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@SignupActivity,
                            constants.GoogleLoginFailed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            binding.btnFb.visibility = View.VISIBLE
        } else {
            binding.btnFb.visibility = View.GONE
        }
    }

    private fun setListener() {
        binding.chkPasswordVisibility.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etPassword.transformationMethod = null

                val end = binding.etPassword.text.length
                binding.etPassword.setSelection(end)

            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod()

                val end = binding.etPassword.text.length
                binding.etPassword.setSelection(end)
            }
        }
        binding.btnSignup.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(username)
            ) {

//                registerUserOnServer(username, email, password, LoginType.LOGIN_FROM_PAINTOLOGY)

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser

                            val model = LoginRequestModel(user?.uid, username, email, password)
                            updateUI(user, model)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(null, null)
                        }
                    }
            }
        }

        binding.btnGoogle.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Toast.makeText(this@SignupActivity, constants.GOOGLE_LOGIN, Toast.LENGTH_SHORT)
                    .show()
            }
            FirebaseUtils.logEvents(this, constants.GOOGLE_LOGIN)

            googleSignIn()

        }

        binding.btnFb.setOnClickListener {

            if (BuildConfig.DEBUG) {
                Toast.makeText(this@SignupActivity, constants.FACEBOOK_LOGIN, Toast.LENGTH_SHORT)
                    .show()
            }
            FirebaseUtils.logEvents(this@SignupActivity, constants.FACEBOOK_LOGIN)

            LoginManager.getInstance()
                .logInWithReadPermissions(
                    this@SignupActivity,
                    listOf(EMAIL, PUBLIC_PROFILE)
                )

        }
    }

    //    private fun updateUI(user: FirebaseUser?, model: LoginRequestModel?, type: LoginType) {
    private fun updateUI(user: FirebaseUser?, model: LoginRequestModel?) {
        hideProgressBar()
        if (user != null) {

            constants.putString(constants.Username, model?.user_name, this@SignupActivity)
            constants.putString(constants.Password, model?.user_password, this@SignupActivity)
            constants.putString(constants.Email, model?.user_email, this@SignupActivity)


//            registerUserOnServer(user, model, type)

            setResult(Activity.RESULT_OK, intent)
        } else {
            constants.putString(constants.Username, "", this@SignupActivity)
            constants.putString(constants.Password, "", this@SignupActivity)
            constants.putString(constants.Email, "", this@SignupActivity)
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(idToken: String, account: GoogleSignInAccount) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@SignupActivity,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@SignupActivity,
                                constants.GoogleLoginSuccess,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(this, constants.GoogleLoginSuccess)

//                        val user: FirebaseUser = auth.currentUser!!
//
//                        val model = LoginRequestModel(
//                            if (account.id != null) account.id else "",
//                            if (account.displayName != null) account.displayName else "",
//                            if (account.email != null) account.email else "",
//                            ""
//                        )

//                        updateUI(user, model, LoginType.LOGIN_FROM_GOOGLE)

                        val builder = AlertDialog.Builder(this@SignupActivity)
                        val view = layoutInflater.inflate(R.layout.dialog_edit_text, null, false)
                        val editText = view.findViewById<EditText>(R.id.et_username)

                        builder.setTitle(getString(R.string.enter_user))
                            .setView(editText)
                            .setPositiveButton(
                                getString(R.string.done)
                            ) { dialogInterface, i ->
                                run {
                                    dialogInterface.dismiss()
                                    val username = editText.text.toString()
                                    registerUserOnServer(
                                        username,
                                        account.email!!,
                                        "",
                                        LoginType.google
                                    )
                                }
                            }
                            .show()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@SignupActivity,
                                constants.GoogleLoginFailed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(this, constants.GoogleLoginFailed)
                        resetData()
                    }
                    hideProgressBar()
                })
    }

    private fun handleFacebookAccessToken(token: AccessToken, jsonObj: JSONObject?) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    val fnm =
                        if (jsonObj!!.has("first_name")) jsonObj.getString("first_name") else ""
                    val lnm =
                        if (jsonObj.has("last_name")) jsonObj.getString("last_name") else ""
                    val email =
                        if (jsonObj.has("email")) jsonObj.getString("email") else ""
                    val gender =
                        if (jsonObj.has("gender")) jsonObj.getString("gender") else ""
                    val fid = if (jsonObj.has("id")) jsonObj.getString("id") else ""

//                    val model = LoginRequestModel(fid, fnm, mail, "")
//
//                    updateUI(user, model, LoginType.LOGIN_FROM_FB)

                    val builder = AlertDialog.Builder(this@SignupActivity)
                    val view = layoutInflater.inflate(R.layout.dialog_edit_text, null, false)
                    val editText = view.findViewById<EditText>(R.id.et_username)

                    builder.setTitle(getString(R.string.enter_user))
                        .setView(editText)
                        .setPositiveButton(
                            getString(R.string.done)
                        ) { dialogInterface, i ->
                            run {
                                dialogInterface.dismiss()
                                val username = editText.text.toString()
                                registerUserOnServer(
                                    username,
                                    email,
                                    "",
                                    LoginType.facebook
                                )
                            }
                        }
                        .show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    resetData()
                }
            }
    }

    /*This method will called an API to store user data in server.this method will called once user do login via facebook OR Google.*/
    private fun registerUserOnServer(
        username: String,
        email: String,
        password: String,
        loginType: LoginType
    ) {
        val _map = HashMap<String, RequestBody>()

        val userName: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            username ?: ""
        )
        val userEmail: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            email
        )
        val userPassword: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            password ?: ""
        )
        val req_ip_address: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            constants.getString(constants.IpAddress, this@SignupActivity)
        )
        val req_ip_country: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            constants.getString(constants.UserCountry, this@SignupActivity)
        )
        val req_ip_city: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            constants.getString(constants.UserCity, this@SignupActivity)
        )

        try {
            val _ip = constants.getString(constants.IpAddress, this@SignupActivity)
            val _country = constants.getString(constants.UserCountry, this@SignupActivity)
            val _city = constants.getString(constants.UserCity, this@SignupActivity)
            Log.e("TAGG", "Region Data  _ip $_ip _country $_country _city $_city")
        } catch (e: Exception) {
        }


        _map["user_ip"] = req_ip_address
        _map["country"] = req_ip_country
        _map["city"] = req_ip_city

//        _map["user_id"] = userId
        _map["user_name"] = userName
        _map["user_email"] = userEmail
        if (!TextUtils.isEmpty(password)) {
            _map["user_password"] = userPassword
        }


        if (loginType == LoginType.facebook) {
            val l_Type: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                resources.getString(R.string.type_facebook)
            )
            _map["flag"] = l_Type

            val actionType: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                "social_media"
            )
            _map["action_type"] = actionType

        } else if (loginType == LoginType.google) {
            val l_Type: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    resources.getString(R.string.type_google)
                )
            _map["flag"] = l_Type

            val actionType: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                "social_media"
            )
            _map["action_type"] = actionType

        } else {
            val l_Type: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                resources.getString(R.string.type_paintology)
            )
            _map["flag"] = l_Type

            val actionType: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                "user_register_simple"
            )
            _map["action_type"] = actionType
        }

        val apiInterface: ApiInterface = ApiClient.getClient_1().create(ApiInterface::class.java)
        val call: Call<LoginResponseModel> = apiInterface.addUserData(ApiClient.SECRET_KEY, _map)

        showProgressBar()

        try {
            call.enqueue(object : Callback<LoginResponseModel> {
                override fun onResponse(
                    call: Call<LoginResponseModel>,
                    response: Response<LoginResponseModel>
                ) {
                    if (response.isSuccessful) {

                        val loginResponseModel = response.body()!!
                        val responseData = loginResponseModel.getObjData()

                        if (responseData != null) {
                            if (responseData.errorMsg != null) {
                                val builder = AlertDialog.Builder(this@SignupActivity)
                                builder.setTitle(getString(R.string.error))
                                    .setMessage(responseData.errorMsg)
                                    .setPositiveButton(
                                        getString(R.string.ok)
                                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                                    .show()
                                hideProgressBar()
                                return
                            } else
                                if (responseData.getUser_id() != null) {

//                                val email = responseData.getUserEmail()

                                    if (responseData.isZipUploaded.equals(
                                            "true",
                                            ignoreCase = true
                                        )
                                    ) {
                                        constants.putString(
                                            constants.IsFileUploaded,
                                            "true",
                                            this@SignupActivity
                                        )
                                    } else constants.putString(
                                        constants.IsFileUploaded,
                                        "false",
                                        this@SignupActivity
                                    )
                                    constants.putString(
                                        constants.UserId,
                                        responseData.getUser_id().toString() + "",
                                        this@SignupActivity
                                    )
                                    constants.putString(
                                        constants.Salt,
                                        if (response.body()!!.getObjData().getSalt() != null
                                        ) response.body()!!
                                            .getObjData().getSalt() else "",
                                        this@SignupActivity
                                    )
                                    Log.e(
                                        "TAGGG",
                                        "Salt Value is " + response.body()!!.getObjData().getSalt()
                                    )

                                    val _user_id =
                                        constants.getString(constants.UserId, this@SignupActivity)
                                    //                            autoLoginRegister(response.body().getObjData().getStatus());
//                            MyApplication.get_realTimeDbUtils(this)
//                                .autoLoginRegister(response.body()!!.getObjData().getStatus())

                                    if (TextUtils.isEmpty(_user_id)) {
                                        Log.e("TAGG", "Authenticate empty")
                                        return
                                    } else {
                                        Log.e("TAGG", "Authenticate goto else")
                                    }

                                    if (loginType == LoginType.paintology) {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(this@SignupActivity) { task ->
                                                if (task.isSuccessful) {
                                                    constants.putString(
                                                        constants.Username,
                                                        username,
                                                        this@SignupActivity
                                                    )
                                                    constants.putString(
                                                        constants.Password,
                                                        password,
                                                        this@SignupActivity
                                                    )
                                                    constants.putString(
                                                        constants.Email,
                                                        email,
                                                        this@SignupActivity
                                                    )
                                                    constants.putString(
                                                        constants.LoginInPaintology,
                                                        "true",
                                                        this@SignupActivity
                                                    )

                                                    updateToken()

                                                    val user = auth.currentUser
                                                    updateUserList(
                                                        user!!,
                                                        username,
                                                        email,
                                                        AppUtils.getLoginType(loginType.ordinal)
                                                    )

                                                    if (KGlobal.isInternetAvailable(this@SignupActivity) && !TextUtils.isEmpty(
                                                            _user_id
                                                        )
                                                    ) {
                                                        startService(
                                                            Intent(
                                                                this@SignupActivity,
                                                                SendDeviceToken::class.java
                                                            )
                                                        )
                                                    }

                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w(
                                                        TAG,
                                                        "signUpWithEmail:failure",
                                                        task.exception
                                                    )
                                                    Toast.makeText(
                                                        baseContext,
                                                        "Firebase Authentication failed: " + task.exception,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    resetData()
//                                                updateUI(null, null, -1)
                                                    hideProgressBar()
                                                    val fromPaintor =
                                                        intent.getBooleanExtra("fromPaintor", false)
                                                    if (fromPaintor) {
                                                        setResult(RESULT_OK)
                                                    }
//                                                finish()
                                                }


//                                LoginInPaintology = constants.getString(
//                                    constants.LoginInPaintology,
//                                    this@SignupActivity
//                                )
                                                if (!TextUtils.isEmpty(
                                                        response.body()!!.getObjData().getStatus()
                                                    )
                                                ) {
                                                    Log.e(
                                                        "TAGGG",
                                                        "Login Status " + response.body()!!
                                                            .getObjData().getStatus()
                                                    )
                                                    if (response.body()!!.getObjData().getStatus()
                                                            .toLowerCase()
                                                            .contains("user already exists")
                                                    ) {
                                                        if (BuildConfig.DEBUG) {
                                                            Toast.makeText(
                                                                this@SignupActivity,
                                                                constants.PaintologyLoginSuccess,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        FirebaseUtils.logEvents(
                                                            this@SignupActivity,
                                                            constants.PaintologyLoginSuccess
                                                        )
                                                    } else if (response.body()!!.getObjData()
                                                            .getStatus()
                                                            .toLowerCase().contains("user inserted")
                                                    ) {
                                                        if (BuildConfig.DEBUG) {
                                                            Toast.makeText(
                                                                this@SignupActivity,
                                                                constants.PaintologyRegistration,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        FirebaseUtils.logEvents(
                                                            this@SignupActivity,
                                                            constants.PaintologyRegistration
                                                        )
                                                    }
                                                }
                                                hideProgressBar()
                                                val fromPaintor =
                                                    intent.getBooleanExtra("fromPaintor", false)
                                                if (fromPaintor) {
                                                    setResult(RESULT_OK)
                                                }
                                                finish()
                                            }

                                    } else if (loginType == LoginType.facebook) {
                                        if (!TextUtils.isEmpty(
                                                response.body()!!.getObjData().getStatus()
                                            )
                                        ) {
                                            Log.e(
                                                "TAGGG",
                                                "Login Status " + response.body()!!.getObjData()
                                                    .getStatus()
                                            )
                                            if (response.body()!!.getObjData().getStatus()
                                                    .contains("user inserted")
                                            ) {
                                                if (BuildConfig.DEBUG) {
                                                    Toast.makeText(
                                                        this@SignupActivity,
                                                        constants.FacebookRegister,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                FirebaseUtils.logEvents(
                                                    this@SignupActivity,
                                                    constants.FacebookRegister
                                                )

                                                constants.putString(
                                                    constants.Username,
                                                    username,
                                                    this@SignupActivity
                                                )
                                                constants.putString(
                                                    constants.Password,
                                                    password,
                                                    this@SignupActivity
                                                )
                                                constants.putString(
                                                    constants.Email,
                                                    email,
                                                    this@SignupActivity
                                                )
                                                constants.putString(
                                                    constants.LoginInPaintology,
                                                    "false",
                                                    this@SignupActivity
                                                )

                                                updateToken()

                                                val user = auth.currentUser
                                                updateUserList(
                                                    user!!,
                                                    username,
                                                    email,
                                                    AppUtils.getLoginType(loginType.ordinal)
                                                )

                                                if (KGlobal.isInternetAvailable(this@SignupActivity) && !TextUtils.isEmpty(
                                                        _user_id
                                                    )
                                                ) {
                                                    startService(
                                                        Intent(
                                                            this@SignupActivity,
                                                            SendDeviceToken::class.java
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        hideProgressBar()
                                        val fromPaintor =
                                            intent.getBooleanExtra("fromPaintor", false)
                                        if (fromPaintor) {
                                            setResult(RESULT_OK)
                                        }
                                        finish()
                                    } else if (loginType == LoginType.google) {
                                        if (!TextUtils.isEmpty(
                                                response.body()!!.getObjData().getStatus()
                                            )
                                        ) {
                                            Log.e(
                                                "TAGGG",
                                                "Login Status " + response.body()!!.getObjData()
                                                    .getStatus()
                                            )
                                            if (response.body()!!.getObjData().getStatus()
                                                    .contains("user inserted")
                                            ) {
                                                if (BuildConfig.DEBUG) {
                                                    Toast.makeText(
                                                        this@SignupActivity,
                                                        constants.GoogleRegistration,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                FirebaseUtils.logEvents(
                                                    this@SignupActivity,
                                                    constants.GoogleRegistration
                                                )

                                                constants.putString(
                                                    constants.Username,
                                                    username,
                                                    this@SignupActivity
                                                )
                                                constants.putString(
                                                    constants.Password,
                                                    password,
                                                    this@SignupActivity
                                                )
                                                constants.putString(
                                                    constants.Email,
                                                    email,
                                                    this@SignupActivity
                                                )
                                                constants.putString(
                                                    constants.LoginInPaintology,
                                                    "false",
                                                    this@SignupActivity
                                                )

                                                updateToken()

                                                val user = auth.currentUser
                                                updateUserList(
                                                    user!!,
                                                    username,
                                                    email,
                                                    AppUtils.getLoginType(loginType.ordinal)
                                                )

                                                if (KGlobal.isInternetAvailable(this@SignupActivity) && !TextUtils.isEmpty(
                                                        _user_id
                                                    )
                                                ) {
                                                    startService(
                                                        Intent(
                                                            this@SignupActivity,
                                                            SendDeviceToken::class.java
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        hideProgressBar()
                                        val fromPaintor =
                                            intent.getBooleanExtra("fromPaintor", false)
                                        if (fromPaintor) {
                                            setResult(RESULT_OK)
                                        }
                                        finish()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@SignupActivity,
                                        "Failed: " + response.body()!!.getObjData().errorMsg,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    Log.e("TAG", "add User goto else data null")
                                    hideProgressBar()
                                }
                        }
                    } else {
                        if (loginType == LoginType.facebook) {
                            signOut()
                        } else if (loginType == LoginType.google) {
                            googleSignOut()
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(
                                    this@SignupActivity,
                                    constants.PaintologyLoginFailed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            FirebaseUtils.logEvents(
                                this@SignupActivity,
                                constants.PaintologyLoginFailed
                            )
                        }
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                this@SignupActivity,
                                constants.event_failed_to_adduser,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseUtils.logEvents(
                            this@SignupActivity,
                            constants.event_failed_to_adduser
                        )

                        hideProgressBar()
                    }
                    //                    new SaveTask(model).execute();
                }

                override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                    Log.e("TAGG", "add user in failure " + t.message, t)
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@SignupActivity,
                            "Ex: " + t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    hideProgressBar()
                }
            })
        } catch (e: Exception) {
            Log.e("TAGGG", "add user in Exception " + e.message, e)
            hideProgressBar()
        }
    }

    private fun updateUserList(
        user: FirebaseUser,
        username: String,
        email: String,
        loginType: String
    ) {

        val _map = java.util.HashMap<String, String>()

        try {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val currentDateandTime = sdf.format(Date())
            _map["create_date"] = currentDateandTime
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        _map["login_type"] = loginType
        _map["user_id"] = constants.getString(constants.UserId, this@SignupActivity)
        _map["key"] = user.uid
        _map["user_email"] = email
        _map["user_name"] = username
        _map["is_online"] = "true"
        _map["is_typing"] = "false"
        _map["profile_pic"] =
            constants.getString(constants.ProfilePicsUrl, this@SignupActivity)
        FirebaseDatabase.getInstance()
            .getReference(constants.firebase_user_list)
            .child(constants.getString(constants.UserId, this@SignupActivity))
            .setValue(_map).addOnCompleteListener {


            }
    }

    private fun updateToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                AppUtils.updateToken(token)

                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(this@SignupActivity, msg, Toast.LENGTH_SHORT).show()
            })
    }

    private fun resetData() {
        constants.putString(constants.Username, "", this@SignupActivity)
        constants.putString(constants.Password, "", this@SignupActivity)
        constants.putString(constants.Email, "", this@SignupActivity)
    }

    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun googleSignOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(
            this@SignupActivity
        ) {
            resetData()
        }
    }

    private fun revokeGoogleAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        mGoogleSignInClient?.revokeAccess()?.addOnCompleteListener(
            this@SignupActivity
        ) {
            resetData()
        }
    }

    private fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()
        resetData()
    }

    private fun hideProgressBar() {
        binding.progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progress.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG: String = "SignupActivity"
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"
    }
}