package com.paintology.lite.photorealistic.drawing.util

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.paintology.lite.photorealistic.drawing.Activity.shared_pref.SharedPref
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.bus.UserLoginUpdateEvent
import com.paintology.lite.photorealistic.drawing.databinding.DialogLogoutBinding
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity
import org.greenrobot.eventbus.EventBus

class LoginUtils(
    val context: Activity,
    val mGoogleSignInClient: GoogleSignInClient? = null,
    val sharedPref: SharedPref
) {

    var constants: StringConstants = StringConstants()

    fun confirmLogout(isFromFB: Boolean, isFromGoogle: Boolean) {

        val dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)

        dialogBinding.btnSeePost.text = "Yes, Log Out"
        dialogBinding.btnOk.text = "Cancel"
        dialogBinding.tvMessage.text = "Are you sure you want to logout?"


        dialogBinding.btnSeePost.setOnClickListener {
            val authStateListener =
                AuthStateListener { firebaseAuth ->
                    if (firebaseAuth.currentUser == null) {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        context.finish()
                    } else {

                    }
                }

            val firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.addAuthStateListener(authStateListener);
            if (isFromFB) {
                LoginManager.getInstance().logOut()
                // clear shared prefs
                clearedData()
            } else if (isFromGoogle) {
                mGoogleSignInClient?.signOut()?.addOnSuccessListener { }
                // clear shared prefs
                clearedData()
            } else if (constants.getBoolean(constants.IsGuestUser, context)) {
                clearedData()
            }

            EventBus.getDefault().post(UserLoginUpdateEvent())

            showToast(context, constants.logout_success)

            FirebaseUtils.logEvents(context, constants.logout_success)
            dialog.setOnDismissListener {
                firebaseAuth.signOut()
            }
            dialog.cancel()
        }

        dialogBinding.btnOk.setOnClickListener {
            dialog.cancel()
            if (BuildConfig.DEBUG) {
                showToast(context, constants.logout_cancel)
            }
            FirebaseUtils.logEvents(context, constants.logout_cancel)
        }

        dialogBinding.imgCross.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun clearedData() {

        try {
            val reference = FirebaseDatabase.getInstance().reference
            val applesQuery =
                reference.child("Tokens").orderByChild("token")
                    .equalTo(FirebaseInstanceId.getInstance().token)

            MyApplication.get_realTimeDbUtils(context)
                .setOffline(constants.getString(constants.UserId, context))
            MyApplication.get_realTimeDbUtils(context).firebaseAuth.signOut()
            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.removeValue()
                        constants.putString(constants.fireBaseToken, "", context)
                        Log.e("TAGG", "applesQuery onRemove")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAGG", "applesQuery onCancelled", databaseError.toException())
                }
            })

        } catch (e: Exception) {
            Log.e("TAG", "Exception at delete token " + e.message, e)
        }

        sharedPref.clearAll()

        constants.putString(constants.LoginInPaintology, "", context)
        constants.putString(constants.Email, "", context)
        constants.putString(constants.Password, "", context)
        constants.putString(constants.Username, "", context)
        constants.putString(constants.ProfilePicsUrl, "", context)
        constants.putString(constants.UserGender, "", context)
        constants.putString(constants.UserId, "", context)

        constants.putString(constants.userAbilityFromPref, "", context)
        constants.putString(constants.userArtMedFromPref, "", context)
        constants.putString(constants.userArtFavFromPref, "", context)

       // AppUtils.setLoggedIn(false)
    }
}