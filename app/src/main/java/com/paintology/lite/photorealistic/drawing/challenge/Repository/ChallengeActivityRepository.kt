package com.paintology.lite.photorealistic.drawing.challenge.Repository

import com.google.firebase.firestore.FirebaseFirestore
import com.paintology.lite.photorealistic.drawing.challenge.TutorialChallengeMode


class ChallengeActivityRepository {



    var db = FirebaseFirestore.getInstance()
    private  var instance: ChallengeActivityRepository? =null
    private val dataSet: ArrayList<TutorialChallengeMode> = ArrayList<TutorialChallengeMode>()

    fun getInstance(): ChallengeActivityRepository {
        if (instance == null) {
            instance = ChallengeActivityRepository()
        }
        return instance as ChallengeActivityRepository
    }

    //pertend to get data from a webservices or online source
//    fun getTutorialChallengeList(): MutableLiveData<List<TutorialChallengeMode>> {
//        setTutorialChallengeData()
//        val data: MutableLiveData<List<TutorialChallengeMode>> = MutableLiveData()
//        data.setValue(dataSet)
//        return data
//
//    }
//
//    private fun setTutorialChallengeData() {
////        dataSet.add(
////            nicePlace(
////                "Bangladesh",
////                "https://th.bing.com/th/id/OIP.V0ZTog9uqSiLHsFvXpYRsgHaE7?pid=ImgDet&rs=1"
////            )
////        )
//
//
//        db.collection("gamifications_staging")
//            .document("challenge")
//            .collection("rules")
//            .whereEqualTo("active", true)
//           // .whereEqualTo("type", "Tutorial")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                   // Log.d("TAG-Firebase", "${document.id} => ${document.data}")
//                    val imagesList = document.data["images"] as? Map<*, *> ?: emptyMap<Any, Any>()
//                    val images = imagesList.get("banner").toString()
//                    val cost = document.getLong("cost") ?: 0
//                    val active = document.getBoolean("active") ?: false
//                    val createdAt = document.getTimestamp("created_at")
//                    val description = document.getString("description") ?: ""
//                    val type = document.getString("type") ?: ""
//                    val title = document.getString("title") ?: ""
//                    val points = document.getLong("points") ?: 0
//                    val tutorialId = document.getString("tutorial_id") ?: ""
//                    val difficulty = document.getString("difficulty") ?: ""
//                    val updatedAt = document.getTimestamp("updated_at")
//                    val xp = document.getLong("xp") ?: 0
//                    val limit = document.getString("limit") ?: ""
//                    val key = document.getString("key") ?: ""
//
//                    Log.d("TAG-Firebase", "Images: $images")
//                    Log.d("TAG-Firebase", "Cost: $cost")
//                    Log.d("TAG-Firebase", "Active: $active")
//                    Log.d("TAG-Firebase", "Created At: $createdAt")
//                    Log.d("TAG-Firebase", "Description: $description")
//                    Log.d("TAG-Firebase", "Type: $type")
//                    Log.d("TAG-Firebase", "Title: $title")
//                    Log.d("TAG-Firebase", "Points: $points")
//                    Log.d("TAG-Firebase", "Tutorial ID: $tutorialId")
//                    Log.d("TAG-Firebase", "Difficulty: $difficulty")
//                    Log.d("TAG-Firebase", "Updated At: $updatedAt")
//                    Log.d("TAG-Firebase", "XP: $xp")
//                    Log.d("TAG-Firebase", "Limit: $limit")
//                    Log.d("TAG-Firebase", "Key: $key")
//
//                    dataSet.add(
//                        TutorialChallengeMode(images,cost,active,createdAt,description,type,title,points,
//                        tutorialId,difficulty,updatedAt,xp,limit,key)
//                    )
//
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("TAG-Firebase", "Error getting documents: ", exception)
//            }
//
//
//
//    }


}