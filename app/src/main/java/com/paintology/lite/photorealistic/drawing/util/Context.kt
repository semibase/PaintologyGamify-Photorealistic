package com.paintology.lite.photorealistic.drawing.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.Art
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.Preferences
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.Progress
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.Social
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.Statistic
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserProfile
import com.paintology.lite.photorealistic.drawing.Activity.notifications.checkNotify
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Adapter.BannerAdapter
import com.paintology.lite.photorealistic.drawing.Adapter.StoreAdapter
import com.paintology.lite.photorealistic.drawing.Model.AppBanner
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.getAppBanners
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.getStoreProducts
import com.paintology.lite.photorealistic.drawing.databinding.ActivityEditTutorialBinding
import com.paintology.lite.photorealistic.drawing.databinding.DialogMainStoreBinding
import com.paintology.lite.photorealistic.drawing.databinding.ViewEnlargeBinding
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date


fun showToast(context: Context, message: String) {
    try {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun sendUserEvent(eventName: String) {
    if (MyApplication.getInstance().applicationContext != null) {
        /*if (BuildConfig.DEBUG) {
            Toast.makeText(
                MyApplication.getInstance().applicationContext,
                eventName,
                Toast.LENGTH_SHORT
            ).show()
        }*/
        FirebaseUtils.logEvents(MyApplication.getInstance().applicationContext, eventName)
    }
}

fun Context.sendUserEventWithParam(eventName: String, params: Bundle) {
   /* Toast.makeText(
        MyApplication.getInstance().applicationContext,
        eventName,
        Toast.LENGTH_SHORT
    ).show()*/
    FirebaseAnalytics.getInstance(this)
        .logEvent(eventName,params)
}


@SuppressLint("SimpleDateFormat")
fun getLastUpdatedTime(snapshot: DataSnapshot): Long {

    try {
        val dateString = snapshot.child("Msg").children.last().child("date").value.toString()
        val date: Date? = SimpleDateFormat("yyyy-MM-dd hh:mm aa").parse(dateString)
        if (date != null) {
            return date.time
        } else return 0
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0
}

fun parseUserProfile(data: Map<String, Any>): UserProfile {
    val statisticMap = data["statistic"] as? Map<String, Any> ?: emptyMap()
    val preferencesMap = data["preferences"] as? Map<String, Any> ?: emptyMap()
    val artMap = preferencesMap["art"] as? Map<String, Any> ?: emptyMap()
    val socialMap = data["social"] as? Map<String, Any> ?: emptyMap()
    val progressMap = data["progress"] as? Map<String, Any> ?: emptyMap()

    val statistic = Statistic(
        totalPosts = (statisticMap["total_posts"] as? Int) ?: 0,
        totalFollowers = (statisticMap["total_followers"] as? Int) ?: 0,
        totalFollowing = (statisticMap["total_following"] as? Int) ?: 0
    )

    val art = Art(
        favorites = artMap["favorites"] as? List<HashMap<*, *>> ?: emptyList(),
        ability = artMap["ability"] as? String ?: "",
        mediums = artMap["mediums"] as? List<HashMap<*, *>> ?: emptyList()
    )

    val preferences = Preferences(art = art)

    val social = Social(
        youtube = socialMap["youtube"] as? String ?: "",
        other = socialMap["other"] as? String ?: "",
        twitter = socialMap["twitter"] as? String ?: "",
        website = socialMap["website"] as? String ?: "",
        quora = socialMap["quora"] as? String ?: "",
        tiktok = socialMap["tiktok"] as? String ?: "",
        facebook = socialMap["facebook"] as? String ?: "",
        instagram = socialMap["instagram"] as? String ?: "",
        pinterest = socialMap["pinterest"] as? String ?: "",
        linkedin = socialMap["linkedin"] as? String ?: "",
        paintology = socialMap["paintology"] as? String ?: ""
    )

    val progress = Progress(
        gallery = progressMap["gallery"] as? Int ?: 0,
        community = progressMap["community"] as? Int ?: 0,
        tutorial = progressMap["tutorial"] as? Int ?: 0,
        drawing = progressMap["drawing"] as? Int ?: 0,
        resource = progressMap["resource"] as? Int ?: 0,
        painting = progressMap["painting"] as? Int ?: 0,
        big_points = progressMap["big_points"] as? Int ?: 0,
    )
    return UserProfile(
        country = data["country"] as? String ?: "",
        statistic = statistic,
        preferences = preferences,
        social = social,
        level = data["level"] as? String ?: "",
        username = data["username"] as? String ?: "",
        name = data["name"] as? String ?: "",
        bio = data["bio"] as? String ?: "",
        features = data["features"] as? List<String> ?: listOf(),
        brushes = data["brushes"] as? List<String> ?: listOf(),
        createdAt = data["created_at"] as? String ?: "",
        avatar = data["avatar"] as? String ?: "",
        points = (data["points"] as? Int) ?: 0,
        auth_provider = data["auth_provider"] as? String ?: "",
        age = data["age"] as? String ?: "",
        email = data["email"] as? String ?: "",
        gender = data["gender"] as? String ?: "",
        progress = progress
    )
}


interface onStoreFetchListener {
    fun onFetched(product: String)
}

fun getStoreProduct(productId: String, onStoreFetchListener: onStoreFetchListener) {
    getStoreProducts(1, "data.id:=$productId")
        .addOnCompleteListener { task: Task<HttpsCallableResult> ->
            if (task.isSuccessful) {
                FireUtils.hideProgressDialog()
                val data = task.getResult().getData() as HashMap<*, *>
                val mItemList = data.get("data") as List<*>
                if (mItemList.isNotEmpty()) {
                    val item = mItemList[0] as HashMap<*, *>
                    val mProduct = item["data"] as HashMap<*, *>
                    if (mProduct.containsKey("id")) {
                        AppUtils.getStoreProductsCommon()[mProduct["id"].toString()] =
                            Gson().toJson(item)
                        onStoreFetchListener.onFetched(Gson().toJson(item))
                        return@addOnCompleteListener
                    }
                }
            }
            onStoreFetchListener.onFetched("")
        }
}

fun Context.setSharedNo()
{
    val lEditor1 = getSharedPreferences("brush", 0).edit()
    lEditor1.putString("pref-saved", "no")
    lEditor1.commit()
}

fun Context.setSharedYes()
{
    val lEditor1 = getSharedPreferences("brush", 0).edit()
    lEditor1.putString("pref-saved", "yes")
    lEditor1.commit()
}

fun Activity.showBanners(editTutorialBinding: ActivityEditTutorialBinding) {
    if (AppUtils.getAppBanners().any { it.status == "active" }) {
        showAppBanners(editTutorialBinding)
    } else {
        getAppBanners().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.documents.size > 0) {
                    val array = mutableListOf<AppBanner>()
                    task.result.documents.forEach { item ->
                        if (item.data != null) {
                            val model =
                                Gson().fromJson(item.data!!.entries.joinToString(", ", "{", "}") {
                                    "\"${it.key}\":\"${it.value}\""
                                }, AppBanner::class.java)
                            if (model != null) {
                                model.banner_id = item.id
                                array.add(model)
                            }
                        }
                    }
                    if (array.any { it.status == "active" }) {
                        AppUtils.saveAppBanners(array.filter { it.status == "active" })
                        showAppBanners(editTutorialBinding)
                    }
                }
            }
        }
    }
}

