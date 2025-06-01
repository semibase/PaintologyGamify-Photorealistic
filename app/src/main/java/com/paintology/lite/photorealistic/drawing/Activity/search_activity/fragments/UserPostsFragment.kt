package com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.firestore.FirebaseFirestore
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.CommunityPostNotification
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.GridLayoutManagerWrapper
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.SearchViewViewModel
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter.SearchResultAdapter
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.interface_event.OnSearchResultClicks
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail
import com.paintology.lite.photorealistic.drawing.databinding.FragmentPostsBinding
import kotlinx.coroutines.launch

class UserPostsFragment : Fragment(), OnSearchResultClicks {
    lateinit var binding: FragmentPostsBinding
    private var searchResultAdapter: SearchResultAdapter? = null
    private val viewModel by activityViewModels<SearchViewViewModel>()
    private val _userPosts = mutableListOf<SearchResultModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        collectState()
    }

    private fun initRecyclerView() {
        searchResultAdapter = SearchResultAdapter(this)
        binding.rvCommunityPost.apply {
            layoutManager = GridLayoutManagerWrapper(context, 2)
            adapter = searchResultAdapter
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userPosts.collect {
                    _userPosts.clear()
                    if (it.isNotEmpty() && it.size >= 4) {
                        searchResultAdapter?.submitList(it)
                    } else {
                        _userPosts.addAll(it)
                        getRecentPosts()
                    }
                }
            }
        }
    }

    private fun getRecentPosts() {
        val db = FirebaseFirestore.getInstance()
        val tutorialsRef = db.collection("community_posts")
            .limit(4 - _userPosts.size.toLong())
            .get()

        tutorialsRef.addOnSuccessListener { snapshot ->
            if (snapshot != null && !snapshot.isEmpty) {
                val notificationList = mutableListOf<CommunityPostNotification>()
                for (doc in snapshot.documents) {
                    val notification = doc.toObject(CommunityPostNotification::class.java)
                    notification?.let { model ->
                        model.id = doc.id
                        notificationList.add(model)
                    }
                }
                val tutorialResult = notificationList.map {
                    SearchResultModel(
                        id = it.id,
                        img = it.images?.contentResized,
                        title = it.title,
                        content = it.description,
                        tutorialNo = "#${it.id}",
                        rating = it.statistic?.likes?.toFloat(), // filled later
                        peopleWatched = it.statistic?.views, //Filled later
                        traceImageLink = it.images?.content,
                        fileName = it.title,
                    )
                }
                _userPosts.addAll(tutorialResult)
                searchResultAdapter?.submitList(_userPosts)
            }
        }.addOnFailureListener {
            if (_userPosts.size > 0)
                searchResultAdapter?.submitList(_userPosts)
        }
    }

    override fun onMenuClick(model: SearchResultModel, position: Int) {
        context?.showToast("click")
        goToBlogPost(model)
    }

    private fun goToBlogPost(model: SearchResultModel) {
        val intent = Intent(
            requireContext(),
            CommunityDetail::class.java
        )
        intent.putExtra("post_id", model.id)
        startActivity(intent)
    }
}
