package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.Drawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.FacetCount
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.model_class.UserFacetCount
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.model_class.UserFacetResponse
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.util.events.DrawingsChangeEvent
import org.greenrobot.eventbus.EventBus

class DrawingViewModel : ViewModel() {

    // get new cloud function data
    private val _allDrawings = MutableLiveData<List<NewDrawing>>()
    val allDrawings: LiveData<List<NewDrawing>> get() = _allDrawings

    private val _freehandDrawings = MutableLiveData<List<NewDrawing>>()
    val freehandDrawings: LiveData<List<NewDrawing>> get() = _freehandDrawings

    private val _tutorialDrawings = MutableLiveData<List<NewDrawing>>()
    val tutorialDrawings: LiveData<List<NewDrawing>> get() = _tutorialDrawings


    init {
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }

    private val _drawingList = MutableLiveData<List<FacetCount>>()
    val drawingList: LiveData<List<FacetCount>>
        get() = _drawingList


    private val _userDrawingsList = MutableLiveData<Drawing>()
    val userDrawingsList: LiveData<Drawing>
        get() = _userDrawingsList

    private val _countryFreehandList = MutableLiveData<Drawing>()
    val countryFreehandList: LiveData<Drawing>
        get() = _countryFreehandList

    private val _countryTutorialList = MutableLiveData<Drawing>()
    val countryTutorialList: LiveData<Drawing>
        get() = _countryTutorialList


    private val _tagDrawings = MutableLiveData<List<NewDrawing>>()
    val tagDrawings: LiveData<List<NewDrawing>> get() = _tagDrawings

    fun fetchDrawingListWithFacet(
        perPage: Int = 1,
        facetBy: String = "author.level",
        filterBy: String = ""
    ) {
        val data = hashMapOf<String, Any>(
            "per_page" to perPage,
            "facet_by" to facetBy,
            "filter_by" to filterBy
        )

        FirebaseFirestoreApi.fetchDrawingListWithFacet(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<String, Any>
                    result?.let { data ->
                        val gson = Gson()
                        val json = gson.toJson(data)
                        Log.e("2478128975109287", "JSON Response: $json")

                        // Handle the response data as needed
                        val drawingResponse = gson.fromJson(json, Drawing::class.java)
                        _drawingList.postValue(drawingResponse.facetCounts)
                        if (filterBy.contains("freehand")) {
                            _countryFreehandList.postValue(drawingResponse)
                        } else if (filterBy.contains("tutorials")) {
                            _countryTutorialList.postValue(drawingResponse)
                        } else if (filterBy.contains("author.user_id")) {
                            _userDrawingsList.postValue(drawingResponse)
                        }
                    } ?: run {
                        Log.e("2478128975109287", "Error: Result is null")
                    }
                } else {
                    val e = task.exception
                    Log.e("2478128975109287", "Error: ${e?.message}", e)
                    if (e is FirebaseFunctionsException) {
                        Log.e("2478128975109287", "Error details: ${e.details}", e)
                    }
                }
            }
    }


    private val _facetCountsList = MutableLiveData<List<UserFacetCount>>()
    val facetCountsList: LiveData<List<UserFacetCount>> = _facetCountsList

    fun fetchUserListWithFacet(perPage: Int = 1, facetBy: String = "level", countryBy: String? = null) {
        val data = if (countryBy != null) {
            hashMapOf<String, Any>(
                "facet_by" to facetBy,
                "filter_by" to countryBy
            )
        } else {
            hashMapOf<String, Any>(
                "per_page" to perPage,
                "facet_by" to facetBy
            )
        }

        FirebaseFirestoreApi.fetchUserListWithFacet(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<String, Any>
                    result?.let { data ->
                        val gson = Gson()
                        val json = gson.toJson(data)
                        Log.e("fetchUserListWithFacet", "JSON Response: $json")

                        // Handle the response data as needed
                        try {
                            val userFacetResponse =
                                gson.fromJson(json, UserFacetResponse::class.java)
                            Log.e("fetchUserListWithFacet", "Parsed response: $userFacetResponse")

                            // Update Lists.rankingList with the actual counts
                            updateRankingList(userFacetResponse.facetCounts)

                            // Post the updated list to LiveData
                            _facetCountsList.postValue(userFacetResponse.facetCounts)
                            Log.e("fetchUserListWithFacet", "LiveData Updated")
                        } catch (e: Exception) {
                            Log.e("fetchUserListWithFacet", "JSON Parsing Error: ${e.message}", e)
                        }
                    } ?: run {
                        Log.e("fetchUserListWithFacet", "Error: Result is null")
                    }
                } else {
                    val e = task.exception
                    Log.e("fetchUserListWithFacet", "Error: ${e?.message}", e)
                    if (e is FirebaseFunctionsException) {
                        Log.e("fetchUserListWithFacet", "Error details: ${e.details}", e)
                    }
                }
            }
    }

    private fun updateRankingList(facetCounts: List<UserFacetCount>?) {
        // Reset all counts to 0 before updating
        Lists.rankingList.forEach {
            it.tvTotalUsers = "0"
        }

        // Update only those that are found in the response
        facetCounts?.forEach { facetCount ->
            facetCount.counts.forEach { count ->
                Lists.rankingList.find { it.tvRankLevel == count.value }?.apply {
                    tvTotalUsers = count.count.toString()
                }
            }
        }

        // Log the updated ranking list to check results
        Lists.rankingList.forEach { ranking ->
            Log.d("Ranking", "Level: ${ranking.tvRankLevel}, Total Users: ${ranking.tvTotalUsers}")
        }
        /*facetCounts?.forEach { facetCount ->
            facetCount.counts.forEach { count ->
                Lists.rankingList.find { it.tvRankLevel == count.value }?.apply {
                    tvTotalUsers = count.count.toString()
                }
            }
        }*/
    }


    /* fun fetchUserListWithFacet(perPage: Int = 1, facetBy: String = "level") {
         val data = hashMapOf<String, Any>(
             "per_page" to perPage,
             "facet_by" to facetBy
         )

         FirebaseFirestoreApi.fetchUserListWithFacet(data)
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     val result = task.result?.data as? Map<String, Any>
                     result?.let { data ->
                         val gson = Gson()
                         val json = gson.toJson(data)
                         Log.e("fetchUserListWithFacet", "JSON Response: $json")

                         // Handle the response data as needed
                         try {
                             val userFacetResponse = gson.fromJson(json, UserFacetResponse::class.java)
                             Log.e("fetchUserListWithFacet", "Parsed response: $userFacetResponse")
                             _facetCountsList.postValue(userFacetResponse.facetCounts)
                             Log.e("fetchUserListWithFacet", "LiveData Updated")
                         } catch (e: Exception) {
                             Log.e("fetchUserListWithFacet", "JSON Parsing Error: ${e.message}", e)
                         }
                     } ?: run {
                         Log.e("fetchUserListWithFacet", "Error: Result is null")
                     }
                 } else {
                     val e = task.exception
                     Log.e("fetchUserListWithFacet", "Error: ${e?.message}", e)
                     if (e is FirebaseFunctionsException) {
                         Log.e("fetchUserListWithFacet", "Error details: ${e.details}", e)
                     }
                 }
             }
     }
 */

