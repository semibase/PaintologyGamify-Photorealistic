package com.paintology.lite.photorealistic.drawing.Activity.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.util.transforms.CircleTransform
import com.squareup.okhttp.MediaType
import com.squareup.picasso.Picasso


@SuppressLint("SetTextI18n")
fun getUserProfileData(
    userId: String,
    profilePic: ImageView? = null,
    flag: ImageView? = null,
    tvPoints: TextView? = null
) {

    if (userId == "") {
        return
    }

    try {
        FirebaseFirestoreApi.getProfile(userId)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data as Map<String, Any>

                    if (flag != null) {
                        val flagResourceId =
                            Lists.getCountryFlagResource(data["country"] as? String ?: "")
                        flag.setImageResource(flagResourceId)
                    }

                    if (tvPoints != null) {
                        tvPoints.text = (data["points"] as? Long ?: 0).toString()+" Pts"
                    }

                    if (profilePic != null) {
                        if ((data["auth_provider"] as? String ?: "guest") == "guest") {
                            Picasso.get().load(
                                Uri.parse(data["avatar"] as? String ?: "")
                            )
                                .transform(CircleTransform())
                                .placeholder(R.drawable.img_default_avatar)
                                .error(R.drawable.img_default_avatar)
                                .into(profilePic)
                        } else {
                            Picasso.get().load(Uri.parse(data["avatar"] as? String ?: ""))
                                .transform(CircleTransform())
                                .placeholder(R.drawable.paintology_logo)
                                .error(R.drawable.paintology_logo)
                                .into(profilePic)
                        }
                    }

                } else {
                    Picasso.get().load(R.drawable.img_default_avatar)
                        .transform(CircleTransform())
                        .placeholder(R.drawable.img_default_avatar)
                        .error(R.drawable.img_default_avatar)
                        .into(profilePic)
                }
            }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.getUserOnlineStatus(userId: String) {

    try {
        val database = FirebaseDatabase.getInstance()
        val presenceRef = database.getReference("users")
            .child(userId)
            .child("status")

        presenceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java) ?: "offline"
                if (status == "online") {
                    setImageResource(R.drawable.online)
                } else {
                    setImageResource(R.drawable.offline)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                setImageResource(R.drawable.offline)
                Log.e("TAGRR", "Failed to read user status", error.toException())
            }
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

private fun ImageView.getUserCountry(userId: String) {

    val database = FirebaseDatabase.getInstance()
    val presenceRef = database.getReference("users")
        .child(userId)
        .child("status")

    presenceRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val status = snapshot.getValue(String::class.java) ?: "offline"

            if (status == "online") {
                setImageResource(R.drawable.online)
            } else {
                setImageResource(R.drawable.offline)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("TAGRR", "Failed to read user status", error.toException())
        }
    })
}

fun String.toMediaTypeOrNull(): MediaType? {
    return try {
        MediaType.parse(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.isNetworkConnected(): Boolean {
    val mgr =
        this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = mgr.activeNetworkInfo
    return netInfo != null && netInfo.isConnected && netInfo.isAvailable
}

// Function to convert DocumentSnapshot to your data class
fun <T> DocumentSnapshot.convertToObject(dataClass: Class<T>): T? {
    val gson = Gson()
    val json = gson.toJson(data)
    return gson.fromJson(json, dataClass)
}

fun <T> Context.openActivity(it: Class<T>) {
    val intent = Intent(this, it)
    startActivity(intent)
}

fun <T> Context.startDrawingActivity(drawing: NewDrawing, it: Class<T>, showLevelData: Boolean) {
    val intent = Intent(this, it)
    intent.putExtra("drawing_model", drawing)
    if (showLevelData) {
        intent.putExtra("isShowingUserLevelData", true)
    }
    startActivity(intent)
}

fun <T> Context.startDrawingActivity(
    drawing: NewDrawing,
    it: Class<T>,
    showLevelData: Boolean,
    country: String
) {
    val intent = Intent(this, it)
    intent.putExtra("drawing_model", drawing)
    intent.putExtra("country", country)
    if (showLevelData) {
        intent.putExtra("isShowingUserLevelData", true)
    }
    startActivity(intent)
}

fun <T> Context.startNewDrawingActivity(drawing: NewDrawing, it: Class<T>, showLevelData: Boolean) {
    val intent = Intent(this, it)
    intent.putExtra("drawing_model", drawing)
    if (showLevelData) {
        intent.putExtra("isShowingUserLevelData", true)
    }
    startActivity(intent)
}

fun <T> Context.startDrawingActivity(drawing: NewDrawing, it: Class<T>) {
    val intent = Intent(this, it)
    intent.putExtra("drawing_model", drawing)
    startActivity(intent)
}

fun <T> Context.startDrawingActivityWithRank(
    drawing: NewDrawing,
    it: Class<T>,
    showLevelData: Boolean,
    drawingType: String,
    userLevelFromBottom: String,
) {
    val intent = Intent(this, it)
    intent.putExtra("drawing_model", drawing)
    intent.putExtra("userLevelFromBottom", userLevelFromBottom)
    if (showLevelData) {
        intent.putExtra("isShowingUserLevelData", true)
    }
    intent.putExtra("drawingType", drawingType)
    startActivity(intent)
}

fun <T> Context.openActivityWithExtras(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun View.onSingleClick1(debounceTime: Long = 2000, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.onSingleClick(debounceTime: Long = 1500, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Context.showToast(message: String) {
    if (BuildConfig.DEBUG) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.showToastRelease(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.showPopupMenu(menuResId: Int, onMenuItemClicked: (menuItem: MenuItem) -> Unit) {
    PopupMenu(context, this).apply {
        inflate(menuResId)
        setOnMenuItemClickListener {
            onMenuItemClicked(it)
            true
        }
        show()
    }
}

fun View.showPopupMenu1(
    menuResId: Int,
    changeMenuItemTitle: Boolean,
    onMenuItemClicked: (menuItem: MenuItem) -> Unit
) {
    PopupMenu(context, this).apply {
        inflate(menuResId)

        // Change the title of the menu item if needed
        if (changeMenuItemTitle) {
            menu.findItem(R.id.doTutorialItem).title = "open drawing"
        }

        setOnMenuItemClickListener {
            onMenuItemClicked(it)
            true
        }
        show()
    }
}


fun getDrawableId(context: Context, nameString: String): Int {

    return context.resources.getIdentifier(
        nameString,
        "drawable",
        context.applicationContext.packageName
    )
}
