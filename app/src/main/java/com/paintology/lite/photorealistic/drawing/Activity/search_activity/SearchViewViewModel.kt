package com.paintology.lite.photorealistic.drawing.Activity.search_activity

import androidx.lifecycle.ViewModel
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.PostType
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewViewModel : ViewModel() {

    private val _tutorPosts = MutableStateFlow(listOf<SearchResultModel>())
    val tutorPosts = _tutorPosts.asStateFlow()

    private val _blogPosts = MutableStateFlow(listOf<SearchResultModel>())
    val blogPosts = _blogPosts.asStateFlow()

    private val _userPosts = MutableStateFlow(listOf<SearchResultModel>())
    val userPosts = _userPosts.asStateFlow()

    private fun getTutorialList(type: PostType) : MutableStateFlow<List<SearchResultModel>> {
        return when (type) {
            PostType.TUTORS -> _tutorPosts
            PostType.POST -> _blogPosts
            PostType.COMMUNITY -> _userPosts
        }
    }

    fun setPostValues(value: List<SearchResultModel>, type: PostType) {
        getTutorialList(type).value = value
    }
}
