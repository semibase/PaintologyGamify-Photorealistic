package com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments

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
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.GridLayoutManagerWrapper
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.SearchViewViewModel
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter.SearchResultAdapter
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.interface_event.OnSearchResultClicks
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils
import com.paintology.lite.photorealistic.drawing.Model.firebase.Post
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.FragmentTutorialsBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import kotlinx.coroutines.launch


class TutorialsFragment : Fragment(), OnSearchResultClicks {
    lateinit var binding: FragmentTutorialsBinding
    private var searchResultAdapter: SearchResultAdapter? = null
    private val viewModel by activityViewModels<SearchViewViewModel>()

    private val _tutorPosts = mutableListOf<SearchResultModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTutorialsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        collectState()
    }

    private fun initRecyclerView() {
        searchResultAdapter = SearchResultAdapter(this)
        binding.rvTutorials.apply {
            layoutManager = GridLayoutManagerWrapper(context, 2)
            adapter = searchResultAdapter
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tutorPosts.collect {
                    _tutorPosts.clear()
                    if (it.isNotEmpty() && it.size >= 4) {
                        searchResultAdapter?.submitList(it)
                    } else {
                        _tutorPosts.addAll(it)
                        getRecentTutorials()
                    }
                }
            }
        }
    }

    fun getRecentTutorials() {
        val db = FirebaseFirestore.getInstance()
        val tutorialsRef = db.collection("tutorials")
            .limit(4 - _tutorPosts.size.toLong())
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
                _tutorPosts.addAll(tutorialResult)
                searchResultAdapter?.submitList(_tutorPosts)
            }
        }.addOnFailureListener {
            if (_tutorPosts.size > 0)
                searchResultAdapter?.submitList(_tutorPosts)
        }
    }

    override fun onMenuClick(model: SearchResultModel, position: Int) {
        model.id?.let {
            FireUtils.showProgressDialog(context, getString(R.string.ss_loading_tutorials))
            TutorialUtils(context).parseTutorial(model.id)
        }
    }
}



