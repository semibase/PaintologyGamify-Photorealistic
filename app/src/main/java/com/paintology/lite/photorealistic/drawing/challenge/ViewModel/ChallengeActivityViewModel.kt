package com.paintology.lite.photorealistic.drawing.challenge.ViewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paintology.lite.photorealistic.drawing.challenge.TutorialChallengeMode
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.getChallengeComments

class ChallengeActivityViewModel : ViewModel() {


    private var _tutorialChallengeList = MutableLiveData<List<TutorialChallengeMode>>()
    val tutorialChallengeList: LiveData<List<TutorialChallengeMode>>
        get() = _tutorialChallengeList

    private var _difficultyLevels = MutableLiveData<List<String>>()
    val difficultyLevels: LiveData<List<String>>
        get() = _difficultyLevels


    fun getChallengeList(userCurrentLevel: String, onComments: (Int) -> Unit) {
        FirebaseFirestoreApi.getChallengesByLevel(userCurrentLevel).addOnSuccessListener {
            val challenges = it.toObjects(TutorialChallengeMode::class.java)
            _tutorialChallengeList.value = challenges
            repeat(challenges.size) { index ->
                if (challenges[index].statistic.comments > 0) {
                    getChallengeComments(challenges[index].key ?: "") { comments ->
                        _tutorialChallengeList.value?.get(index)?.comments = comments
                        onComments(index)
                    }
                }
                if (challenges[index].statistic.likes > 0) {
                    FirebaseFirestoreApi.getLikeOnChallenge(challenges[index].key ?: ""){
                        _tutorialChallengeList.value?.get(index)?.likes = it
                    }
                }
            }
        }
            .addOnFailureListener {
                Log.e("getChallengeList", "${it.message}")
            }
    }

    fun getDifficultyLevels() {
        FirebaseFirestoreApi.getDifficultyLevels()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val list: MutableList<String> = ArrayList()
                    val l = it.result.get("difficulties") as ArrayList<String>


                    _difficultyLevels.value = l
                }
            }
            .addOnFailureListener {
                Log.e("getDifficultyLevels", "${it.message}")
            }
    }


}