fun Context.gotoUrl(url: String?) {
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

private fun ViewPager2.onInfinitePageChangeCallback(listSize: Int) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)

            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                when (currentItem) {
                    listSize - 1 -> setCurrentItem(1, false)
                    0 -> setCurrentItem(listSize - 2, false)
                }
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            if (position != 0 && position != listSize - 1) {
                // pageIndicatorView.setSelected(position-1)
            }
        }
    })
}

fun Activity.showAppBanners(editTutorialBinding: ActivityEditTutorialBinding) {
    try {
        val dialog = Dialog(this)
        val adapter =
            BannerAdapter(
                this,
                AppUtils.getAppBanners().sortedBy { it.sorting_number },
                object : BannerAdapter.onItemClickListener {

                    override fun onDialogClose(position: Int, banner: AppBanner) {
                        val bundle = Bundle()
                        bundle.putString("banner_id", banner.banner_id)
                        bundle.putString("title", banner.title)
                        sendUserEventWithParam(StringConstants.app_banner_close, bundle)
                        dialog.dismiss()
                    }

                    override fun onItemClick(position: Int, banner: AppBanner) {
                        if (banner.target_id == "close") {
                            dialog.dismiss()
                        } else {
                            val bundle = Bundle()
                            bundle.putString("banner_id", banner.banner_id)
                            bundle.putString("title", banner.title)
                            sendUserEventWithParam(StringConstants.app_banner_open, bundle)
                            var target = banner.target_id
                            if (banner.target_name == "country") {
                                target = StringConstants.constants.getString(
                                    StringConstants.constants.UserCountry,
                                    this@showAppBanners
                                )
                            }
                            checkNotify(
                                banner.target_type,
                                banner.target_name,
                                target
                            )
                        }
                    }
                })
        val binding: DialogMainStoreBinding =
            DialogMainStoreBinding.inflate(LayoutInflater.from(this), null, false)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.setPreviewBothSide(
            com.intuit.sdp.R.dimen._25sdp,
            com.intuit.sdp.R.dimen._25sdp
        )
        binding.viewPager2.setCurrentItem(1, false)
        binding.viewPager2.onInfinitePageChangeCallback(AppUtils.getAppBanners().size + 2)
        dialog.setContentView(binding.getRoot())
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setOnDismissListener {
            editTutorialBinding.includedHeader.navBanners.visibility = View.VISIBLE
            editTutorialBinding.includedHeader.navBanners.onSingleClick {
                try {
                    editTutorialBinding.drawerLayout.closeDrawer(GravityCompat.START)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showBanners(editTutorialBinding)
            }
        }
        dialog.setOnShowListener {
            editTutorialBinding.includedHeader.navBanners.visibility = View.GONE
        }
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}


fun getStoreProducts(context: Context, isShow: Boolean = false, page: Int = 1) {
    getStoreProducts(page)
        .addOnCompleteListener { task: Task<HttpsCallableResult> ->
            if (task.isSuccessful) {
                val data = task.getResult().getData() as HashMap<*, *>
                val mItemList = data.get("data") as List<*>
                for (mItem in mItemList) {
                    val item = mItem as HashMap<*, *>
                    val mProduct = item["data"] as HashMap<*, *>
                    if (mProduct.containsKey("id")) {
                        AppUtils.getStoreProducts()[mProduct.get("id").toString()] =
                            Gson().toJson(item)
                    }
                }
                if (mItemList.isNotEmpty()) {
                    val pp = page + 1;
                    getStoreProducts(context, isShow, pp)
                } else {
                    if (AppUtils.getStoreProducts().size > 0 && isShow) {
                        FireUtils.hideProgressDialog()
                        showStoreDialog(context)
                    }
                }
            } else {
                FireUtils.hideProgressDialog()
                Log.e("TAGRR", "failed " + task.exception!!.message)
            }
        }
}

fun showEnlargeImage(context: Context, url: String) {
    try {
        val dialog = Dialog(context, R.style.NormalDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        val binding: ViewEnlargeBinding =
            ViewEnlargeBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            )
        Picasso.get().load(
            Uri.parse(url)).into(binding.ivEnlargeImage)
        binding.ivBackArrow.onSingleClick {
            binding.ivBackArrow.visibility = View.GONE
            dialog.dismiss()
        }
        binding.fmImage.onSingleClick {
            binding.ivBackArrow.visibility = View.GONE
            dialog.dismiss()
        }
        binding.ivEnlargeImage.onSingleClick {
            binding.ivBackArrow.visibility = View.GONE
            dialog.dismiss()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.ivBackArrow.visibility = View.VISIBLE
        }, 500)
        dialog.setContentView(binding.getRoot())
        dialog.setOnDismissListener {
            binding.ivBackArrow.visibility = View.GONE
        }
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun showStoreDialog(context: Context) {
    if (AppUtils.getStoreProducts().size > 0) {
        try {
            val adapter =
                StoreAdapter(context, ArrayList<String>(AppUtils.getStoreProducts().values))
            val dialog = Dialog(context)
            val binding: DialogMainStoreBinding =
                DialogMainStoreBinding.inflate(LayoutInflater.from(context), null, false)
            binding.viewPager2.adapter = adapter
            binding.viewPager2.setPreviewBothSide(
                com.intuit.sdp.R.dimen._25sdp,
                com.intuit.sdp.R.dimen._25sdp
            )
            dialog.setContentView(binding.getRoot())
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    } else {
        FireUtils.showProgressDialog(context, context.resources.getString(R.string.ss_loading_please_wait))
        getStoreProducts(context, true, 1)
    }
}

class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
    RecyclerView.ItemDecoration() {
    private val horizontalMarginInPx: Int =
        context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }
}

fun ViewPager2.setPreviewBothSide(
    @DimenRes nextItemVisibleSize: Int,
    @DimenRes currentItemHorizontalMargin: Int
) {
    this.offscreenPageLimit = 1
    val nextItemVisiblePx = resources.getDimension(nextItemVisibleSize)
    val currentItemHorizontalMarginPx = resources.getDimension(currentItemHorizontalMargin)
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
        page.translationX = -pageTranslationX * position
        //   page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
    }
    this.setPageTransformer(pageTransformer)

    val itemDecoration = HorizontalMarginItemDecoration(
        context,
        currentItemHorizontalMargin
    )
    this.addItemDecoration(itemDecoration)
}
