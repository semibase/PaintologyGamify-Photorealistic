package com.paintology.lite.photorealistic.drawing.search

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.paintology.lite.photorealistic.drawing.Activity.BaseActivity
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.SearchViewActivity
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.Community.Community
import com.paintology.lite.photorealistic.drawing.Community.ShowPostFromNotification
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.NewSubCategoryActivity
import com.paintology.lite.photorealistic.drawing.Enums.SearchResultType
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type
import com.paintology.lite.photorealistic.drawing.Model.Blogpost
import com.paintology.lite.photorealistic.drawing.Model.PostDetailResponse
import com.paintology.lite.photorealistic.drawing.Model.SearchResponse
import com.paintology.lite.photorealistic.drawing.Model.Tutorial
import com.paintology.lite.photorealistic.drawing.Model.Userpost
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.getSearchedContent
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.saveSearchedContent
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySearchBinding
import com.paintology.lite.photorealistic.drawing.interfaces.SearchItemClickListener
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo
import com.paintology.lite.photorealistic.drawing.room.AppDatabase
import com.paintology.lite.photorealistic.drawing.util.AppUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.net.MalformedURLException
import java.net.URL

class SearchActivity : BaseActivity(), SearchItemClickListener {

    private val TAG = SearchActivity::class.java.name
    private lateinit var binding: ActivitySearchBinding

    //    var tutorials: MutableList<PostDetailModel> = ArrayList()
    var tutorials: MutableList<Tutorial> = ArrayList()
    var userPostList: List<Userpost> = ArrayList()
    var blogPostList: List<Blogpost> = ArrayList()
    private var db: AppDatabase? = null
    lateinit var mContext: Context
    var defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/"
    var progressDialog: ProgressDialog? = null
    private var searchedSubCatId: String? = null
    var tutorial_type: Tutorial_Type? = null

    var userLevel = StringConstants.beginner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userLevel =
            sharedPref.getString(StringConstants.user_level, StringConstants.beginner).toString()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        db = MyApplication.getDb()

        mContext = this

        initViews()
        getDataFromIntent()

