package com.paintology.lite.photorealistic.drawing.Activity.search_activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.GridLayoutManagerWrapper
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.SearchViewViewModel
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter.SearchResultAdapter
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.interface_event.OnSearchResultClicks
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.databinding.FragmentUserPostBinding
import kotlinx.coroutines.launch


class UserPostFragment : Fragment() ,OnSearchResultClicks {

    lateinit var binding: FragmentUserPostBinding
    private var searchResultAdapter: SearchResultAdapter? = null
    private val viewModel by activityViewModels<SearchViewViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        collectState()
    }

    private fun initRecyclerView() {
        searchResultAdapter = SearchResultAdapter(this)
        binding.rvUserPosts.apply {
            layoutManager = GridLayoutManagerWrapper(context, 2)
            adapter = searchResultAdapter
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blogPosts.collect {
                    searchResultAdapter?.submitList(it)
                }
            }
        }
    }


    override fun onMenuClick(model: SearchResultModel, position: Int) {
        context?.showToast(position.toString())
    }
}