//    fun fetchDrawingStagingList(
//        pageNo: Int,
//        perPage: Int,
//        countryCode: String?,
//        filterBy: String?,
//        sortBy: String?
//    ) {
//
//        Log.e("country",countryCode.toString())
//
//        val filters = filterBy?.let {
//            hashMapOf("filter_by" to it)
//        }
//
//        val sorts = sortBy?.let {
//            hashMapOf("sort_by" to it)
//        }
//
//        FirebaseFirestoreApi.fetchDrawingList(pageNo, perPage, filters, sorts)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val result = task.result?.data as? Map<String, Any>
//
//
//                    result?.let { data ->
//
//
//
//                        val drawingsData = data["data"] as? List<Map<String, Any>>
//                        val drawings = drawingsData?.mapNotNull { parseDrawing(it) } ?: emptyList()
//
//                        val filteredDrawings = if (countryCode.isNullOrEmpty()) {
//                            drawings
//                        } else {
//
//                            drawings.filter { it.author.country == countryCode }
//                        }
//
//                        // Separate freehand and tutorials drawings
//                        val freehandDrawings = filteredDrawings.filter { it.type == "freehand" }
//                        val tutorialDrawings = filteredDrawings.filter { it.type == "tutorials" }
//
//                        // Update LiveData for freehand drawings
//                        val currentFreehandList = _freehandDrawings.value.orEmpty().toMutableList()
//                        if (pageNo == 1) {
//                            currentFreehandList.clear() // Clear list only for the first page
//                        }
//                        currentFreehandList.addAll(freehandDrawings)
//                        _freehandDrawings.postValue(currentFreehandList)
//
//                        // Update LiveData for tutorial drawings
//                        val currentTutorialList = _tutorialDrawings.value.orEmpty().toMutableList()
//                        if (pageNo == 1) {
//                            currentTutorialList.clear() // Clear list only for the first page
//                        }
//
//                        currentTutorialList.addAll(tutorialDrawings)
//                        _tutorialDrawings.postValue(currentTutorialList)
//
//
//                    } ?: run {
//                        Log.e("FirebaseTest", "Error: Result is null")
//                        _freehandDrawings.postValue(emptyList())
//                        _tutorialDrawings.postValue(emptyList())
//                    }
//                } else {
//                    val e = task.exception
//                    Log.e("FirebaseTest", "Error: ${e?.message}", e)
//                    if (e is FirebaseFunctionsException) {
//                        Log.e("FirebaseTest", "Error details: ${e.details}", e)
//                    }
//                    _freehandDrawings.postValue(emptyList())
//                    _tutorialDrawings.postValue(emptyList())
//                }
//            }
//    }

    fun fetchDrawingTagList(
        pageNo: Int,
        perPage: Int,
        search: String
    ) {

        FirebaseFirestoreApi.fetchDrawingListWithTag(pageNo, perPage, search)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<String, Any>
                    Log.e("TAGRR",task.result.data.toString())
                    result?.let { data ->
                        val drawingsData = data["data"] as? List<Map<String, Any>>
                        val drawings = drawingsData?.mapNotNull { parseDrawing(it) } ?: emptyList()

                        if (drawings.isNotEmpty()) {
                            // Update LiveData for tutorial drawings
                            val currenttagList =
                                _tagDrawings.value.orEmpty().toMutableList()

                            if (pageNo == 1) {
                                currenttagList.clear() // Clear list only for the first page
                            }

                            currenttagList.addAll(drawings)
                            _tagDrawings.postValue(currenttagList)


                        } else {
                            _tagDrawings.postValue(emptyList())
                        }

                    } ?: run {
                        Log.e("FirebaseTest", "Error: Result is null")
                        _tagDrawings.postValue(emptyList())
                    }
                } else {
                    val e = task.exception
                    Log.e("FirebaseTest", "Error: ${e?.message}", e)
                    if (e is FirebaseFunctionsException) {
                        Log.e("FirebaseTest", "Error details: ${e.details}", e)
                    }
                    _tagDrawings.postValue(emptyList())
                }
            }
    }


    fun fetchDrawingFreeHandList(
        pageNo: Int,
        perPage: Int,
        countryCode: String?,
        filterByy: String?,
        sortBy: String?
    ) {

        var filterBy = filterByy
        if (!countryCode.isNullOrEmpty()) {
            if (!filterByy.isNullOrEmpty()) {
                filterBy += "&&"
            }
            filterBy += "author.country:="
            filterBy += countryCode
        }

        val filters = filterBy?.let {
            hashMapOf("filter_by" to it)
        }

        val sorts = sortBy?.let {
            hashMapOf("sort_by" to it)
        }


        FirebaseFirestoreApi.fetchDrawingList(pageNo, perPage, filters, sorts)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<String, Any>
                    result?.let { data ->
                        val drawingsData = data["data"] as? List<Map<String, Any>>
                        val drawings = drawingsData?.mapNotNull { parseDrawing(it) } ?: emptyList()

                        /*// Logging for debugging
                        Log.d("Filter FirebaseTest", "Country Code: $countryCode")
                        Log.d("Filter FirebaseTest", "Total drawings fetched: ${drawings.size}")

                        val filteredDrawings = if (countryCode.isNullOrEmpty()) {
                            drawings
                        } else {
                            drawings.filter { drawing ->
                                val drawingCountry = drawing.author?.country
                                Log.d("Filter FirebaseTest", "Drawing Country: $drawingCountry")

                                drawingCountry == countryCode
                            }
                        }

                        Log.d(
                            "Filter FirebaseTest",
                            "Filtered drawings count: ${filteredDrawings.size}"
                        )

                        // Separate freehand and tutorials drawings
                        val freehandDrawings = filteredDrawings.filter { it.type == "freehand" }
*/
                        // Update LiveData for freehand drawings

                        if (drawings.isNotEmpty()) {
                            // Update LiveData for tutorial drawings
                            val currentFreehandList =
                                _freehandDrawings.value.orEmpty().toMutableList()

                            if (pageNo == 1) {
                                currentFreehandList.clear() // Clear list only for the first page
                            }

                            currentFreehandList.addAll(drawings)
                            _freehandDrawings.postValue(currentFreehandList)

                            EventBus.getDefault().post(DrawingsChangeEvent(currentFreehandList))

                        } else {
                            _freehandDrawings.postValue(emptyList())
                        }

                    } ?: run {
                        Log.e("FirebaseTest", "Error: Result is null")
                        _freehandDrawings.postValue(emptyList())
                    }
                } else {
                    val e = task.exception
                    Log.e("FirebaseTest", "Error: ${e?.message}", e)
                    if (e is FirebaseFunctionsException) {
                        Log.e("FirebaseTest", "Error details: ${e.details}", e)
                    }
                    _freehandDrawings.postValue(emptyList())
                }
            }
    }

    fun fetchDrawingStagingList(
        pageNo: Int,
        perPage: Int,
        countryCode: String?,
        filterByy: String?,
        sortBy: String?
    ) {

        var filterBy = filterByy
        if (!countryCode.isNullOrEmpty()) {
            if (!filterByy.isNullOrEmpty()) {
                filterBy += "&&"
            }
            filterBy += "author.country:="
            filterBy += countryCode
        }

        val filters = filterBy?.let {
            hashMapOf("filter_by" to it)
        }

        val sorts = sortBy?.let {
            hashMapOf("sort_by" to it)
        }

        FirebaseFirestoreApi.fetchDrawingList(pageNo, perPage, filters, sorts)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<String, Any>
                    result?.let { data ->
                        val drawingsData = data["data"] as? List<Map<String, Any>>
                        val drawings = drawingsData?.mapNotNull { parseDrawing(it) } ?: emptyList()

                        // Logging for debugging
                        Log.d("Filter FirebaseTest", "Country Code: $countryCode")
                        Log.d("Filter FirebaseTest", "Total drawings fetched: ${drawings.size}")

                        /*  val filteredDrawings = if (countryCode.isNullOrEmpty()) {
                              drawings
                          } else {
                              drawings.filter { drawing ->
                                  val drawingCountry = drawing.author?.country
                                  Log.d("Filter FirebaseTest", "Drawing Country: $drawingCountry")

                                  drawingCountry == countryCode
                              }
                          }

                          Log.d(
                              "Filter FirebaseTest",
                              "Filtered drawings count: ${filteredDrawings.size}"
                          )*/

                        // Separate freehand and tutorials drawings


                        if (drawings.isNotEmpty()) {
                            // Update LiveData for tutorial drawings
                            val currentTutorialList =
                                _tutorialDrawings.value.orEmpty().toMutableList()
                            if (pageNo == 1) {
                                currentTutorialList.clear() // Clear list only for the first page
                            }

                            currentTutorialList.addAll(drawings)
                            _tutorialDrawings.postValue(currentTutorialList)

                            EventBus.getDefault().post(DrawingsChangeEvent(currentTutorialList))

                        } else {
                            _tutorialDrawings.postValue(emptyList())
                        }
                    } ?: run {
                        Log.e("FirebaseTest", "Error: Result is null")
                        _tutorialDrawings.postValue(emptyList())
                    }
                } else {
                    val e = task.exception
                    Log.e("FirebaseTest", "Error: ${e?.message}", e)
                    if (e is FirebaseFunctionsException) {
                        Log.e("FirebaseTest", "Error details: ${e.details}", e)
                    }
                    _tutorialDrawings.postValue(emptyList())
                }
            }
    }


    private fun parseDrawing(data: Map<String, Any>?): NewDrawing {
        val id = data?.get("id") as? String ?: ""
        val title = data?.get("title") as? String ?: ""
        val description = data?.get("description") as? String ?: ""
        val createdAt = data?.get("created_at") as? String ?: ""
        val type = data?.get("type") as? String ?: ""
        val referenceId = data?.get("reference_id") as? String ?: ""
        val tags = data?.get("tags") as? List<String> ?: emptyList()

        val imagesData = data?.get("images") as? Map<String, Any>
        val images = Images(content = imagesData?.get("content") as? String ?: "")

        val metadataData = data?.get("metadata") as? Map<String, Any>
        val metadata = Metadata(
            path = metadataData?.get("path") as? String ?: "",
            parentFolderPath = metadataData?.get("parent_folder_path") as? String ?: "",
            tutorialId = metadataData?.get("tutorial_id") as? String ?: ""
        )


        val statisticsData = data?.get("statistic") as? Map<String, Any>
        val statistics = Statistic(
            comments = statisticsData?.get("comments") as? Int,
            likes = statisticsData?.get("likes") as? Int ?: 0,
            ratings = statisticsData?.get("ratings") as? Int ?: 0,
            reviewsCount = statisticsData?.get("reviews_count") as? Int ?: 0,
            shares = statisticsData?.get("shares") as? Int ?: 0,
            views = statisticsData?.get("views") as? Int ?: 0
        )

        val authorData = data?.get("author") as? Map<String, Any>
        val author = Author(
            userId = authorData?.get("user_id") as? String ?: "",
            name = authorData?.get("name") as? String ?: "",
            avatar = authorData?.get("avatar") as? String ?: "",
            country = authorData?.get("country") as? String,
            level = authorData?.get("level") as? String
        )
        val linksData = data?.get("links") as? Map<String, Any>
        val links = Links(youtube = linksData?.get("youtube") as? String ?: "")

        return NewDrawing(
            id = id,
            title = title,
            description = description,
            createdAt = createdAt,
            type = type,
            tags = tags,
            images = images,
            links = links,
            metadata = metadata,
            statistic = statistics,
            author = author,
            referenceId = referenceId
        )
    }

}

