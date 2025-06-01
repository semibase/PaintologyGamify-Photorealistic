package com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.GridLayoutManagerWrapper
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.SearchViewViewModel
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter.SearchResultAdapter
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.interface_event.OnSearchResultClicks
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import com.paintology.lite.photorealistic.drawing.Model.firebase.Post
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.databinding.FragmentBlogPostsBinding
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard.longLog
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import kotlinx.coroutines.launch

class BlogPostFragment : Fragment(), OnSearchResultClicks {
    lateinit var binding: FragmentBlogPostsBinding
    private var searchResultAdapter: SearchResultAdapter? = null
    private val viewModel by activityViewModels<SearchViewViewModel>()
    private val _blogPosts = mutableListOf<SearchResultModel>()

    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBlogPostsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initProgressBar()

        collectState()
    }

    private fun initProgressBar() {
        progressDialog = ProgressDialog(this@BlogPostFragment.requireContext())

        progressDialog?.setTitle(resources.getString(R.string.please_wait))
        progressDialog?.setMessage("Loading Tutorials...")
        progressDialog?.setCanceledOnTouchOutside(false)
    }

    private fun initRecyclerView() {
        searchResultAdapter = SearchResultAdapter(this)
        binding.rvBlogPosts.apply {
            layoutManager = GridLayoutManagerWrapper(context, 2)
            adapter = searchResultAdapter
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blogPosts.collect {
                    _blogPosts.clear()
                    if (it.isNotEmpty() && it.size >= 4) {
                        searchResultAdapter?.submitList(it)
                    } else {
                        _blogPosts.addAll(it)
                        getRecentBlogs()
                    }
                }
            }
        }
    }

    private fun getRecentBlogs() {
        val db = FirebaseFirestore.getInstance()
        val tutorialsRef = db.collection("posts")
            .limit(4 - _blogPosts.size.toLong())
            .get()

        tutorialsRef.addOnSuccessListener { snapshot ->
            if (snapshot != null && !snapshot.isEmpty) {
                val notificationList = mutableListOf<Post>()
                for (doc in snapshot.documents) {
                    val notification = doc.toObject(Post::class.java)
                    notification?.let { model ->
                        model.id = doc.id
                        notificationList.add(model)
                    }
                }
                val tutorialResult = notificationList.map {
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
                _blogPosts.addAll(tutorialResult)
                searchResultAdapter?.submitList(_blogPosts)
            }
        }.addOnFailureListener {
            if (_blogPosts.size > 0)
                searchResultAdapter?.submitList(_blogPosts)
        }
    }

    override fun onMenuClick(model: SearchResultModel, position: Int) {
        Log.d("id", model.id.toString())
        model.id?.let {
            progressDialog?.show()
            FirebaseFirestoreApi
                .getPostDetail(model.id)
                .addOnCompleteListener {
                    progressDialog?.dismiss()

                    if (it.isSuccessful) {
                        longLog("${it.result.data}")
                        val responseData = it.result.data as Map<String, Any>
                        val gson = GsonBuilder().create()
                        val json = gson.toJson(responseData)

                        val post = gson.fromJson(
                            json,
                            Post::class.java
                        )

                        goToBlogPost(post)
                    }
                }
        }
    }

    private fun goToBlogPost(post: Post) {
        post.ref?.let {
            KGlobal.openInBrowser(requireContext(), it)
        }
    }
}
