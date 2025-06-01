package com.paintology.lite.photorealistic.drawing.Activity.your_ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.LeaderBoardRankingModel

class RankingViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _topUsers = MutableLiveData<List<LeaderBoardRankingModel>>()
    val topUsers: LiveData<List<LeaderBoardRankingModel>> get() = _topUsers

    private val levels = listOf("Expert", "Advanced 3", "Advanced 2", "Advanced 1", "Intermediate 3", "Intermediate 2", "Intermediate 1", "Beginner 3", "Beginner 2", "Beginner 1")

    fun fetchTopUsers(level: String? = null,countryCode: String? = null) {
        fetchTopUsersRecursive(level,countryCode, 0, mutableListOf())
    }

    private fun fetchTopUsersRecursive(level: String?,countryCode: String?, levelIndex: Int, topUsers: MutableList<LeaderBoardRankingModel>) {
        if (levelIndex >= levels.size || topUsers.size >= 100) {
            _topUsers.value = topUsers
            return
        }

        val currentLevel = level ?: levels[levelIndex]

        var query = db.collection("users")
            .whereEqualTo("level", currentLevel)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(100 - topUsers.size.toLong())

        if (countryCode != null) {
            query = query.whereEqualTo("country", countryCode)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {

                        val name = document.getString("name")
                        val avatar = document.getString("avatar")
                        val points: Number? = document.getLong("points")
                        val level = document.getString("level")
                        val externalId = document.getString("external_id")

                        var pts = 0

                        if (points != null) {
                            pts = points.toInt()
                        }

                        val user = LeaderBoardRankingModel(
                            document.id,
                            externalId ?: "",
                            avatar ?: "default_avatar_url",  // Handle null avatar
                            name ?: "Unknown",  // Handle null name
                            0,  // Static awards count
                            pts,  // Points,
                            level
                        )

                        topUsers.add(user)
                    }
                }
                if (topUsers.size < 100 && level == null) {
                    fetchTopUsersRecursive(level,countryCode, levelIndex + 1, topUsers)
                } else {
                    _topUsers.value = topUsers
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
                _topUsers.value = topUsers
            }
    }


}