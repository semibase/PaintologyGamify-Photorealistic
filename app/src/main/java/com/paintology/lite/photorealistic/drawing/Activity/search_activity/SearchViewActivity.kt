package com.paintology.lite.photorealistic.drawing.Activity.search_activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter.SearchViewPagerAdapter
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.PostType
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Model.CommunityPost
import com.paintology.lite.photorealistic.drawing.Model.firebase.Post
import com.paintology.lite.photorealistic.drawing.Model.firebase.SearchContentResponse
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySearchViewBinding
import com.paintology.lite.photorealistic.drawing.util.AppUtils

class SearchViewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySearchViewBinding.inflate(layoutInflater)
    }


    private var adapter: SearchViewPagerAdapter? = null

    private val viewModel by viewModels<SearchViewViewModel>()

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getDataFromIntent()
        initViewPagerAdepter()
        initToolbar()

        binding.edtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->

            Toast.makeText(this, "search", Toast.LENGTH_SHORT).show()
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.edtSearch.text
                        .toString()
                        .isNotEmpty()
                ) {
                    binding.edtSearch.hideKeyboard(this)
                    search(binding.edtSearch.text.toString())
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.imgSearch.onSingleClick {
            if (binding.edtSearch.text
                    .toString()
                    .isNotEmpty()
            ) {
                binding.edtSearch.hideKeyboard(this)
                search(binding.edtSearch.text.toString())
            }
        }
    }

    fun View.hideKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun getDataFromIntent() {
        val hasSearchResponse = intent.getBooleanExtra("has_search_response", false)

        var response: String? = null
        if (hasSearchResponse) {
            response = AppUtils.getSearchResponse()
        }

        val search = intent.getStringExtra("search")
        val searchKeyword = intent.getStringExtra("searchKeyword")

        if (response != null) {
            initData(response)
        } else {
            showLoading()

        }

        if (!TextUtils.isEmpty(searchKeyword)) {
            binding.edtSearch.setText(searchKeyword)
        }
    }

    private fun initData(data: String) {
        val gson = Gson()
        // Convert the JSON string back to a SearchContentResponse object
        val searchContentResponse: SearchContentResponse = gson.fromJson(data, SearchContentResponse::class.java)

        // Now you can use the searchContentResponse object
        println(searchContentResponse)
        setupUserPosts(searchContentResponse.community_posts)
        setUpBlogPosts(searchContentResponse.posts)
        setUpTutorialPosts(searchContentResponse.tutorials)
    }

    private fun setupUserPosts(communityPost: List<CommunityPost>) {
        val tutorialResult = communityPost.map {
            SearchResultModel(
                id = it.id,
                img = it.images.content_resized,
                title = it.title,
                content = it.description,
                tutorialNo = "#${it.id}",
                rating = it.statistic.likes.toFloat(), // filled later
                peopleWatched = it.statistic.views, //Filled later
                traceImageLink = it.images.content,
                fileName = it.title,
            )
        }
        viewModel.setPostValues(tutorialResult, PostType.COMMUNITY)
    }

    private fun setUpBlogPosts(post: List<Post>) {
        val tutorialResult = post.map {
            SearchResultModel(
                id = it.id,
                img = it.images?.thumbnail,
                title = it.title,
                content = it.content,
                tutorialNo = "#${it.id}",
                rating = 0f, // filled later
                peopleWatched = 0, //Filled later
                traceImageLink = it.images?.thumbnail,
                fileName = it.id,
            )
        }
        viewModel.setPostValues(tutorialResult, PostType.POST)
    }

    private fun setUpTutorialPosts(tutorsPost: List<Post>) {
        val tutorialResult = tutorsPost.map {
            SearchResultModel(
                id = it.id,
                img = it.images?.thumbnail,
                title = it.title,
                content = it.content,
                tutorialNo = "#${it.id}",
                rating = 0f, // filled later
                peopleWatched = 0, //Filled later
                traceImageLink = it.images?.thumbnail,
                fileName = it.title,
            )
        }
        viewModel.setPostValues(tutorialResult, PostType.TUTORS)
    }


    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.pain_search)
            imgFav.hide()
        }
    }


    private fun initViewPagerAdepter() {
        adapter = SearchViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            // Set tab text or custom view based on position
            when (position) {
                0 -> tab.text = getString(R.string.tutorials)
                1 -> tab.text = getString(R.string.user_post)
                2 -> tab.text = getString(R.string.blog_posts)
            }
        }.attach()
    }

    private fun search(search: String) {
        var endPoint = ""
        val data: MutableMap<String, Any> = HashMap()
        val isNumberSearch = search.matches("\\d+".toRegex())

        if (isNumberSearch) {
            data["documentId"] = search
            data["searchType"] = "tutorials"
            endPoint = "search-id"
        } else {
            data["q"] = search
            data["sort_by"] = "created_at:desc"
            data["page"] = 1
            data["per_page"] = 5

            endPoint = "search-contents"
        }
        showLoading()
        FirebaseFunctions
            .getInstance()
            .getHttpsCallable(endPoint)
            .call(data)
            .addOnCompleteListener { result ->
                hideLoading()
                if (result.isSuccessful) {
                    val responseData = result.result.data as Map<*, *>

                    val gson = GsonBuilder().create()
                    val json = gson.toJson(responseData)

                    initData(json)
                }
            }
    }

    private fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle(resources.getString(R.string.please_wait))
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }
}
