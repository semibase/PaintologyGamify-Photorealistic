package com.paintology.lite.photorealistic.drawing.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.paintology.lite.photorealistic.drawing.Enums.LoginType
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.onboarding.utils.Events
import com.paintology.lite.photorealistic.drawing.util.*
import java.text.SimpleDateFormat
import java.util.Date

class GuestUtils {

    val constants = StringConstants()
    fun Activity.login(countryCode: String, action: (Boolean,String) -> Unit) {

        val auth = FirebaseAuth.getInstance()
        auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInAnonymously:success")
                val id = AppUtils.generateRandomDigits(9)
                val userId = "Guest_$id"

                constants.putString(
                    constants.UserId,
                    FirebaseAuth.getInstance().currentUser!!.uid.toString(),
                    this
                )

                constants.putBoolean(
                    constants.IsGuestUser,
                    true,
                    this
                );

                registerUser(
                    LoginType.guest.toString(),
                    userId,
                    userId,
                    "",
                    countryCode,
                    action
                )

            } else {
                action.invoke(false,"Authentication failed.")
            }
        }
    }

    private fun Context.registerUser(
        authProvider: String,
        email: String,
        name: String,
        avatar: String,
        country: String,
        action: (Boolean,String) -> Unit
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
                    this
                )

                constants.putString(StringConstants.isVideoShown, "", this)

                MyApplication.get_realTimeDbUtils(this).setCurrentUser();
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    FireUtils.updateToken(this, it)
                }

                val result = task.result as HashMap<String, Any>
                if (result.containsKey("name")) {
                    val userName = result["name"].toString() ?: ""
                    constants.putString(constants.Username, userName, this)
                }

                if (result.containsKey("country")) {
                    val userCountry = result["country"].toString() ?: ""
                    constants.putString(constants.UserCountry, userCountry, this)
                }

                FirebaseUtils.logEvents(this, Events.EVENT_LOGIN_GUEST)

                if (authProvider != LoginType.guest.toString()) {
                    addData(result,action)
                } else {
                    FirebaseFirestoreApi.sendFlag()
                    AppUtils.savePurchasedProducts(
                        listOf<String>()
                    )
                    AppUtils.savePurchasedBrushes(listOf<String>())


                    FirebaseUtils.SetInstance(this)
                    FirebaseUtils.logEvents(this, Events.EVENT_HOME)
                    FirebaseFirestoreApi.claimActivityPointsWithId(
                        StringConstants.default_bonus_point,
                        null
                    )
                    action.invoke(true,"")
                }
            } else {
                action.invoke(false,"Authentication failed. Please check your credentials.")
                FireUtils.hideProgressDialog()
                val e = task.exception
                Log.e("TAG", "Error: ", e)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun Context.addData(data: HashMap<String, Any>, action: (Boolean,String) -> Unit) {

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
        _map["user_id"] = constants.getString(constants.UserId, this)
        _map["key"] = constants.getString(constants.UserId, this)
        _map["is_online"] = "true"
        _map["is_typing"] = "false"

        constants.putString(constants.Username, _map["user_name"], this)

        FirebaseDatabase.getInstance().getReference(constants.firebase_user_list)
            .child(constants.getString(constants.UserId, this))
            .setValue(_map).addOnCompleteListener {
                FirebaseUtils.logEvents(this, Events.EVENT_HOME)
                action.invoke(true,"")
            }
    }

}