        AppUtils.hideKeyboard(this)
    }

    private fun initViews() {

        binding.imgTest.onSingleClick {
            openActivity(SearchViewActivity::class.java)
        }

        binding.btnSearchHeader.setOnClickListener(View.OnClickListener {
            FirebaseUtils.logEvents(
                this@SearchActivity,
                StringConstants.search_header_textentry_query
            )
            if (binding.edtHashSearch.text.toString().isNotEmpty()) {
                searchFromEditText()
            }
        })

        binding.edtHashSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                if (binding.edtHashSearch.text.toString().isNotEmpty()) {
                    searchFromEditText()
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.edtHashSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.isNotEmpty()) {
                        binding.btnClear.visibility = View.VISIBLE
                    } else {
                        binding.btnClear.visibility = View.GONE
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.tvTutorialMore.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SearchActivity, CategoryActivity::class.java)
            startActivity(intent)
            finish()
        })

        binding.tvUserpostMore.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SearchActivity, Community::class.java)
            startActivity(intent)
            finish()
        })

        binding.tvBlogpostMore.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SearchActivity, Community::class.java)
            startActivity(intent)
            finish()
        })

        binding.btnClear.setOnClickListener(View.OnClickListener {
            binding.edtHashSearch.setText("")
        })
    }

    private fun getDataFromIntent() {
        val hasSearchResponse = intent.getBooleanExtra("has_search_response", false)

        var response: String? = null
        if (hasSearchResponse) {
            response = AppUtils.getSearchResponse()
        }

        val search = intent.getStringExtra("search")

        if (response != null) {
            initData(response)
        }

        if (!TextUtils.isEmpty(search)) {
            binding.edtHashSearch.setText(search)
        }

    }

    private fun initData(response: String) {
//        var searchResponse: SearchResponse? = null
//        try {
//            searchResponse = Gson().fromJson(response, SearchResponse::class.java)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        if (response != null) {
            val mainArray = JSONObject(response)
            if (mainArray.length() > 0) {
                val tutorials = mainArray.get("tutorials").toString()
                val blogpost = mainArray.get("blogpost").toString()
                val userpost = mainArray.get("userpost").toString()

//                setupTutorial(tutorials)
                setupTutorialWithGson(tutorials)
                setupBlogPost(blogpost)
                setupUserPost(userpost)
            }
        }
    }

    private fun setupUserPost(userpostResponse: String) {
        val gson = Gson()
        // prepare type information
        val itemListType: Type =
            object : TypeToken<ArrayList<Userpost>?>() {}.type

        try {
            // read the list
            userPostList = gson.fromJson(userpostResponse, itemListType)

            if (userPostList != null) {
                binding.userPostContainer.visibility = View.VISIBLE
//                val adapter = UserPostAdapter(this@SearchActivity, userPostList, this, SearchResultType.USER_POST)
//                setupUserPostRecyclerView(adapter)

                // Initializing the ViewPagerAdapter
                // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)
                binding.vpUserPost.adapter = UserPostAdapter(
                    this@SearchActivity,
                    userPostList,
                    binding.vpUserPost,
                    this,
                    SearchResultType.USER_POST
                )

                setupViewPager(binding.vpUserPost)

            } else {
                binding.userPostContainer.visibility = View.GONE
            }

//            binding.tvUserpostHeading.text = String.format("User Posts (%s)", userPostList.size)
            binding.tvUserpostHeading.text = "User Posts"

        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

//    private fun setupUserPostRecyclerView(adapter: UserPostAdapter) {
//        binding.hsvUserPost.setLayoutManager(
//            GridLayoutManager(
//                this,
//                1,
//                GridLayoutManager.HORIZONTAL,
//                false
//            )
//        )
//        binding.hsvUserPost.adapter = adapter
//    }

    private fun setupBlogPost(blogpostResponse: String) {
        val gson = Gson()
        // prepare type information
        val itemListType: Type =
            object : TypeToken<ArrayList<Blogpost>?>() {}.type


        try {
            // read the list
            blogPostList = gson.fromJson(blogpostResponse, itemListType)

            if (blogPostList != null) {
                binding.blogPostContainer.visibility = View.VISIBLE
//                val adapter = BlogPostAdapter(this@SearchActivity, blogPostList, this, SearchResultType.BLOG_POST)
//                setupBlogPostRecyclerView(adapter)

                // Initializing the ViewPagerAdapter
                // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)
                binding.vpBlogPost.adapter = BlogPostAdapter(
                    this@SearchActivity,
                    blogPostList,
                    binding.vpBlogPost,
                    this,
                    SearchResultType.BLOG_POST
                )

                setupViewPager(binding.vpBlogPost)

            } else {
                binding.blogPostContainer.visibility = View.GONE
            }

//            binding.tvBlogpostHeading.text = String.format("Blog Posts (%s)", blogPostList.size)
            binding.tvBlogpostHeading.text = "Blog Posts"

        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

//    private fun setupBlogPostRecyclerView(adapter: BlogPostAdapter) {
//        binding.hsvBlogPost.setLayoutManager(
//            GridLayoutManager(
//                this,
//                1,
//                GridLayoutManager.HORIZONTAL,
//                false
//            )
//        )
//
//        binding.hsvBlogPost.adapter = adapter
//    }

    private fun setupTutorialWithGson(tutorialsResponse: String) {
        val gson = Gson()
        // prepare type information
        val itemListType: Type =
            object : TypeToken<ArrayList<Tutorial>?>() {}.type

        try {
            // read the list
            val list: List<Tutorial> = gson.fromJson(tutorialsResponse, itemListType)

            tutorials.addAll(list)

            if (list != null) {
                binding.tutorialsContainer.visibility = View.VISIBLE
//                val adapter = TutorialAdapter(this@SearchActivity, list, this, SearchResultType.TUTORIAL)
//                setupTutorialRecyclerView(adapter)

                // Initializing the ViewPagerAdapter
                // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)
                binding.vpTutorials.adapter = SliderTutorialAdapter(
                    this@SearchActivity, list,
                    binding.vpTutorials, this, SearchResultType.TUTORIAL
                )

                setupViewPager(binding.vpTutorials)

            } else {
                binding.tutorialsContainer.visibility = View.GONE
            }

//            binding.tvTutorialHeading.text = String.format("Tutorials (%s)", list.size)
            binding.tvTutorialHeading.text = "Tutorials"

        } catch (e: Exception) {
            binding.tutorialsContainer.visibility = View.GONE
            e.message?.let { Log.e(TAG, it) }
        }

//        val tutorials = Gson().fromJson(tutorialsResponse, SearchResponse::class.java)
//        val list = tutorials?.tutorials
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        // You need to retain one page on each side so that the next and previous items are visible
        viewPager.offscreenPageLimit = 1

        // Add a PageTransformer that translates the next and previous items horizontally
        // towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
//                    page.scaleY = 1 - (0.25f * Math.abs(position))
            // If you want a fading effect uncomment the next line:
//                     page.alpha = 0.25f + (1 - Math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)

        // The ItemDecoration gives the current (centered) item horizontal margin so that
        // it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            this@SearchActivity,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        viewPager.addItemDecoration(itemDecoration)
    }

//    private fun setupTutorialRecyclerView(adapter: TutorialAdapter) {
//        binding.hsvTutorials.layoutManager = GridLayoutManager(
//            this,
//            1,
//            GridLayoutManager.HORIZONTAL,
//            false
//        )
//        binding.hsvTutorials.adapter = adapter
//    }

    override fun selectItem(pos: Int, type: SearchResultType) {
        if (type == SearchResultType.BLOG_POST) {
            val webUrl = blogPostList[pos].webUrl
            KGlobal.openInBrowser(this, webUrl)
        } else if (type == SearchResultType.USER_POST) {
            val post_id = userPostList[pos].id
            val bundle = Bundle()
            bundle.putString("post_id", post_id.toString())

            val intent = Intent(
                this,
                ShowPostFromNotification::class.java
            )
            intent.putExtras(bundle)
            startActivity(intent)
        } else if (type == SearchResultType.TUTORIAL) {
            val _object = tutorials[pos]
            var tutorial_type: Tutorial_Type

            if (_object != null) {
                if (_object.videosAndFiles != null && _object.videosAndFiles!!.size >= 2 && _object.videosAndFiles!!
                        .get(0).textFile != null && _object.videosAndFiles!!
                        .get(1)
                        .textFile != null && _object.youtubeLink != null && _object.youtubeLink!!.isNotEmpty()
                ) {
                    if (_object.videosAndFiles!![0]
                            .overlayImage != null || _object.videosAndFiles!![1]
                            .overlayImage != null
                    ) {
                        tutorial_type = Tutorial_Type.Strokes_Overlaid_Window
                    } else if (_object.videosAndFiles!![0]
                            .traceImage == null || _object.videosAndFiles!![1]
                            .traceImage == null
                    ) {
                        tutorial_type = Tutorial_Type.Strokes_Window
                    } else {
                        tutorial_type = Tutorial_Type.Strokes_Window
                    }
                } else if (_object.videosAndFiles != null && _object.videosAndFiles!!.isNotEmpty() && _object.videosAndFiles!![0]
                        .traceImage != null && _object.youtubeLink != null && _object.youtubeLink!!.isNotEmpty()
                ) {
                    tutorial_type = Tutorial_Type.Video_Tutorial_Trace
                } else if (_object.videosAndFiles != null && _object.videosAndFiles!!.isNotEmpty() && _object.videosAndFiles!![0]
                        .overlayImage != null && _object.youtubeLink != null && !_object.youtubeLink!!
                        .isEmpty()
                ) {
                    tutorial_type = Tutorial_Type.Video_Tutorial_Overraid
                } else if (_object.videosAndFiles != null && _object.videosAndFiles!!.isNotEmpty() && _object.videosAndFiles!!
                        .get(0).overlayImage != null && _object.youtubeLink!!.isEmpty()
                ) {
                    tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY
                } else if (_object.videosAndFiles != null && _object.videosAndFiles!!.isNotEmpty() &&
                    _object.videosAndFiles!![0].traceImage != null && _object.youtubeLink!!.isEmpty()
                ) {
                    tutorial_type = Tutorial_Type.DO_DRAWING_TRACE
                }
//                else if (_object.getExternal_link() != null && !_object.getExternal_link()
//                        .isEmpty()
//                ) {
//                    if (_object.getExternal_link().contains("youtu.be")) {
//                        tutorial_type = Tutorial_Type.SeeVideo_From_External_Link
//                    } else {
//                        tutorial_type = Tutorial_Type.Read_Post
//                    }
//                }
                else if (_object.youtubeLink != null && _object.youtubeLink!!.isNotEmpty()
                ) {
                    tutorial_type = Tutorial_Type.See_Video
                } else {
                    tutorial_type = Tutorial_Type.READ_POST_DEFAULT
                }

                processTutorial(tutorial_type, _object)
            }

        }
    }

    fun processTutorial(tutorial_type: Tutorial_Type, _object: Tutorial) {
        if (tutorial_type == Tutorial_Type.See_Video) {
            val eventName = "watch_video_"
            StringConstants.IsFromDetailPage = true
            val intent = Intent(
                this@SearchActivity,
                Play_YotubeVideo::class.java
            )
            intent.putExtra("url", _object.youtubeLink)
            intent.putExtra("isVideo", true)
            startActivity(intent)
            return
        } else if (tutorial_type == Tutorial_Type.Read_Post) {
//            val eventName = "read_post_"
//            try {
//                Log.e("TAGGG", "ExtLinks " + _object!!.getExternal_link())
//                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_object.getExternal_link().replace("htttps://", "https://").trim()));
//                startActivity(browserIntent);*/KGlobal.openInBrowser(
//                    this@SearchActivity,
//                    _object!!.getExternal_link().replace("htttps://", "https://")
//                        .trim { it <= ' ' })
//            } catch (ex: ActivityNotFoundException) {
//            } catch (e: java.lang.Exception) {
//            }
            return
        } else if (tutorial_type == Tutorial_Type.SeeVideo_From_External_Link) {
//            val eventName = "watch_video_from_external_link_"
//            StringConstants.IsFromDetailPage = true
//            val intent = Intent(
//                this@SearchActivity,
//                Play_YotubeVideo::class.java
//            )
//            intent.putExtra("url", _object!!.getExternal_link())
//            intent.putExtra("isVideo", true)
//            Log.e("TAGGG", "URL " + _object!!.getExternal_link())
//            startActivity(intent)
            return
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Overraid) {
            val eventName = "video_tutorial_overlaid_"
            val fileName = _object.videosAndFiles!![0].overlayImage!!.filename
            val file = File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName)
            val youtubeLink = _object.youtubeLink
            if (youtubeLink != null) {
                val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                    .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                if (!file.exists()) {
                    DownloadsImage(
                        _youtube_id,
                        _object.videosAndFiles!![0].overlayImage!!.url.toString(),
                        false,
                        _object.videosAndFiles!![0].overlayImage!!.filename.toString(),
                        mContext,
                        _object
                    ).execute(
                        _object.videosAndFiles!![0].overlayImage!!.url
                    )
                    return
                } else {
//                    if (_object.getPost_title() != null)
//                        FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        this@SearchActivity,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra("youtube_video_id", _youtube_id)
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(this))
                    if (_object.canvasColor?.isNotEmpty() == true) {
                        intent.putExtra("canvas_color", _object.canvasColor)
                    }
                    val swatches = _object.colorSwatch
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.id)
                    startActivity(intent)
                    return
                }
            } else {
                Toast.makeText(this@SearchActivity, "Youtube Link Not Found!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_OVERLAY) {
            val eventName = "do_drawing_overlay_"
            val fileName = _object.videosAndFiles?.get(0)?.overlayImage?.filename.toString()
            val fileURL = _object.videosAndFiles?.get(0)?.overlayImage?.url.toString()
            DownloadOverlayFromDoDrawing(fileURL, fileName, false, mContext, _object).execute()
        } else if (tutorial_type == Tutorial_Type.DO_DRAWING_TRACE) {
            val fileName = _object.videosAndFiles?.get(0)?.traceImage?.name.toString()
            val fileURL = _object.videosAndFiles?.get(0)?.traceImage?.url.toString()
            DownloadOverlayFromDoDrawing(fileURL, fileName, true, mContext, _object).execute()
        } else if (tutorial_type == Tutorial_Type.Strokes_Window) {
            DownloadsTextFiles(_object, mContext).execute()
        } else if (tutorial_type == Tutorial_Type.Strokes_Overlaid_Window) {
            var OverLayName: String = ""
            var OverLayUrl: String = ""
            if (_object.videosAndFiles!![0].overlayImage != null) {
                OverLayName =
                    if (_object.videosAndFiles!![0].overlayImage!!.filename != null) _object.videosAndFiles!![0].overlayImage!!.filename.toString() else "overLaid.jpg"
                OverLayUrl = _object.videosAndFiles!![0].overlayImage!!.url.toString()
            } else {
                OverLayName =
                    if (_object.videosAndFiles!![1].overlayImage!!.filename != null) _object.videosAndFiles!![1].overlayImage!!.filename.toString() else "overLaid.jpg"
                OverLayUrl = _object.videosAndFiles!![1].overlayImage!!.url.toString()
            }
            DownloadOverlayImage(OverLayUrl, OverLayName!!, mContext, _object).execute()
        } else if (tutorial_type == Tutorial_Type.Video_Tutorial_Trace) {
            try {
                val youtubeLink = _object.youtubeLink
                if (youtubeLink != null) {
                    val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    if (_object.videosAndFiles != null && _object.videosAndFiles!![0].traceImage != null && _object.videosAndFiles!![0].traceImage!!.sizes != null) {
                        if (_object.videosAndFiles!![0].traceImage!!.sizes!!.getLarge() != null) {
                            val fileName =
                                _object.videosAndFiles!![0].traceImage!!.sizes!!.getLarge()
                                    .substring(
                                        _object.videosAndFiles!![0].traceImage!!.sizes!!.getLarge()
                                            .lastIndexOf('/') + 1
                                    )
                            val file = File(KGlobal.getTraceImageFolderPath(this) + "/" + fileName)
                            if (!file.exists()) DownloadsImage(
                                _youtube_id,
                                _object.videosAndFiles!![0].traceImage!!.sizes!!.getLarge(),
                                true,
                                "",
                                mContext,
                                _object
                            ).execute(
                                _object.videosAndFiles!![0].traceImage!!.sizes!!.getLarge()
                            ) else {
//                                if (_object.getPost_title() != null)
//                                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                                StringConstants.IsFromDetailPage = false
                                val intent = Intent(
                                    this@SearchActivity,
                                    PaintActivity::class.java
                                )
                                intent.putExtra("youtube_video_id", _youtube_id)
                                intent.action = "YOUTUBE_TUTORIAL"
                                intent.putExtra("paint_name", file.absolutePath)
                                if (_object.canvasColor?.isNotEmpty() == true) {
                                    intent.putExtra("canvas_color", _object.canvasColor)
                                }
                                val swatches = _object.colorSwatch
                                val gson = Gson()
                                val swatchesJson = gson.toJson(swatches)
                                intent.putExtra("swatches", swatchesJson)
                                intent.putExtra("id", _object.id)
                                startActivity(intent)
                            }
                        }
                    } else {
//                        if (_object.getPost_title() != null)
//                            FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                        StringConstants.IsFromDetailPage = false
                        val intent = Intent(
                            this@SearchActivity,
                            PaintActivity::class.java
                        )
                        intent.putExtra("youtube_video_id", _youtube_id)
                        intent.action = "YOUTUBE_TUTORIAL"
                        if (_object.canvasColor?.isNotEmpty() == true) {
                            intent.putExtra("canvas_color", _object.canvasColor)
                        }
                        val swatches = _object.colorSwatch
                        val gson = Gson()
                        val swatchesJson = gson.toJson(swatches)
                        intent.putExtra("swatches", swatchesJson)
                        intent.putExtra("id", _object.id)
                        startActivity(intent)
                    }
                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(this@SearchActivity, "Failed To Load!", Toast.LENGTH_SHORT).show()
            }
        } else if (tutorial_type == Tutorial_Type.READ_POST_DEFAULT) {
            try {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(defaultLink.trim()));
                startActivity(browserIntent);*/
                KGlobal.openInBrowser(this@SearchActivity, defaultLink.trim { it <= ' ' })
            } catch (anf: ActivityNotFoundException) {
            } catch (e: java.lang.Exception) {
            }
        }
    }


//    override fun selectItem(pos: Int, type: SearchResultType) {
//        if (type == SearchResultType.BLOG_POST) {
//            val post_id = blogPostList[pos].id
//            val bundle = Bundle()
//            bundle.putString("post_id", post_id.toString())
//
//            val intent = Intent(
//                this,
//                ShowPostFromNotification::class.java
//            )
//            intent.putExtras(bundle)
//            startActivity(intent)
//        } else if (type == SearchResultType.USER_POST) {
//            val post_id = userPostList[pos].id
//            val bundle = Bundle()
//            bundle.putString("post_id", post_id.toString())
//
//            val intent = Intent(
//                this,
//                ShowPostFromNotification::class.java
//            )
//            intent.putExtras(bundle)
//            startActivity(intent)
//        } else if (type == SearchResultType.TUTORIAL) {
//            val _object = tutorials[pos]
//            var tutorial_type: Tutorial_Type
//
//            if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size >= 2 && _object.getVideo_and_file_list()
//                    .get(0).getObj_text_files() != null && _object.getVideo_and_file_list()
//                    .get(1)
//                    .getObj_text_files() != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
//                    .isEmpty()
//            ) {
//                if (_object.getVideo_and_file_list().get(0)
//                        .getObj_overlaid() != null || _object.getVideo_and_file_list().get(1)
//                        .getObj_overlaid() != null
//                ) {
//                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window
//                } else if (_object.getVideo_and_file_list().get(0)
//                        .getObj_trace_image() == null || _object.getVideo_and_file_list()
//                        .get(1)
//                        .getObj_trace_image() == null
//                ) {
//                    tutorial_type = Tutorial_Type.Strokes_Window
//                } else {
//                    tutorial_type = Tutorial_Type.Strokes_Window
//                }
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
//                    .get(0)
//                    .getObj_trace_image() != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
//                    .isEmpty()
//            ) {
//                tutorial_type = Tutorial_Type.Video_Tutorial_Trace
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
//                    .get(0)
//                    .getObj_overlaid() != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
//                    .isEmpty()
//            ) {
//                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
//                    .get(0).getObj_overlaid() != null && _object.getYoutube_link_list()
//                    .isEmpty()
//            ) {
//                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY
//            } else if (_object != null && _object.getVideo_and_file_list() != null && _object.getVideo_and_file_list().size > 0 && _object.getVideo_and_file_list()
//                    .get(0).getObj_trace_image() != null && _object.getYoutube_link_list()
//                    .isEmpty()
//            ) {
//                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE
//            } else if (_object.getExternal_link() != null && !_object.getExternal_link()
//                    .isEmpty()
//            ) {
//                if (_object.getExternal_link().contains("youtu.be")) {
//                    tutorial_type = Tutorial_Type.SeeVideo_From_External_Link
//                } else {
//                    tutorial_type = Tutorial_Type.Read_Post
//                }
//            } else if (_object != null && _object.getYoutube_link_list() != null && !_object.getYoutube_link_list()
//                    .isEmpty()
//            ) {
//                tutorial_type = Tutorial_Type.See_Video
//            } else {
//                tutorial_type = Tutorial_Type.READ_POST_DEFAULT
//            }
//
//            processTutorial(tutorial_type, _object)
//
//        }
//    }

    internal class DownloadOverlayFromDoDrawing(
        var traceImageLink: String,
        var fileName: String,
        isFromTrace: Boolean,
        val mContext: Context,
        val _object: Tutorial
    ) :
        AsyncTask<Void?, Void?, String?>() {
        var isFromTrace = false
        var progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(mContext)
            progressDialog?.setMessage(mContext.getResources().getString(R.string.please_wait))
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setCancelable(false)
            progressDialog?.show()
        }

        protected override fun doInBackground(vararg strings: Void?): String? {
            val file = File(
                KGlobal.getTraceImageFolderPath(mContext),
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
                    File(KGlobal.getTraceImageFolderPath(mContext)) //Creates app specific folder
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

        protected override fun onPostExecute(path: String?) {
            super.onPostExecute(path)
            try {
                progressDialog?.dismiss()
                //                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try_" + _object.getPost_title().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));
                StringConstants.IsFromDetailPage = false
                if (isFromTrace) {
                    val intent = Intent(
                        mContext,
                        PaintActivity::class.java
                    )
                    intent.action = "Edit Paint"
                    intent.putExtra("FromLocal", true)
                    intent.putExtra("paint_name", path)
                    if (_object.canvasColor?.isNotEmpty() == true) {
                        intent.putExtra("canvas_color", _object.canvasColor)
                    }
                    val swatches = _object.colorSwatch
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.id)
                    mContext.startActivity(intent)
                } else {
                    val intent = Intent(
                        mContext,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra(
                        "ParentFolderPath",
                        KGlobal.getTraceImageFolderPath(mContext)
                    )
                    if (_object.canvasColor?.isNotEmpty() == true) {
                        intent.putExtra("canvas_color", _object.canvasColor)
                    }
                    val swatches = _object.colorSwatch
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.id)
                    mContext.startActivity(intent)
                }
                Log.e(
                    "TAGGG",
                    "Overlay Downloaded File traceImageLink $traceImageLink fileName $fileName full path $path"
                )
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception at post $e")
            }
        }

        init {
            this.isFromTrace = isFromTrace
        }
    }

    internal class DownloadsImage(
        var youtubeLink: String,
        var traceImageLink: String,
        isFromTrace: Boolean,
        fileName: String,
        val mContext: Context,
        val _object: Tutorial
    ) :
        AsyncTask<String?, Void?, String?>() {
        var fileName: String
        var isFromTrace = false
        var progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(mContext)
            progressDialog?.setMessage(mContext.getResources().getString(R.string.please_wait))
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setCancelable(false)
            progressDialog?.show()
        }

        protected override fun doInBackground(vararg strings: String?): String {
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
                File(KGlobal.getTraceImageFolderPath(mContext)) //Creates app specific folder
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

        protected override fun onPostExecute(path: String?) {
            super.onPostExecute(path)
            try {
                progressDialog?.dismiss()
                //                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                if (isFromTrace) {
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        mContext,
                        PaintActivity::class.java
                    )
                    intent.putExtra("youtube_video_id", youtubeLink)
                    intent.action = "YOUTUBE_TUTORIAL"
                    intent.putExtra("paint_name", path)
                    if (_object.canvasColor?.isNotEmpty() == true) {
                        intent.putExtra("canvas_color", _object.canvasColor)
                    }
                    val swatches = _object.colorSwatch
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.id)
                    mContext.startActivity(intent)
                } else {
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        mContext,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra(
                        "ParentFolderPath",
                        KGlobal.getTraceImageFolderPath(mContext)
                    )
                    intent.putExtra("youtube_video_id", youtubeLink)
                    if (_object.canvasColor?.isNotEmpty() == true) {
                        intent.putExtra("canvas_color", _object.canvasColor)
                    }
                    val swatches = _object.colorSwatch
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("id", _object.id)
                    mContext.startActivity(intent)
                }
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception at post $e")
            }
        }

        init {
            this.isFromTrace = isFromTrace
            this.fileName = fileName
        }
    }


    internal class DownloadsTextFiles(
        var _object: Tutorial,
        val mContext: Context
    ) :
        AsyncTask<Void, Void, java.util.ArrayList<String>>() {
        var progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(mContext)
            progressDialog?.setMessage(mContext.getResources().getString(R.string.please_wait))
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setCancelable(false)
            progressDialog?.show()
        }

        protected override fun doInBackground(vararg strings: Void): java.util.ArrayList<String> {
            val lst_fileNames = java.util.ArrayList<String>()
            val file1 = File(KGlobal.getStrokeEventFolderPath(mContext))
            if (!file1.exists()) {
                file1.mkdirs()
            }
            for (i in 0..1) {
                val textFileLink = _object.videosAndFiles!![i].textFile?.url
                val fileName =
                    _object.videosAndFiles!![i].textFile?.filename
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

        protected override fun onPostExecute(list: java.util.ArrayList<String>) {
            super.onPostExecute(list)
            try {
//                if (isDestroyed()) {
//                    return
//                }
                if (progressDialog != null && progressDialog?.isShowing() == true) progressDialog?.dismiss()
                StringConstants.IsFromDetailPage = false
                val intent = Intent(
                    mContext,
                    PaintActivity::class.java
                )
                if (_object.canvasColor?.isNotEmpty() == true) {
                    intent.putExtra("canvas_color", _object.canvasColor)
                }
                val swatches = _object.colorSwatch
                val gson = Gson()
                val swatchesJson = gson.toJson(swatches)
                intent.putExtra("swatches", swatchesJson)
                intent.putExtra("id", _object.id)
                val youtubeLink = _object.youtubeLink
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
                        mContext,
                        "Stroke Event File Not Downloaded Properly",
                        Toast.LENGTH_SHORT
                    ).show()
                }

//                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                mContext.startActivity(intent)
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception " + e.message)
            }
        }
    }

    internal class DownloadOverlayImage(
        var traceImageLink: String,
        var fileName: String,
        val mContext: Context,
        var _object: Tutorial?
    ) :
        AsyncTask<Void, Void, java.util.ArrayList<String>>() {
        var progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(mContext)
            progressDialog?.setMessage(mContext.getResources().getString(R.string.please_wait))
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setCancelable(false)
            progressDialog?.show()
        }

        protected override fun doInBackground(vararg strings: Void): java.util.ArrayList<String> {
            val filesList = downloadTextFiles()
            val file = File(
                KGlobal.getTraceImageFolderPath(mContext),
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
                    File(KGlobal.getTraceImageFolderPath(mContext)) //Creates app specific folder
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

        protected override fun onPostExecute(lst_main: java.util.ArrayList<String>) {
            super.onPostExecute(lst_main)
            try {
                progressDialog?.dismiss()
                //                if (_object.getPost_title() != null)
//                    FirebaseUtils.logEvents(GalleryDashboard.this, "Try " + _object.getPost_title());
                StringConstants.IsFromDetailPage = false
                val intent = Intent(
                    mContext,
                    PaintActivity::class.java
                )
                if (_object?.canvasColor?.isNotEmpty() == true) {
                    intent.putExtra("canvas_color", _object!!.canvasColor)
                }
                val swatches = _object?.colorSwatch
                val gson = Gson()
                val swatchesJson = gson.toJson(swatches)
                intent.putExtra("swatches", swatchesJson)
                intent.putExtra("id", _object!!.id)
                val youtubeLink = _object!!.youtubeLink
                if (youtubeLink != null) {
                    val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    intent.putExtra("youtube_video_id", _youtube_id)
                }
                intent.action = "YOUTUBE_TUTORIAL_WITH_OVERLAID"
                if (lst_main.size == 2) {
                    intent.putExtra("StrokeFilePath", lst_main[0])
                    intent.putExtra("EventFilePath", lst_main[1])
                }
                intent.putExtra(
                    "OverlaidImagePath", File(
                        KGlobal.getTraceImageFolderPath(mContext),
                        fileName
                    ).absolutePath
                )
                mContext.startActivity(intent)
                Log.e(
                    "TAGGG",
                    "Overlay Downloaded File traceImageLink " + traceImageLink + " fileName " + fileName + " full path " + lst_main.size
                )
            } catch (e: java.lang.Exception) {
                Log.e("TAGGG", "Exception at post $e")
            }
        }

        fun downloadTextFiles(): java.util.ArrayList<String> {
            val lst_fileNames = java.util.ArrayList<String>()
            val file1 = File(KGlobal.getStrokeEventFolderPath(mContext))
            if (!file1.exists()) {
                file1.mkdirs()
            }
            for (i in 0..1) {
                val textFileLink: String =
                    _object!!.videosAndFiles?.get(i)?.textFile?.url.toString()
                val fileName: String =
                    _object!!.videosAndFiles?.get(i)?.textFile?.filename.toString()
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

    private fun searchFromEditText() {
        val tutorialID: String = binding.edtHashSearch.text.toString()
        if (!TextUtils.isEmpty(tutorialID)) {
            searchedSubCatId = null
            AppUtils.setSearchResponse(null)
            AppUtils.hideKeyboard(this)
            getCategoryDetailFromAPI(BuildConfig.CAT_ID, tutorialID)
        }
    }

    fun getCategoryDetailFromAPI(catID: String?, search: String) {
        val apiInterface = ApiClient.getRetroClient().create(ApiInterface::class.java)
        val call: Call<String> = apiInterface.searchContent(ApiClient.SECRET_KEY, search)
        progressDialog = ProgressDialog(mContext)
        progressDialog!!.setTitle(resources.getString(R.string.please_wait))
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

        try {
            call.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        if (progressDialog != null) {
                            if (progressDialog!!.isShowing && !this@SearchActivity.isDestroyed) {
                                progressDialog!!.dismiss()
                            }
                        }

                        if (FirebaseAuth.getInstance().currentUser != null) {
                            getSearchedContent(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .addOnSuccessListener { documentSnapshot ->
                                    var list: MutableList<String> =
                                        documentSnapshot["content"] as MutableList<String>
                                    if (list != null) {
                                        if (!list.contains(search)) {
                                            list.add(search)
                                            val map: HashMap<String, List<String>> = HashMap()
                                            map["content"] = list
                                            saveSearchedContent(
                                                map,
                                                FirebaseAuth.getInstance().currentUser!!.uid
                                            )
                                                .addOnSuccessListener {
                                                    Log.e(
                                                        "SaveSearch",
                                                        "onSuccess"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e(
                                                        "SaveSearch",
                                                        e.message!!
                                                    )
                                                }
                                        }
                                    } else {
                                        list = java.util.ArrayList()
                                        list.add(search)
                                        val map: HashMap<String, List<String>> = HashMap()
                                        map["content"] = list
                                        saveSearchedContent(
                                            map,
                                            FirebaseAuth.getInstance().currentUser!!.uid
                                        )
                                            .addOnSuccessListener {
                                                Log.e(
                                                    "SaveSearch",
                                                    "onSuccess"
                                                )
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(
                                                    "SaveSearch",
                                                    e.message!!
                                                )
                                            }
                                    }
                                }
                                .addOnFailureListener { }
                        }

                        if (response != null && response.body() != null) {
                            Log.e("TAGGG", "Response Data " + response.body())
                            parseResponseManually(search, response.body()!!)
                        } else {
                            if (this@SearchActivity.isDestroyed) { // or call isFinishing() if min sdk version < 17
                                return
                            }
                            showToast("Failed To Load")
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("TAGG", "Exception " + e.message)
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    showToast("Failed To Retrieve Content!")
                }
            })
        } catch (e: java.lang.Exception) {
            if (progressDialog != null) {
                if (progressDialog!!.isShowing && !this@SearchActivity.isDestroyed) {
                    progressDialog!!.dismiss()
                }
            }
            Log.e("TAGGG", "Exception at callAPI " + e.message + " " + e.toString())
        }
    }

//    private fun parseResponseManually(response: String) {
//        try {
//            if (!TextUtils.isEmpty(response)) {
//                if (response.contains("\"data\":{\"status\"")) {
//                    val gson = Gson()
//                    val postDetailResponse = gson.fromJson(
//                        response,
//                        PostDetailResponse::class.java
//                    )
//                    if (postDetailResponse != null) {
//                        val status = postDetailResponse.data.status
//                        if (status.equals(
//                                "Authentication failed. Post Id is missing.",
//                                ignoreCase = true
//                            ) ||
//                            status.equals("No Tutorial exists with this Post Id", ignoreCase = true)
//                        ) {
//                            val builder = AlertDialog.Builder(this@SearchActivity)
//                            builder.setMessage("Tutorial not found. Please try again")
//                                .setPositiveButton(
//                                    "OK"
//                                ) { dialogInterface, i -> dialogInterface.dismiss() }
//                                .show()
//                            return
//                        }
//                    }
//                } else {
//                    Log.e("API Response", "Response$response")
//                }
//            }
//        } catch (e: JsonSyntaxException) {
//            e.printStackTrace()
//        }
//
//        if (response.startsWith("{\"tutorials\"")) {
//            initData(response);
//        }
//    }

    fun parseResponseManually(search: String, response: String) {
        var response = response
        try {
            try {
                if (!TextUtils.isEmpty(response)) {
                    if (response.contains("\"data\":{\"status\"")) {
                        val gson = Gson()
                        val postDetailResponse = gson.fromJson(
                            response,
                            PostDetailResponse::class.java
                        )
                        if (postDetailResponse != null) {
                            val status = postDetailResponse.data.status
                            if (status.equals(
                                    "Authentication failed. Post Id is missing.",
                                    ignoreCase = true
                                ) ||
                                status.equals(
                                    "No Tutorial exists with this Post Id",
                                    ignoreCase = true
                                )
                            ) {
                                val builder = AlertDialog.Builder(this@SearchActivity)
                                builder.setMessage("Tutorial not found. Please try again")
                                    .setPositiveButton(
                                        "OK"
                                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                                    .show()
                                return
                            }
                        }
                    } else {
                        Log.e("API Response", "Response$response")
                    }
                }
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@SearchActivity,
                        "Json Catch Block: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(this@SearchActivity, "Parse enter", Toast.LENGTH_SHORT).show()
            }
            var searchResponse: SearchResponse? = null
            try {
                searchResponse = Gson().fromJson(
                    response,
                    SearchResponse::class.java
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@SearchActivity,
                        "GSON Catch Block: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Log.e("API Response", "searchResponse$response")
            if (searchResponse != null) {
                val isNumberSearch = searchResponse.data!!.isNumberSearch
                val searchType = searchResponse.data!!.searchedNumberIs
                val searchStatus = searchResponse.data!!.searchResponse
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        this@SearchActivity,
                        "isNumberSearch: $isNumberSearch", Toast.LENGTH_SHORT
                    ).show()
                }
                Toast.makeText(this@SearchActivity, "" + searchStatus, Toast.LENGTH_SHORT).show()
                if (isNumberSearch) {
                    if (progressDialog != null) {
                        if (progressDialog!!.isShowing && !this@SearchActivity.isDestroyed) {
                            progressDialog!!.dismiss()
                        }
                    }
                    val gson = Gson()
                    when (searchType) {
                        "tutorial" -> {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "Tutorial searched by id.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            val list = searchResponse.data!!.tutorials
                            val tutorial = list!![0]
                            handleNumberSearch(tutorial)
                        }

                        "category" -> {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "Category searched by id.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            val intent = Intent(
                                this,
                                NewSubCategoryActivity::class.java
                            )
                            intent.putExtra("sub_cat_id", searchedSubCatId)
                            intent.putExtra("cate_id", search)
                            intent.putExtra("level", userLevel)
                            intent.putExtra("childs", gson.toJson(searchResponse.data!!.childs))
                            intent.putExtra("total_tutorials", searchResponse.data!!.count)
                            intent.putExtra("cate_name", searchResponse.data!!.name)
                            startActivity(intent)
                        }

                        "subcategory" -> {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "Subcategory searched by id.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            searchedSubCatId = search
                            getCategoryDetailFromAPI(
                                BuildConfig.CAT_ID,
                                searchResponse.data!!.parentCategoryId.toString()
                            )
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(
                            this@SearchActivity,
                            "You searched text.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (progressDialog != null) {
                        if (progressDialog!!.isShowing && !this@SearchActivity.isDestroyed) {
                            progressDialog!!.dismiss()
                        }
                    }
                    val gson = Gson()
                    response = gson.toJson(searchResponse.data)
                    AppUtils.setSearchResponse(response)

                    val intent = Intent(
                        this@SearchActivity,
                        SearchActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    intent.putExtra("search_response", response);
                    //                    intent.putExtra("search_response", response);
                    intent.putExtra("has_search_response", true)
                    intent.putExtra("search", search)
                    startActivity(intent)

//                    val intent = intent
//                    intent.putExtra("has_search_response", true)
//                    intent.putExtra("search", search)
//                    getDataFromIntent()
                }
            }

        } catch (e: java.lang.Exception) {
            if (progressDialog != null) {
                if (progressDialog!!.isShowing && !this@SearchActivity.isDestroyed) {
                    progressDialog!!.dismiss()
                }
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@SearchActivity,
                    "Parse Main Catch Block: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.e("TAGGG", "Exception at parse " + e.message + " " + e.stackTrace.toString())
        }
    }

    private fun handleNumberSearch(tutorial: Tutorial) {
        if (tutorial != null) {
            if (tutorial.videosAndFiles != null && tutorial.videosAndFiles!!.size >= 2 && tutorial.videosAndFiles!![0].textFile != null && tutorial.videosAndFiles!![1]
                    .textFile != null && tutorial.youtubeLink != null && tutorial.youtubeLink!!.isNotEmpty()
            ) {
                if (tutorial.videosAndFiles!![0]
                        .overlayImage != null || tutorial.videosAndFiles!![1]
                        .overlayImage != null
                ) {
                    tutorial_type = Tutorial_Type.Strokes_Overlaid_Window
                } else if (tutorial.videosAndFiles!![0]
                        .traceImage == null || tutorial.videosAndFiles!![1]
                        .traceImage == null
                ) {
                    tutorial_type = Tutorial_Type.Strokes_Window
                } else {
                    tutorial_type = Tutorial_Type.Strokes_Window
                }
            } else if (tutorial.videosAndFiles != null && !tutorial.videosAndFiles!!.isEmpty() && tutorial.videosAndFiles!![0]
                    .traceImage != null && tutorial.youtubeLink != null && !tutorial.youtubeLink!!.isEmpty()
            ) {
                tutorial_type = Tutorial_Type.Video_Tutorial_Trace
            } else if (tutorial.videosAndFiles != null && !tutorial.videosAndFiles!!.isEmpty() && tutorial.videosAndFiles!![0]
                    .overlayImage != null && tutorial.youtubeLink != null && !tutorial.youtubeLink!!
                    .isEmpty()
            ) {
                tutorial_type = Tutorial_Type.Video_Tutorial_Overraid
            } else if (tutorial.videosAndFiles != null && !tutorial.videosAndFiles!!.isEmpty() && tutorial.videosAndFiles!![0].overlayImage != null && tutorial.youtubeLink!!.isEmpty()) {
                tutorial_type = Tutorial_Type.DO_DRAWING_OVERLAY
            } else if (tutorial.videosAndFiles != null && !tutorial.videosAndFiles!!.isEmpty() && tutorial.videosAndFiles!![0].traceImage != null && tutorial.youtubeLink!!.isEmpty()) {
                tutorial_type = Tutorial_Type.DO_DRAWING_TRACE
            } else if (tutorial.youtubeLink != null && !tutorial.youtubeLink!!.isEmpty()) {
                tutorial_type = Tutorial_Type.See_Video
            } else {
                tutorial_type = Tutorial_Type.READ_POST_DEFAULT
            }
            processTutorial(tutorial_type!!, tutorial)
        }
    }

    fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}