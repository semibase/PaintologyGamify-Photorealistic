package com.paintology.lite.photorealistic.drawing.videoguide

import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.DashboardScreen.NewSubCategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.TutorialDetail_Activity
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel.postData
import com.paintology.lite.photorealistic.drawing.Model.Overlaid
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData
import com.paintology.lite.photorealistic.drawing.Model.sizes
import com.paintology.lite.photorealistic.drawing.Model.text_files
import com.paintology.lite.photorealistic.drawing.Model.trace_image
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.getGuideRating
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.setGuideRating
import com.paintology.lite.photorealistic.drawing.databinding.ActivityVideoGuideBinding
import com.paintology.lite.photorealistic.drawing.helpers.ShareBroadcast
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo
import com.paintology.lite.photorealistic.drawing.room.AppDatabase
import com.paintology.lite.photorealistic.drawing.room.daos.ColorSwatchDao
import com.paintology.lite.photorealistic.drawing.room.entities.ColorSwatchEntity
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class VideoGuideActivity : AppCompatActivity(),
    VideoGuideAdapter.ItemClickListener {

    private lateinit var binding: ActivityVideoGuideBinding
    private var apiInterface: ApiInterface? = null
    private var newSubCategoryAdapter: VideoGuideAdapter? = null
    private lateinit var progressDialog: ProgressDialog
    lateinit var _object: PostDetailModel
    private var tutorial_type: Tutorial_Type? = null
    private lateinit var db: AppDatabase
    var defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        apiInterface = ApiClient.getClient_1().create(ApiInterface::class.java)

        // database
        db = MyApplication.getDb()

        getVideoGuideDataFromAPI()

    }

    private fun getVideoGuideDataFromAPI() {
        val call: Call<GetCategoryPostModel> =
            apiInterface!!.getCategoryPostList(ApiClient.SECRET_KEY, 32.toString())
        val progressDialog = ProgressDialog(this@VideoGuideActivity)

        progressDialog.setTitle(resources.getString(R.string.please_wait))
        progressDialog.setMessage("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        try {
            call.enqueue(object : Callback<GetCategoryPostModel?> {
                override fun onResponse(
                    call: Call<GetCategoryPostModel?>,
                    response: Response<GetCategoryPostModel?>
                ) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing) {
                            progressDialog.dismiss()
                        }
                        if (response.body() != null && response.body()!!.getCode() == 200
                        ) {
                            if (response.body()!!.getPostList().size > 0) {
                                val list = response.body()!!.getPostList()

                                newSubCategoryAdapter = VideoGuideAdapter(
                                    this@VideoGuideActivity,
                                    list,
                                    this@VideoGuideActivity
                                )

                                title = getString(R.string.guide_video_count, list.size)

                                binding.list.adapter = newSubCategoryAdapter
                            }
                        } else {
                            Toast.makeText(
                                this@VideoGuideActivity, response.body()!!
                                    .getResponse(), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e(NewSubCategoryActivity::class.java.name, e.message!!)
                    }
                }

                override fun onFailure(call: Call<GetCategoryPostModel?>, t: Throwable) {
                    if (progressDialog.isShowing && !this@VideoGuideActivity.isDestroyed) progressDialog.dismiss()
                }
            })
        } catch (e: Exception) {
            if (progressDialog.isShowing && !this@VideoGuideActivity.isDestroyed) progressDialog.dismiss()
            Log.e("TAGGG", "Exception at callAPI " + e.message + " " + e.toString())
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

    override fun onSubMenuClick(view: View, item: GetCategoryPostModel.postData, position: Int) {
        // Initializing the popup menu and giving the reference as current context
        val popupMenu = PopupMenu(this, view)

        // Inflating popup menu from popup_menu.xml file
        popupMenu.menuInflater.inflate(R.menu.video_guide_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.action_share -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this,
                            StringConstants.TUTORIAL_MENU_SHARE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this, StringConstants.TUTORIAL_MENU_SHARE)
                    onShareClick(item)
                }

                R.id.action_open_guide -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this,
                            StringConstants.TUTORIAL_MENU_OPEN,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this, StringConstants.TUTORIAL_MENU_OPEN)

                    if (!TextUtils.isEmpty(item.objdata.youtube_link)) {
                        val intent = Intent(
                            this,
                            Play_YotubeVideo::class.java
                        )
                        intent.putExtra("url", item.objdata.youtube_link)
                        intent.putExtra("isVideo", true)
                        startActivity(intent)
                    } else if (!TextUtils.isEmpty(item.objdata.redirect_url)) {
                        try {
                            KGlobal.openInBrowser(
                                this,
                                item.objdata.redirect_url
                            )
                        } catch (e: Exception) {
                            Log.e("VideoGuideAdapter", "" + e.message)
                        }
                    }
                }

                R.id.action_rating -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this,
                            StringConstants.TUTORIAL_MENU_RATING,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FirebaseUtils.logEvents(this, StringConstants.TUTORIAL_MENU_RATING)
                    if (AppUtils.isLoggedIn()) {
                        openRatingDialog(item)
                    } else {
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                    }
                }
            }
            true
        }
        // Showing the popup menu
        popupMenu.show()

    }

    override fun onItemClick(view: View, item: postData, position: Int) {

        val cateId = 32.toString()

        if (item.getObjdata().getRedirect_url() != null && !item.getObjdata().getRedirect_url()
                .isEmpty() && !item.getObjdata().getRedirect_url()
                .equals("canvas", ignoreCase = true)
        ) {

            try {
                val url = item.getObjdata().getRedirect_url()
                KGlobal.openInBrowser(
                    this@VideoGuideActivity,
                    url
                )
            } catch (ex: ActivityNotFoundException) {
            } catch (e: java.lang.Exception) {
            }
            return
        } else if (item.getObjdata().getRedirect_url() != null && !item.getObjdata()
                .getRedirect_url().isEmpty() && item.getObjdata().getRedirect_url()
                .equals("canvas", ignoreCase = true)
        ) {
            getCategoryDetailFromAPI(cateId, item.getObjdata().id)
        } else {
            val intent = Intent(this, TutorialDetail_Activity::class.java)
            intent.putExtra("catID", cateId)
            intent.putExtra("postID", item.getObjdata().id)
            startActivity(intent)
        }
    }

    private fun getCategoryDetailFromAPI(catID: String?, postID: String?) {
        apiInterface = ApiClient.getRetroClient().create(ApiInterface::class.java)
        val call = apiInterface!!.getPostDetail(ApiClient.SECRET_KEY, catID, postID)
        progressDialog = ProgressDialog(this@VideoGuideActivity)
        progressDialog.setTitle(resources.getString(R.string.please_wait))
        progressDialog.setMessage("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        try {
            call.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing() && !this@VideoGuideActivity.isDestroyed()) {
                            progressDialog.dismiss()
                        }
                        if (response != null && response.body() != null) {
                            Log.e("TAGGG", "Response Data " + response.body())
                            parseResponseManually(response.body())
                        } else {
                            if (this@VideoGuideActivity.isDestroyed) { // or call isFinishing() if min sdk version < 17
                                return
                            }
//                                showSnackBar("Failed To Load")
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("TAGG", "Exception " + e.message)
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
//                        showSnackBar("Failed To Retrieve Content!")
                }
            })
        } catch (e: java.lang.Exception) {
            if (progressDialog != null && progressDialog.isShowing() && !this@VideoGuideActivity.isDestroyed()) progressDialog.dismiss()
            Log.e("TAGGG", "Exception at callAPI " + e.message + " " + e.toString())
        }
    }

    private fun openRatingDialog(item: postData) {
        val popDialog = AlertDialog.Builder(this)
        val linearLayout = LinearLayout(this)
        val rating = RatingBar(this)
        val stars = rating.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary),
            PorterDuff.Mode.SRC_ATOP
        )
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.topMargin = 20
        rating.layoutParams = lp
        rating.numStars = 5
        rating.stepSize = 1f

        //add ratingBar to linearLayout
        linearLayout.addView(rating)
        popDialog.setTitle(getString(R.string.rating))

        //add linearLayout to dailog
        popDialog.setView(linearLayout)
        rating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar: RatingBar?, v: Float, b: Boolean ->
                println(
                    "Rated val:$v"
                )
            }

        // Button OK
        popDialog.setPositiveButton(
            R.string.done
        ) { dialog, which ->
            Toast.makeText(
                this,
                rating.progress.toString(),
                Toast.LENGTH_SHORT
            ).show()
            val map =
                HashMap<String, Int>()
            map["rating"] = rating.progress
            setGuideRating(
                item.objdata.id, map,
                FirebaseAuth.getInstance().currentUser!!.uid
            )
                .addOnCompleteListener { task ->
                    Log.e(
                        "RatingResult",
                        "" + task.isSuccessful
                    )
                }
                .addOnFailureListener { e -> Log.e("RatingResult", e.message!!) }
            dialog.dismiss()
        } // Button Cancel
            .setNegativeButton(
                R.string.cancel
            ) { dialog, id -> dialog.cancel() }

        // Fetch previous rating before showing dialog.
        getGuideRating(
            item.objdata.id,
            FirebaseAuth.getInstance().currentUser!!.uid
        ).addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val ds = task.result
                val value = ds["rating"]
                if (value != null) {
                    rating.rating = java.lang.Float.valueOf(value.toString())
                }
            }
            popDialog.create()
            popDialog.show()
        }
            .addOnFailureListener { e: java.lang.Exception? ->
                popDialog.create()
                popDialog.show()
            }
    }

    private fun onShareClick(item: postData) {

        var url = item.objdata.youtube_link
        if (TextUtils.isEmpty(url)) {
            url = item.objdata.redirect_url
        }

        val content = "Check out Paintology Guide. \n$url"
        doSocialShare(content)
    }

    fun doSocialShare(content: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.subcategory_share_subject, "Paintology Guide")
            )

            shareIntent.putExtra(Intent.EXTRA_TEXT, content)
            shareIntent.type = "*/*"
            val receiver = Intent(
                this,
                ShareBroadcast::class.java
            )
            val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    receiver,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    receiver,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            val chooser: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent.createChooser(shareIntent, "Share To", pendingIntent.intentSender)
            } else {
                Intent.createChooser(shareIntent, "Share To")
            }
            startActivity(chooser)
        } catch (e: java.lang.Exception) {
            Log.e(PaintActivity::class.java.name, e.message!!)
        }
    }

    fun parseResponseManually(response: String?) {
        try {
            val mainArray = JSONArray(response)
            if (mainArray.length() > 0) {
                val _lst_video_file = ArrayList<videos_and_files>()
                val objectFirst = mainArray.getJSONObject(0)
                _object = PostDetailModel()
                _object.setID(if (objectFirst.has("ID")) objectFirst.getString("ID") else "")
                _object.setCategoryName(if (objectFirst.has("categoryName")) objectFirst.getString("categoryName") else "")
                _object.setCategoryURL(if (objectFirst.has("categoryURL")) objectFirst.getString("categoryURL") else "")
                _object.setExternal_link(
                    if (objectFirst.has("external_link")) objectFirst.getString(
                        "external_link"
                    ) else ""
                )
                _object.setCanvas_color(if (objectFirst.has("canvas_color")) objectFirst.getString("canvas_color") else "")
                _object.setVisitPage(if (objectFirst.has("VisitPage")) objectFirst.getString("VisitPage") else "")
                _object.setMembership_plan(
                    if (objectFirst.has("membership_plan")) objectFirst.getString(
                        "membership_plan"
                    ) else ""
                )
                _object.setPost_content(if (objectFirst.has("post_content")) objectFirst.getString("post_content") else "")
                _object.setPost_date(if (objectFirst.has("post_date")) objectFirst.getString("post_date") else "")
                _object.setPost_title(if (objectFirst.has("post_title")) objectFirst.getString("post_title") else "")
                _object.setRating(if (objectFirst.has("Rating")) objectFirst.getString("Rating") else "")
                _object.setText_descriptions(
                    if (objectFirst.has("text_descriptions")) objectFirst.getString(
                        "text_descriptions"
                    ) else ""
                )
                _object.setThumb_url(if (objectFirst.has("thumb_url")) objectFirst.getString("thumb_url") else "")
                _object.setYoutube_link_list(
                    if (objectFirst.has("youtube_link")) objectFirst.getString(
                        "youtube_link"
                    ) else ""
                )
                if (objectFirst.has("color_swatch") && !objectFirst.isNull("color_swatch")) {
                    val swatchesArray = objectFirst.getJSONArray("color_swatch")
                    val swatches = ArrayList<ColorSwatch>()
                    if (swatchesArray != null && swatchesArray.length() > 0) {
                        for (i in 0 until swatchesArray.length()) {
                            val swatch = swatchesArray.getJSONObject(i).getString("color_swatch")
                            val colorSwatch = ColorSwatch()
                            colorSwatch.color_swatch = swatch
                            swatches.add(colorSwatch)
                        }
                    }
                    _object.setSwatches(swatches)

                    // save swatches into database
                    val colorSwatchDao: ColorSwatchDao = db.colorSwatchDao()
                    val colorSwatchEntity = ColorSwatchEntity()
                    colorSwatchEntity.postId = _object.getID().toInt()
                    colorSwatchEntity.swatches = Gson().toJson(_object.getSwatches())
                    Executors.newSingleThreadExecutor().execute {
                        colorSwatchDao.insertAll(
                            colorSwatchEntity
                        )
                    }
                }
                if (objectFirst.has("ResizeImage") && objectFirst.getString("ResizeImage") != null) {
                    _object.setResizeImage(objectFirst.getString("ResizeImage"))
                }
                if (objectFirst.has("RelatedPostsData")) {
                    val related_list_json = objectFirst.getJSONArray("RelatedPostsData")
                    val related_List = ArrayList<RelatedPostsData>()
                    if (related_list_json != null && related_list_json.length() > 0) {
                        for (i in 0 until related_list_json.length()) {
                            val obj_related = RelatedPostsData()
                            val obj = related_list_json.getJSONObject(i)
                            if (obj.has("ID")) {
                                obj_related.id = obj.getInt("ID")
                            }
                            if (obj.has("post_title") && obj.getString("post_title") != null) {
                                obj_related.post_title = obj.getString("post_title")
                            }
                            if (obj.has("thumbImage") && obj.getString("thumbImage") != null) {
                                obj_related.thumbImage = obj.getString("thumbImage")
                            }
                            related_List.add(obj_related)
                        }
                        _object.setList_related_post(related_List)
                    }
                }
                val contentSectionList = ArrayList<ContentSectionModel>()
                var obj_content = ContentSectionModel()
                obj_content.setUrl(_object.getThumb_url())
                obj_content.setCaption("Featured")
                obj_content.videoContent = false
                contentSectionList.add(obj_content)
                if (objectFirst.has("EmbededData")) {
                    val embededVideoList = objectFirst.getJSONArray("EmbededData")
                    for (i in 0 until embededVideoList.length()) {
                        obj_content = ContentSectionModel()
                        val obj = embededVideoList.getJSONObject(i)
                        obj_content.setUrl(if (obj.has("EmbededPath")) obj.getString("EmbededPath") else "")
                        obj_content.setCaption(if (obj.has("Caption")) obj.getString("Caption") else "")
                        if (obj_content.getUrl() != null && !obj_content.getUrl()
                                .isEmpty() && obj_content.getUrl().contains("youtu.be")
                        ) {
                            if (obj_content.getUrl().contains("youtu.be")) {
                                obj_content.videoContent = true
                                val _youtube_id =
                                    obj_content.getUrl().replace("https://youtu.be/", "")
                                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                                obj_content.setYoutube_url("http://img.youtube.com/vi/$_youtube_id/0.jpg")
                            }
                        }
                        contentSectionList.add(obj_content)
                    }
                }
                try {
                    if (objectFirst.has("EmbededImage")) {
                        val embededImageList = objectFirst.getJSONArray("EmbededImage")
                        for (i in 0 until embededImageList.length()) {
                            val `object` = embededImageList.getJSONObject(i)
                            obj_content = ContentSectionModel()
                            obj_content.setUrl(if (`object`.has("EmbededPath")) `object`.getString("EmbededPath") else "")
                            obj_content.setCaption(if (`object`.has("Caption")) `object`.getString("Caption") else "")
                            obj_content.videoContent = false
                            contentSectionList.add(obj_content)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("TAGG", "Exception at parseembeddd image " + e.message)
                }
                _object.setFeaturedImage(contentSectionList)
                if (objectFirst.has("videos_and_files")) {
                    var videoArray: JSONArray? = null
                    try {
                        videoArray = objectFirst.getJSONArray("videos_and_files")
                    } catch (e: java.lang.Exception) {
                    }
                    if (videoArray != null) for (i in 0 until videoArray.length()) {
                        val obj = videoArray.getJSONObject(i)
                        val videos_and_files = videos_and_files()
                        if (obj.has("text_file") && !obj.getString("text_file").toString()
                                .equals("false", ignoreCase = true)
                        ) {
                            val obj_text_file = text_files()
                            val obj_text = obj.getJSONObject("text_file")
                            obj_text_file.id = if (obj_text.has("ID")) obj_text.getInt("ID") else 0
                            obj_text_file.setTitle(if (obj_text.has("title")) obj_text.getString("title") else "")
                            obj_text_file.setIcon(if (obj_text.has("icon")) obj_text.getString("icon") else "")
                            obj_text_file.setFilename(
                                if (obj_text.has("filename")) obj_text.getString(
                                    "filename"
                                ) else ""
                            )
                            obj_text_file.setUrl(if (obj_text.has("url")) obj_text.getString("url") else "")
                            videos_and_files.setObj_text_files(obj_text_file)
                        } else videos_and_files.setObj_text_files(null)
                        try {
                            if (obj.has("trace_image") && !obj.getString("trace_image").toString()
                                    .equals("false", ignoreCase = true)
                            ) {
                                val obj_trace = trace_image()
                                val obj_trace_object = obj.getJSONObject("trace_image")
                                obj_trace.id =
                                    if (obj_trace_object.has("ID")) obj_trace_object.getInt("ID") else 0
                                obj_trace.setTitle(
                                    if (obj_trace_object.has("title")) obj_trace_object.getString(
                                        "title"
                                    ) else ""
                                )
                                obj_trace.setIcon(
                                    if (obj_trace_object.has("icon")) obj_trace_object.getString(
                                        "icon"
                                    ) else ""
                                )
                                obj_trace.setFilename(
                                    if (obj_trace_object.has("filename")) obj_trace_object.getString(
                                        "filename"
                                    ) else ""
                                )
                                obj_trace.setUrl(
                                    if (obj_trace_object.has("url")) obj_trace_object.getString(
                                        "url"
                                    ) else ""
                                )
                                if (obj_trace_object.has("sizes")) {
                                    val objSize = obj_trace_object.getJSONObject("sizes")
                                    val obj_size = sizes()
                                    obj_size.setLarge(if (objSize.has("large")) objSize.getString("large") else "")
                                    obj_trace.setObj_sizes(obj_size)
                                } else {
                                    obj_trace.setObj_sizes(null)
                                }
                                videos_and_files.setObj_trace_image(obj_trace)
                            } else videos_and_files.setObj_trace_image(null)
                        } catch (e: java.lang.Exception) {
                            Log.e("TAGGG", "Exception at add traceImage " + e.message)
                        }
                        try {
                            if (obj.has("overlay_image") && !obj.getString("overlay_image")
                                    .toString().equals("false", ignoreCase = true)
                            ) {
                                val overlaid = Overlaid()
                                val obj_overlaid_object = obj.getJSONObject("overlay_image")
                                if (obj_overlaid_object != null) {
                                    overlaid.setTitle(
                                        if (obj_overlaid_object.has("title")) obj_overlaid_object.getString(
                                            "title"
                                        ) else ""
                                    )
                                    overlaid.setFilename(
                                        if (obj_overlaid_object.has("filename")) obj_overlaid_object.getString(
                                            "filename"
                                        ) else ""
                                    )
                                    overlaid.setUrl(
                                        if (obj_overlaid_object.has("url")) obj_overlaid_object.getString(
                                            "url"
                                        ) else ""
                                    )
                                }
                                videos_and_files.setObj_overlaid(overlaid)
                            } else videos_and_files.setObj_overlaid(null)
                        } catch (e: java.lang.Exception) {
                            Log.e("TAGG", "Exception at getoverlay " + e.message)
                        }
                        _lst_video_file.add(videos_and_files)
                    }
                    if (_lst_video_file != null && !_lst_video_file.isEmpty()) _object.setVideo_and_file_list(
                        _lst_video_file
                    )
                } else _object.setVideo_and_file_list(null)
            }
            if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size >= 2 && _object.getVideo_and_file_list()
                    .get(0).getObj_text_files() != null && _object.getVideo_and_file_list().get(1)
                    .getObj_text_files() != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
                    .isEmpty()
            ) {
                if (_object.getVideo_and_file_list().get(0)
                        .getObj_overlaid() != null || _object.getVideo_and_file_list().get(1)
                        .getObj_overlaid() != null
                ) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window
                } else if (_object.getVideo_and_file_list().get(0)
                        .getObj_trace_image() == null || _object.getVideo_and_file_list().get(1)
                        .getObj_trace_image() == null
                ) {
                    tutorial_type = Tutorial_Type.Strokes_Window
                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window
                }
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
                    .get(0)
                    .getObj_trace_image() != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
                    .isEmpty()
            ) {
                tutorial_type = Tutorial_Type.Video_Tutorial_Trace
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
                    .get(0)
                    .getObj_overlaid() != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
                    .isEmpty()
            ) {
                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
                    .get(0).getObj_overlaid() != null && _object.getYoutube_link_list().isEmpty()
            ) {
                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY
            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
                    .get(0).getObj_trace_image() != null && _object.getYoutube_link_list().isEmpty()
            ) {
                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE
            } else if (_object.getExternal_link() != null && !_object.getExternal_link()
                    .isEmpty()
            ) {
                if (_object.getExternal_link().contains("youtu.be")) {
                    tutorial_type = Tutorial_Type.SeeVideo_From_External_Link
                } else {
                    tutorial_type = Tutorial_Type.Read_Post
                }
            } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
                    .isEmpty()
            ) {
                tutorial_type = Tutorial_Type.See_Video
            } else {
                tutorial_type = Tutorial_Type.READ_POST_DEFAULT
            }
//            progressDialog.dismiss()
            processTutorial()
        } catch (e: java.lang.Exception) {
//            if (progressDialog != null) {
//                progressDialog.dismiss()
//            }
            Log.e("TAGGG", "Exception at parse " + e.message + " " + e.stackTrace.toString())
        }
    }

    fun processTutorial() {
        if (tutorial_type === Tutorial_Type.See_Video) {
            val eventName = "watch_video_"
            StringConstants.IsFromDetailPage = true
            val intent = Intent(
                this@VideoGuideActivity,
                Play_YotubeVideo::class.java
            )
            intent.putExtra("url", _object.getYoutube_link_list())
            intent.putExtra("isVideo", true)
            startActivity(intent)
            return
        } else if (tutorial_type === Tutorial_Type.Read_Post) {
            val eventName = "read_post_"
            try {
                Log.e("TAGGG", "ExtLinks " + _object.getExternal_link())
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
                startActivity(browserIntent);*/KGlobal.openInBrowser(
                    this@VideoGuideActivity,
                    _object.getExternal_link().replace("htttps://", "https://").trim { it <= ' ' })
            } catch (ex: ActivityNotFoundException) {
            } catch (e: java.lang.Exception) {
            }
            return
        } else if (tutorial_type === Tutorial_Type.SeeVideo_From_External_Link) {
            val eventName = "watch_video_from_external_link_"
            StringConstants.IsFromDetailPage = true
            val intent = Intent(
                this@VideoGuideActivity,
                Play_YotubeVideo::class.java
            )
            intent.putExtra("url", _object.getExternal_link())
            intent.putExtra("isVideo", true)
            Log.e("TAGGG", "URL " + _object.getExternal_link())
            startActivity(intent)
            return
        } else if (tutorial_type === Tutorial_Type.Video_Tutorial_Overraid) {
            val eventName = "video_tutorial_overlaid_"
            val fileName = _object.getVideo_and_file_list()[0].obj_overlaid.getFilename()
            val file = File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName)
            val youtubeLink = _object.getYoutube_link_list()
            if (youtubeLink != null) {
                val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                    .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                if (!file.exists()) {
                    DownloadsImage(
                        _youtube_id,
                        _object.getVideo_and_file_list()[0].obj_overlaid.getUrl(),
                        false,
                        _object.getVideo_and_file_list()[0].obj_overlaid.getFilename(),
                    ).execute(
                        _object.getVideo_and_file_list()[0].obj_overlaid.getUrl()
                    )
                    return
                } else {
//                    if (_object.getPost_title() != null)
//                        FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        this@VideoGuideActivity,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra("youtube_video_id", _youtube_id)
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this))
                    if (_object.getCanvas_color().isNotEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color())
                    }
                    val swatches = _object.swatches
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.id)
                    startActivity(intent)
                    return
                }
            } else {
                Toast.makeText(
                    this@VideoGuideActivity,
                    "Youtube Link Not Found!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (tutorial_type === Tutorial_Type.DO_DRAWING_OVERLAY) {
            val eventName = "do_drawing_overlay_"
            val fileName = _object.getVideo_and_file_list()[0].getObj_overlaid().getFilename()
            val fileURL = _object.getVideo_and_file_list()[0].getObj_overlaid().getUrl()
            DownloadOverlayFromDoDrawing(fileURL, fileName, false).execute()
        } else if (tutorial_type === Tutorial_Type.DO_DRAWING_TRACE) {
            val fileName = _object.getVideo_and_file_list()[0].getObj_trace_image().getFilename()
            val fileURL = _object.getVideo_and_file_list()[0].getObj_trace_image().getUrl()
            DownloadOverlayFromDoDrawing(fileURL, fileName, true).execute()
        } else if (tutorial_type === Tutorial_Type.Strokes_Window) {
            DownloadsTextFiles(_object).execute()
        } else if (tutorial_type === Tutorial_Type.Strokes_Overlaid_Window) {
            var OverLayName: String? = null
            var OverLayUrl: String? = null
            if (_object.getVideo_and_file_list()[0].getObj_overlaid() != null) {
                OverLayName = if (_object.getVideo_and_file_list()[0].getObj_overlaid()
                        .getFilename() != null
                ) _object.getVideo_and_file_list()[0].getObj_overlaid()
                    .getFilename() else "overLaid.jpg"
                OverLayUrl = _object.getVideo_and_file_list()[0].getObj_overlaid().getUrl()
            } else {
                OverLayName = if (_object.getVideo_and_file_list()[1].getObj_overlaid()
                        .getFilename() != null
                ) _object.getVideo_and_file_list()[1].getObj_overlaid()
                    .getFilename() else "overLaid.jpg"
                OverLayUrl = _object.getVideo_and_file_list()[1].getObj_overlaid().getUrl()
                if (OverLayName != null && OverLayName != OverLayUrl) {
                    DownloadOverlayImage(OverLayUrl, OverLayName).execute()
                }
            }
        } else if (tutorial_type === Tutorial_Type.Video_Tutorial_Trace) {
            try {
                val youtubeLink = _object.getYoutube_link_list()
                if (youtubeLink != null) {
                    val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    if (_object.getVideo_and_file_list() != null && _object.getVideo_and_file_list()[0].obj_trace_image != null && _object.getVideo_and_file_list()[0].obj_trace_image.getObj_sizes() != null) {
                        if (_object.getVideo_and_file_list()[0].obj_trace_image.getObj_sizes()
                                .getLarge() != null
                        ) {
                            val fileName =
                                _object.getVideo_and_file_list()[0].obj_trace_image.getObj_sizes()
                                    .getLarge().substring(
                                        _object.getVideo_and_file_list()[0].obj_trace_image.getObj_sizes()
                                            .getLarge().lastIndexOf('/') + 1
                                    )
                            val file =
                                File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName)
                            if (!file.exists()) DownloadsImage(
                                _youtube_id,
                                _object.getVideo_and_file_list()[0].obj_trace_image.getObj_sizes()
                                    .getLarge(),
                                true,
                                ""
                            ).execute(
                                _object.getVideo_and_file_list()[0].obj_trace_image.getObj_sizes()
                                    .getLarge()
                            ) else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                                StringConstants.IsFromDetailPage = false
                                val intent = Intent(
                                    this@VideoGuideActivity,
                                    PaintActivity::class.java
                                )
                                intent.putExtra("youtube_video_id", _youtube_id)
                                intent.action = "YOUTUBE_TUTORIAL"
                                intent.putExtra("paint_name", file.absolutePath)
                                if (!_object.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", _object.getCanvas_color())
                                }
                                intent.putExtra("id", _object.id)
                                startActivity(intent)
                            }
                        }
                    } else {
//                        if (_object.getPost_title() != null)
//                            FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                        StringConstants.IsFromDetailPage = false
                        val intent = Intent(
                            this@VideoGuideActivity,
                            PaintActivity::class.java
                        )
                        intent.putExtra("youtube_video_id", _youtube_id)
                        intent.action = "YOUTUBE_TUTORIAL"
                        if (!_object.getCanvas_color().isEmpty()) {
                            intent.putExtra("canvas_color", _object.getCanvas_color())
                        }
                        intent.putExtra("id", _object.id)
                        startActivity(intent)
                    }
                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(this@VideoGuideActivity, "Failed To Load!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (tutorial_type === Tutorial_Type.READ_POST_DEFAULT) {
            try {
                KGlobal.openInBrowser(this@VideoGuideActivity, defaultLink.trim { it <= ' ' })
            } catch (anf: ActivityNotFoundException) {
            } catch (e: java.lang.Exception) {
            }
        }

    }

    inner class DownloadsImage(
        var youtubeLink: String,
        var traceImageLink: String,
        isFromTrace: Boolean,
        fileName: String
    ) :
        AsyncTask<String?, Void?, String?>() {
        var fileName: String
        var isFromTrace = false

        init {
            this.isFromTrace = isFromTrace
            this.fileName = fileName
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@VideoGuideActivity)
            progressDialog.setMessage(getString(R.string.please_wait))
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: String?): String? {
            var url: URL? = null
            try {
                url = URL(strings[0])
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            var bm: Bitmap? = null
            try {
                bm = BitmapFactory.decodeStream(url!!.openConnection().getInputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            }

            //Create Path to save Image
            val path =
                File(KGlobal.getTraceImageFolderPath(this@VideoGuideActivity)) //Creates app specific folder
            if (!path.exists()) {
                path.mkdirs()
            }
            val imageFile = File(
                path,
                traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1)
            ) // Imagename.png
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(imageFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                if (out != null) {
                    bm!!.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                    out.close()
                } // Compress Image

                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
            } catch (e: java.lang.Exception) {
                Log.e("TAGG", "Exception at download " + e.message)
            }
            return imageFile.absolutePath
        }

        override fun onPostExecute(path: String?) {
            super.onPostExecute(path)
            try {
                progressDialog.dismiss()
                //                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        this@VideoGuideActivity,
                        PaintActivity::class.java
                    )
                    intent.putExtra("youtube_video_id", youtubeLink)
                    intent.action = "YOUTUBE_TUTORIAL"
                    intent.putExtra("paint_name", path)
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color())
                    }
                    intent.putExtra("id", _object.getID())
                    startActivity(intent)
                } else {
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        this@VideoGuideActivity,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra(
                        "ParentFolderPath",
                        KGlobal.getTraceImageFolderPath(this@VideoGuideActivity)
                    )
                    intent.putExtra("youtube_video_id", youtubeLink)
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color())
                    }
                    val swatches: List<ColorSwatch> = _object.getSwatches()
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.getID())
                    startActivity(intent)
                }
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception at post $e")
            }
        }
    }


    inner class DownloadsTextFiles(var _objects: PostDetailModel) :
        AsyncTask<Void?, Void?, java.util.ArrayList<String?>?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@VideoGuideActivity)
            progressDialog.setMessage(getString(R.string.please_wait))
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: Void?): java.util.ArrayList<String?>? {
            val lst_fileNames = java.util.ArrayList<String?>()
            val file1 = File(KGlobal.getStrokeEventFolderPath(this@VideoGuideActivity))
            if (!file1.exists()) {
                file1.mkdirs()
            }
            for (i in 0..1) {
                val textFileLink = _objects.getVideo_and_file_list()[i].getObj_text_files().getUrl()
                val fileName =
                    _objects.getVideo_and_file_list()[i].getObj_text_files().getFilename()
                val file = File(file1, fileName)
                if (file.exists()) {
                    lst_fileNames.add(file.absolutePath)
                } else {
                    try {
                        val url = URL(textFileLink)
                        val ucon = url.openConnection()
                        ucon.readTimeout = 50000
                        ucon.connectTimeout = 100000
                        val `is` = ucon.getInputStream()
                        val inStream = BufferedInputStream(`is`, 1024 * 5)
                        if (file.exists()) {
                            lst_fileNames.add(file.absolutePath)
                            break
                        }
                        val outStream = FileOutputStream(file)
                        val buff = ByteArray(5 * 1024)
                        var len: Int
                        while (inStream.read(buff).also { len = it } != -1) {
                            outStream.write(buff, 0, len)
                        }
                        outStream.flush()
                        outStream.close()
                        inStream.close()
                        lst_fileNames.add(file.absolutePath)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return lst_fileNames
        }

        override fun onPostExecute(list: java.util.ArrayList<String?>?) {
            super.onPostExecute(list)
            try {
                if (this@VideoGuideActivity.isDestroyed) {
                    return
                }
                if (progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()
                StringConstants.IsFromDetailPage = false
                val intent = Intent(
                    this@VideoGuideActivity,
                    PaintActivity::class.java
                )
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color())
                }
                val youtubeLink = _objects.getYoutube_link_list()
                if (youtubeLink != null) {
                    val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    intent.putExtra("youtube_video_id", _youtube_id)
                }
                intent.action = "YOUTUBE_TUTORIAL_WITH_FILE"
                if (list != null) {
                    if (list.size == 2) {
                        intent.putExtra("StrokeFilePath", list[0])
                        intent.putExtra("EventFilePath", list[1])
                    } else Toast.makeText(
                        this@VideoGuideActivity,
                        "Stroke Event File Not Downloaded Properly",
                        Toast.LENGTH_SHORT
                    ).show()
                }

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                intent.putExtra("id", _object.getID())
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception " + e.message)
            }
        }
    }

    inner class DownloadOverlayImage(var traceImageLink: String, var fileName: String) :
        AsyncTask<Void?, Void?, java.util.ArrayList<String?>?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@VideoGuideActivity)
            progressDialog.setMessage(getResources().getString(R.string.please_wait))
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: Void?): java.util.ArrayList<String?>? {
            val filesList: java.util.ArrayList<String?> = downloadTextFiles()
            val file = File(
                KGlobal.getTraceImageFolderPath(this@VideoGuideActivity),
                fileName
            )
            return if (file.exists()) {
                filesList
            } else {
                var url: URL? = null
                try {
                    url = URL(traceImageLink)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                var bm: Bitmap? = null
                try {
                    bm = BitmapFactory.decodeStream(url!!.openConnection().getInputStream())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //Create Path to save Image
                val path =
                    File(KGlobal.getTraceImageFolderPath(this@VideoGuideActivity)) //Creates app specific folder
                if (!path.exists()) {
                    path.mkdirs()
                }
                val imageFile = File(path, fileName) // Imagename.png
                var out: FileOutputStream? = null
                try {
                    out = FileOutputStream(imageFile)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                try {
                    if (out != null) {
                        bm!!.compress(Bitmap.CompressFormat.PNG, 100, out)
                        out.flush()
                        out.close()
                    } // Compress Image

                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (e: java.lang.Exception) {
                    Log.e("TAGG", "Exception at download " + e.message)
                }
                filesList
            }
        }

        override fun onPostExecute(lst_main: java.util.ArrayList<String?>?) {
            super.onPostExecute(lst_main)
            try {
                progressDialog.dismiss()
                //                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try " + _object.getPost_title());
                StringConstants.IsFromDetailPage = false
                val intent = Intent(
                    this@VideoGuideActivity,
                    PaintActivity::class.java
                )
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color())
                }
                val youtubeLink: String = _object.getYoutube_link_list()
                if (youtubeLink != null) {
                    val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    intent.putExtra("youtube_video_id", _youtube_id)
                }
                intent.action = "YOUTUBE_TUTORIAL_WITH_OVERLAID"
                if (lst_main != null) {
                    if (lst_main.size == 2) {
                        intent.putExtra("StrokeFilePath", lst_main?.get(0))
                        intent.putExtra("EventFilePath", lst_main?.get(1))
                    }
                }
                intent.putExtra(
                    "OverlaidImagePath", File(
                        KGlobal.getTraceImageFolderPath(this@VideoGuideActivity),
                        fileName
                    ).absolutePath
                )
                intent.putExtra("id", _object.id)
                startActivity(intent)
                if (lst_main != null) {
                    Log.e(
                        "TAGGG",
                        "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size
                    )
                }
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception at post $e")
            }
        }

        fun downloadTextFiles(): java.util.ArrayList<String?> {
            val lst_fileNames = java.util.ArrayList<String?>()
            val file1 = File(KGlobal.getStrokeEventFolderPath(this@VideoGuideActivity))
            if (!file1.exists()) {
                file1.mkdirs()
            }
            for (i in 0..1) {
                val textFileLink: String =
                    _object.getVideo_and_file_list().get(i).getObj_text_files().getUrl()
                val fileName: String =
                    _object.getVideo_and_file_list().get(i).getObj_text_files().getFilename()
                val file = File(file1, fileName)
                if (file.exists()) {
                    lst_fileNames.add(file.absolutePath)
                } else {
                    try {
                        val url = URL(textFileLink)
                        val ucon = url.openConnection()
                        ucon.readTimeout = 50000
                        ucon.connectTimeout = 100000
                        val `is` = ucon.getInputStream()
                        val inStream = BufferedInputStream(`is`, 1024 * 5)
                        if (file.exists()) {
                            lst_fileNames.add(file.absolutePath)
                            break
                        }
                        val outStream = FileOutputStream(file)
                        val buff = ByteArray(5 * 1024)
                        var len: Int
                        while (inStream.read(buff).also { len = it } != -1) {
                            outStream.write(buff, 0, len)
                        }
                        outStream.flush()
                        outStream.close()
                        inStream.close()
                        lst_fileNames.add(file.absolutePath)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return lst_fileNames
        }
    }

    inner class DownloadOverlayFromDoDrawing(
        var traceImageLink: String,
        var fileName: String,
        isFromTrace: Boolean
    ) :
        AsyncTask<Void?, Void?, String?>() {
        var isFromTrace = false

        init {
            this.isFromTrace = isFromTrace
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@VideoGuideActivity)
            progressDialog.setMessage(getResources().getString(R.string.please_wait))
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: Void?): String? {
            val file = File(
                KGlobal.getTraceImageFolderPath(this@VideoGuideActivity),
                fileName
            )
            return if (file.exists()) {
                file.absolutePath
            } else {
                var url: URL? = null
                try {
                    url = URL(traceImageLink)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                var bm: Bitmap? = null
                try {
                    bm = BitmapFactory.decodeStream(url!!.openConnection().getInputStream())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //Create Path to save Image
                val path =
                    File(KGlobal.getTraceImageFolderPath(this@VideoGuideActivity)) //Creates app specific folder
                if (!path.exists()) {
                    path.mkdirs()
                }
                val imageFile = File(path, fileName) // Imagename.png
                var out: FileOutputStream? = null
                try {
                    out = FileOutputStream(imageFile)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                try {
                    if (out != null) {
                        bm!!.compress(Bitmap.CompressFormat.PNG, 100, out)
                        out.flush()
                        out.close()
                    } // Compress Image

                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                } catch (e: java.lang.Exception) {
                    Log.e("TAGG", "Exception at download " + e.message)
                }
                imageFile.absolutePath
            }
        }

        override fun onPostExecute(path: String?) {
            super.onPostExecute(path)
            try {
                progressDialog.dismiss()
                //                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(SubCategoryActivity.this, "Try_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));
                StringConstants.IsFromDetailPage = false
                if (isFromTrace) {
                    val intent = Intent(
                        this@VideoGuideActivity,
                        PaintActivity::class.java
                    )
                    intent.action = "Edit Paint"
                    intent.putExtra("FromLocal", true)
                    intent.putExtra("paint_name", path)
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color())
                    }
                    val swatches: List<ColorSwatch> = _object.getSwatches()
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.getID())
                    startActivity(intent)
                } else {
                    val intent = Intent(
                        this@VideoGuideActivity,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra(
                        "ParentFolderPath",
                        KGlobal.getTraceImageFolderPath(this@VideoGuideActivity)
                    )
                    if (!_object.getCanvas_color().isEmpty()) {
                        intent.putExtra("canvas_color", _object.getCanvas_color())
                    }
                    val swatches: List<ColorSwatch> = _object.getSwatches()
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.getID())
                    startActivity(intent)
                }
                Log.e(
                    "TAGGG",
                    "Overlay Downloaded File traceImageLink $traceImageLink fileName $fileName full path $path"
                )
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception at post $e")
            }
        }
    }
}