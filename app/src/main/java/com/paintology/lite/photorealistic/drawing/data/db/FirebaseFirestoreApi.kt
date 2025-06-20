package com.paintology.lite.photorealistic.drawing.data.db

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserUpdateProfile
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model.UserUpdateProfileFlag
import com.paintology.lite.photorealistic.drawing.Activity.utils.showToast
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.challenge.model.Comment
import com.paintology.lite.photorealistic.drawing.util.FirebaseConstants
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * All Rider APIs for firestore
 */
object FirebaseFirestoreApi {


    // Function to get user data prod
    @JvmStatic
    fun callAuthCheckFunction(
        authProvider: String,
        email: String,
        register: Boolean
    ): Task<Map<String, Any>> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "auth_provider" to authProvider,
            "email" to email,
            "register" to register
        )

        return functions
            .getHttpsCallable("user-authCheck")
            .call(data)
            .continueWith { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Unknown error")
                }

                @Suppress("UNCHECKED_CAST")
                task.result?.data as Map<String, Any>
            }
    }

    // user init production function
    @JvmStatic
    fun callUserInitFunction(
        authProvider: String,
        email: String,
        age: String,
        avatar: String,
        bio: String,
        country: String,
        gender: String,
        name: String
    ): Task<Map<String, Any>> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "auth_provider" to authProvider,
            "email" to email,
            "age" to age,
            "avatar" to avatar,
            "bio" to bio,
            "country" to country,
            "gender" to gender,
            "name" to name
        )

        return functions
            .getHttpsCallable("user-init")
            .call(data)
            .continueWith { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Unknown error")
                }

                @Suppress("UNCHECKED_CAST")
                task.result?.data as Map<String, Any>
            }
    }

    @JvmStatic
    fun fetchDrawingListWithFacet(data: HashMap<String, Any>): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        // Log the data for debugging
        val jsonLikeData = data.entries.joinToString(", ", "{", "}") {
            "\"${it.key}\":\"${it.value}\""
        }
        Log.e("Firebase_Firestore", "fetchDrawingListWithFacet: $jsonLikeData")

        return functions.getHttpsCallable("drawing-list").call(data)
    }


    @JvmStatic
    fun incrementCategoryViewsCount(id: String): Task<HttpsCallableResult> {

        val functions = FirebaseFunctions.getInstance()

        // Creating a map with the data to be sent
        val data = mapOf("category_id" to id)

        // Constructing JSON-like string for logging purposes
        val jsonLikeData = data.entries.joinToString(", ", "{", "}") {
            "\"${it.key}\":\"${it.value}\""
        }
        Log.e("Firebase_Firestore", "callCategoryViewFunction: $jsonLikeData")

        // Calling the Cloud Function
        return functions.getHttpsCallable("category-view").call(data)

    }

    @JvmStatic
    fun fetchUserListWithFacet(data: HashMap<String, Any>): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()


        val jsonLikeData = data.entries.joinToString(", ", "{", "}") {
            "\"${it.key}\":\"${it.value}\""
        }
        Log.e("Firebase_Firestore", "fetchDrawingListWithFacet: $jsonLikeData")

        return functions.getHttpsCallable("user-list").call(data)
    }

    @JvmStatic
    fun fetchDrawingListWithTag(
        page: Int = 1,
        perPage: Int = 10,
        q: String = ""
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = HashMap<String, Any>()

        // Set default pagination parameters
        data["page"] = page
        data["per_page"] = perPage
        data["q"] = q


        return functions.getHttpsCallable("drawing-list").call(data)
    }


    // fetch drawing list for gallery production functions
    @JvmStatic
    fun fetchDrawingList(
        page: Int = 1,
        perPage: Int = 10,
        filters: HashMap<String, String>? = null,
        sortBy: HashMap<String, String>? = null,
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = HashMap<String, Any>()

        // Set default pagination parameters
        data["page"] = page
        data["per_page"] = perPage

        // Add sort_by parameter
        sortBy?.let {
            data.putAll(it)

            // Convert filters to a JSON-like string for logging
            val jsonLikeFilters = it.entries.joinToString(", ", "{", "}") {
                "\"${it.key}\":\"${it.value}\""
            }
            Log.e("Firebase_Firestore", "SORT_BY fetchDrawingStagingList: $jsonLikeFilters")
        } ?: run {
            Log.e("Firebase_Firestore", "SORT_BY fetchDrawingStagingList: {}")
        }


        filters?.let {
            data.putAll(it)

            // Convert filters to a JSON-like string for logging
            val jsonLikeFilters = it.entries.joinToString(", ", "{", "}") {
                "\"${it.key}\":\"${it.value}\""
            }
            Log.e("Firebase_Firestore", "fetchDrawingStagingList: $jsonLikeFilters")
        } ?: run {
            Log.e("Firebase_Firestore", "fetchDrawingStagingList: {}")
        }

        // Log the sort_by parameter
        Log.e("Firebase_Firestore", "fetchDrawingStagingList: sort_by=\"$sortBy\"")

        return functions.getHttpsCallable("drawing-list").call(data)
    }

    // post to gallery production function
    @JvmStatic
    fun drawingPostFunction(
        title: String,
        description: String,
        imageUrl: String,
        drawingType: String,
        referenceID: String,
        tags: List<String>,
        path: String,
        parentFolderPath: String,
        tutorialID: String,
        youtubeUrl: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "image_url" to imageUrl,
            "type" to drawingType,
            "reference_id" to referenceID,
            "tags" to tags,
            "path" to path,
            "parent_folder_path" to parentFolderPath,
            "tutorial_id" to tutorialID,
            "youtube_url" to youtubeUrl,
            "app" to StringConstants.APP_NAME
        )

        return functions
            .getHttpsCallable("drawing-post")
            .call(data)
    }

    // send gallery post to firebase
    @JvmStatic
    fun uploadImageToStorage(imagePath: String): Task<Uri> {
        val storage = FirebaseStorage.getInstance()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageRef = storage.reference.child("gallery").child("$timeStamp.jpg")

        val uploadTask = storageRef.putFile(Uri.fromFile(File(imagePath)))

        return Tasks.whenAll(uploadTask).continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception ?: UnknownError("Error uploading image")
            }
            // Get the download URL for the uploaded image
            storageRef.downloadUrl
        }
    }

    // Function to check if a drawing is liked by a specific user

    fun isDrawingLiked(drawingId: String, userId: String): Task<Boolean> {
        val db = FirebaseFirestore.getInstance()
        val likeDocRef = db.collection("drawings").document(drawingId)
            .collection("likes").document(userId)

        return likeDocRef.get().continueWith { task ->
            task.result?.exists() == true
        }
    }

    fun isPostRated(postId: String, userId: String): Task<Boolean> {
        val db = FirebaseFirestore.getInstance()
        val ratingDocRef = db.collection("drawings").document(postId)
            .collection("reviews").document(userId)

        return ratingDocRef.get().continueWith { task ->
            Log.e("TAG", task.result.exists().toString() + " " + postId + " " + userId);
            task.result?.exists()
        }
    }


    // Function to submit a comment for a drawing prod
    @JvmStatic
    fun drawingCommentFunction(
        drawingId: String,
        comment: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId,
            "comment" to comment
        )

        return functions
            .getHttpsCallable("drawing-comment")
            .call(data)
    }

    // reply to comment prod
    fun drawingReplyCommentFunction(
        drawingId: String,
        comment: String,
        parentId: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId,
            "comment" to comment,
            "parent_id" to parentId,
        )

        return functions
            .getHttpsCallable("drawing-comment")
            .call(data)
    }

    // profile function by shehroz used in drawing view screen
    @JvmStatic
    fun userProfileFunction(userId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "user_id" to userId
        )

        return functions
            .getHttpsCallable("user-profile")
            .call(data)
    }


    // get specific drawing prod
    fun getDrawingById(drawingId: String): Task<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()
        return db.collection("drawings").document(drawingId).get()
    }

    // get comments for drawing prod
    fun fetchComments(drawingsId: String): Task<QuerySnapshot> {
        val db = FirebaseFirestore.getInstance()
        val commentsRef = db.collection("drawings").document(drawingsId).collection("comments")
        return commentsRef.get()
    }

    // set share Icon Count
    fun shareCountDrawing(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "drawing_id" to drawingId
        )
        return functions
            .getHttpsCallable("drawing-share")
            .call(data)
    }

    // set count views for drawing
    fun viewDrawing(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "drawing_id" to drawingId
        )
        return functions
            .getHttpsCallable("drawing-view")
            .call(data)
    }

    // set rating for drawing
    fun reviewDrawing(drawingId: String, rating: Int, message: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "drawing_id" to drawingId,
            "rating" to rating,
            "message" to message
        )

        Log.d("TAG", "Sending data to cloud function: $data")

        return functions
            .getHttpsCallable("drawing-review")
            .call(data)
    }

    // set like to drawings
    @JvmStatic
    fun drawingLikeFunction(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId
        )

        return functions
            .getHttpsCallable("drawing-like")
            .call(data)
    }


    // unlike gallery drawing
    @JvmStatic
    fun drawingUnlikeFunction(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId
        )

        return functions
            .getHttpsCallable("drawing-unlike")
            .call(data)
    }

    @JvmStatic
    fun callUserStagingInitFunction(
        authProvider: String,
        email: String,
        age: String,
        avatar: String,
        bio: String,
        country: String,
        gender: String,
        name: String
    ): Task<Map<String, Any>> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "auth_provider" to authProvider,
            "email" to email,
            "age" to age,
            "avatar" to avatar,
            "bio" to bio,
            "country" to country,
            "gender" to gender,
            "name" to name
        )

        return functions
            .getHttpsCallable("user-init")
            .call(data)
            .continueWith { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Unknown error")
                }

                @Suppress("UNCHECKED_CAST")
                task.result?.data as Map<String, Any>
            }
    }


    @JvmStatic
    fun fetchDrawingStagingList(
        page: Int = 1,
        perPage: Int = 10,
        filters: HashMap<String, String>? = null,
        sortBy: HashMap<String, String>? = null,
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = HashMap<String, Any>()

        // Set default pagination parameters
        data["page"] = page
        data["per_page"] = perPage

        // Add sort_by parameter
        sortBy?.let {
            data.putAll(it)

            // Convert filters to a JSON-like string for logging
            val jsonLikeFilters = it.entries.joinToString(", ", "{", "}") {
                "\"${it.key}\":\"${it.value}\""
            }
            Log.e("Firebase_Firestore", "SORT_BY fetchDrawingStagingList: $jsonLikeFilters")
        } ?: run {
            Log.e("Firebase_Firestore", "SORT_BY fetchDrawingStagingList: {}")
        }


        filters?.let {
            data.putAll(it)

            // Convert filters to a JSON-like string for logging
            val jsonLikeFilters = it.entries.joinToString(", ", "{", "}") {
                "\"${it.key}\":\"${it.value}\""
            }
            Log.e("Firebase_Firestore", "fetchDrawingStagingList: $jsonLikeFilters")
        } ?: run {
            Log.e("Firebase_Firestore", "fetchDrawingStagingList: {}")
        }

        // Log the sort_by parameter
        Log.e("Firebase_Firestore", "fetchDrawingStagingList: sort_by=\"$sortBy\"")

        return functions.getHttpsCallable("drawing-staging-list").call(data)
    }


    // get tutorial category data
    @JvmStatic
    fun fetchCategoryStagingList(level: ArrayList<String>): Task<HttpsCallableResult> {
        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data.put("levels", level)
        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("category-list").call(data)
    }

    @JvmStatic
    fun fetchTutorialsList(filter: String, page: Int): Task<HttpsCallableResult> {
        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data.put("filter_by", filter)
        data.put("page", page)
        data.put("per_page", 20)
        data["sort_by"] = "level:asc"


        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("tutorial-list").call(data)
    }


    @JvmStatic
    fun fetchTutorialsList(
        pageNo: Int,
        filter: String,
        sortBy: String? = null
    ): Task<HttpsCallableResult> {
        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data["filter_by"] = filter
        data["page"] = pageNo
        data["per_page"] = 30
        if (sortBy != null)
            data["sort_by"] = sortBy
        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("tutorial-list").call(data)
    }

    @JvmStatic
    fun fetchTutorialsListCount(filter: String = ""): Task<HttpsCallableResult> {

        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data.put("filter_by", filter)
        data.put("page", 1)
        data.put("per_page", 1)
        data.put("facet_by", "level")
        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("tutorial-list").call(data)
    }


    // send gallery post to firebase

    /* @JvmStatic
     fun uploadImageToStorage(imagePath: String): Task<Uri> {
         val storage = FirebaseStorage.getInstance()
         val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
         val storageRef = storage.reference.child("gallery").child("$timeStamp.jpg")

         val uploadTask = storageRef.putFile(Uri.fromFile(File(imagePath)))

         return Tasks.whenAll(uploadTask).continueWithTask { task ->
             if (!task.isSuccessful) {
                 throw task.exception ?: UnknownError("Error uploading image")
             }
             // Get the download URL for the uploaded image
             storageRef.downloadUrl
         }
     }
 */


    @JvmStatic
    fun drawingStagingPostFunction(
        title: String,
        description: String,
        imageUrl: String,
        drawingType: String,
        referenceID: String,
        tags: List<String>,
        path: String,
        parentFolderPath: String,
        tutorialID: String,
        youtubeUrl: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "image_url" to imageUrl,
            "type" to drawingType,
            "reference_id" to referenceID,
            "tags" to tags,
            "path" to path,
            "parent_folder_path" to parentFolderPath,
            "tutorial_id" to tutorialID,
            "youtube_url" to youtubeUrl,
        )

        return functions
            .getHttpsCallable("drawing-staging-post")
            .call(data)
    }

    // Function to submit a comment for a drawing
    @JvmStatic
    fun drawingStagingCommentFunction(
        drawingId: String,
        comment: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId,
            "comment" to comment
        )

        return functions
            .getHttpsCallable("drawing-staging-comment")
            .call(data)
    }

    // profile function by shehroz used in drawing view screen
    fun userStagingProfileFunction(userId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "user_id" to userId
        )

        return functions
            .getHttpsCallable("user-staging-profile")
            .call(data)
    }

    // set share Icon Count
    fun shareStagingCountDrawing(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "drawing_id" to drawingId
        )
        return functions
            .getHttpsCallable("drawing-staging-share")
            .call(data)
    }


    // set count views for drawing
    fun viewStagingDrawing(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "drawing_id" to drawingId
        )
        return functions
            .getHttpsCallable("drawing-staging-view")
            .call(data)
    }

    // set rating for drawing
    fun reviewStagingDrawing(
        drawingId: String,
        rating: Int,
        message: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf(
            "drawing_id" to drawingId,
            "rating" to rating,
            "message" to message
        )

        Log.d("TAG", "Sending data to cloud function: $data")

        return functions
            .getHttpsCallable("drawing-staging-review")
            .call(data)
    }

    // set staging like to drawings
    @JvmStatic
    fun drawingStagingLikeFunction(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId
        )

        return functions
            .getHttpsCallable("drawing-staging-like")
            .call(data)
    }

    // unlike gallery drawing
    @JvmStatic
    fun drawingStagingUnlikeFunction(drawingId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "drawing_id" to drawingId
        )

        return functions
            .getHttpsCallable("drawing-staging-unlike")
            .call(data)
    }


    @JvmStatic
    fun getDrawings(): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("drawings_staging").document().get()
    }

    fun getGalleryTutorials1(): Task<String> {
        return FirebaseFirestore.getInstance().collection("gamifications_staging")
            .document("challenge")
            .collection("submissions")
            .get()
            .continueWith { task ->
                val jsonArray = JSONArray()
                val result = task.getResult()
                result?.documents?.forEach { document ->
                    document.data?.let { jsonObject ->
                        jsonArray.put(JSONObject(jsonObject))
                    }
                }
                jsonArray.toString()
            }
    }


    @JvmStatic
    fun setRating(
        tutorialID: String,
        data: HashMap<String, Int>,
        uid: String
    ): Task<Void> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("Ratings")
            .collection(tutorialID)
            .document(uid).set(data)
    }

    @JvmStatic
    fun getTutorialDetail(uid: String): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("tutorials")
            .document(uid).get()
    }

    fun getPostDetail(uid: String): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("posts")
            .document(uid).get()
    }

    @JvmStatic
    fun getRating(
        tutorialID: String,
        uid: String
    ): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("Ratings")
            .collection(tutorialID)
            .document(uid)
            .get()
    }

    @JvmStatic
    fun setGuideRating(
        tutorialID: String,
        data: HashMap<String, Int>,
        uid: String
    ): Task<Void> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("GuideRatings")
            .collection(tutorialID)
            .document(uid).set(data)
    }

    @JvmStatic
    fun getGuideRating(
        tutorialID: String,
        uid: String
    ): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("GuideRatings")
            .collection(tutorialID)
            .document(uid)
            .get()
    }

    @JvmStatic
    fun saveSearchedContent(
        data: HashMap<String, List<String>>,
        uid: String
    ): Task<Void> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("SearchedContent")
            .collection(uid)
//            .add(data)
            .document("search").set(data)
    }

    @JvmStatic
    fun getSearchedContent(
        uid: String
    ): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("SearchedContent")
            .collection(uid)
//            .add(data)
            .document("search").get()
    }

    @JvmStatic
    fun saveCommunitySearchedContent(
        data: HashMap<String, List<String>>,
        uid: String
    ): Task<Void> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("CommunitySearchedContent")
            .collection(uid)
//            .add(data)
            .document("search").set(data)
    }

    @JvmStatic
    fun getCommunitySearchedContent(
        uid: String
    ): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection(FirebaseConstants.ENVIRONMENT)
            .document("CommunitySearchedContent")
            .collection(uid)
//            .add(data)
            .document("search").get()
    }


    @JvmStatic
    fun getChallenges(): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("gamifications_staging")
            .document("challenge")
            .collection("rules")
            .whereEqualTo("active", true)
            .get()
    }

    @JvmStatic
    fun getChallengesByLevel(userCurrentLevel: String): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("gamifications_staging")
            .document("challenge")
            .collection("rules")
            .whereEqualTo("active", true)
//            .whereEqualTo("difficulty", userCurrentLevel)
            .get()
    }

    @JvmStatic
    fun getDifficultyLevels(): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("gamifications_staging")
            .document("challenge")
            .get()
    }

    fun addChallengeComment(
        comment: String,
        challengeKey: String,
        before: () -> Unit,
        after: () -> Unit,
        onSuccess: () -> Unit
    ) {
//        if (comment.isNotBlank() && FirebaseAuth.getInstance().currentUser?.isAnonymous == false) {
        if (comment.isNotBlank()) {
            before()
            val data = hashMapOf(
                "comment" to comment,
                "key" to challengeKey
            )
            FirebaseFunctions.getInstance()
                .getHttpsCallable("challenge-staging-comment")
                .call(data)
                .addOnSuccessListener { onSuccess() }
                .addOnCompleteListener { after() }
        }
    }

    fun addLike(
        challengeKey: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val data = hashMapOf(
            "key" to challengeKey
        )
        FirebaseFunctions.getInstance()
            .getHttpsCallable("challenge-staging-like")
            .call(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun removeLike(
        challengeKey: String
    ) {
        val data = hashMapOf(
            "key" to challengeKey
        )
        FirebaseFunctions.getInstance()
            .getHttpsCallable("challenge-staging-unlike")
            .call(data)

    }

    fun addView(challengeKey: String) {
        val data = hashMapOf(
            "key" to challengeKey
        )
        FirebaseFunctions.getInstance()
            .getHttpsCallable("challenge-staging-view")
            .call(data)
            .addOnSuccessListener {}
            .addOnCompleteListener { }
    }


    fun getChallengeComments(challengeKey: String, onSuccess: (MutableList<Comment>) -> Unit) {
        FirebaseFirestore.getInstance().collection("gamifications_staging")
            .document("challenge")
            .collection("rules")
            .document(challengeKey)
            .collection("comments").get().addOnSuccessListener { challengeRef ->
                val comments = challengeRef.toObjects(Comment::class.java)
                onSuccess(comments)
            }
    }

    fun getLikeOnChallenge(challengeKey: String, onSuccess: (MutableList<Comment>) -> Unit) {
        FirebaseFirestore.getInstance().collection("gamifications_staging")
            .document("challenge")
            .collection("rules")
            .document(challengeKey)
            .collection("likes").get().addOnSuccessListener { challengeRef ->
                val comments = challengeRef.toObjects(Comment::class.java)
                onSuccess(comments)
            }
    }

    @JvmStatic
    fun getProfile(uid: String): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("users").document(uid).get()
    }

    @JvmStatic
    fun updateProfile(
        userProfile: UserUpdateProfile
    ): Task<HttpsCallableResult> {
        val info = Gson().toJson(userProfile)
        val data = JSONObject(info)
        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("user-update")
            .call(data)
    }

    @JvmStatic
    fun updateCountry(
        userCountry: String
    ): Task<HttpsCallableResult> {
        val data = JSONObject()
        data.put("country", userCountry)
        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("user-update")
            .call(data)
    }

    @JvmStatic
    fun updateProfileName(
        userField: String,
        userName: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            userField to userName
        )
        return functions.getHttpsCallable("user-update")
            .call(data)
    }

    @JvmStatic
    fun updateProfileFlag(
        userProfile: UserUpdateProfileFlag
    ): Task<HttpsCallableResult> {
        val info = Gson().toJson(userProfile)
        val data = JSONObject(info)
        val functions = FirebaseFunctions.getInstance()
        return functions.getHttpsCallable("user-update")
            .call(data)
    }

    @JvmStatic
    fun fetchProfilePrefsData(): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "type" to "all"
        )
        return functions
            .getHttpsCallable("preference-options")
            .call(data)
    }

    @JvmStatic
    fun fetchActivityRewards(
        pageNo: Int,
        filterBY: String,
        sortBY: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data["filter_by"] = filterBY
        data["page"] = pageNo
        data["per_page"] = 30
        data["sort_by"] = sortBY
        return functions
            .getHttpsCallable("activity-list")
            .call(data)
    }

    @JvmStatic
    fun fetchIntroVideos(
        pageNo: Int,
        filterBY: String,
        sortBY: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>()
        data["filter_by"] = filterBY
        data["page"] = pageNo
        data["per_page"] = 30
        data["sort_by"] = sortBY
        return functions
            .getHttpsCallable("guide-list")
            .call(data)
    }

    @JvmStatic
    fun fetchIntroVideoById(
        guide_id: String
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>()
        data["guide_id"] = guide_id
        return functions
            .getHttpsCallable("guide-view")
            .call(data)
    }


    @JvmStatic
    fun updateIncreasableRewardValue(
        field: String,
        value: Int,
        uid: String
    ) {

    }

    @JvmStatic
    fun fetchUnavailableActivity(): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, String>()
        return functions
            .getHttpsCallable("activity-unavailable")
            .call(data)
    }

    fun Context.claimBigPoints(act: String) {
        if (FirebaseAuth.getInstance().currentUser != null && act.isNotEmpty()) {
            fetchUnavailableActivity().addOnSuccessListener { it ->
                val doneActs = it.data as java.util.ArrayList<*>
                val check = doneActs.any { it.toString() == act }
                if (!check) {
                    val functions = FirebaseFunctions.getInstance()
                    val data = hashMapOf(
                        "key" to act
                    )
                    functions.getHttpsCallable("activity-claim")
                        .call(data)
                } else {
                    showToast(getString(R.string.ss_points_already_claimed))
                }
            }.addOnFailureListener {
                Log.e("TAGRR", it.message.toString() + " error")
            }
        }
    }

    @JvmStatic
    fun claimActivityPointsWithId(act: String, mId: String? = null) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val functions = FirebaseFunctions.getInstance()
            val data = hashMapOf(
                "key" to act
            )
            if (mId != null) {
                data["reference_id"] = mId
            }
            functions.getHttpsCallable("activity-claim")
                .call(data)
        }
    }

    fun sendIssue(
        title: String,
        msg: String,
        category: String,
        imageArray: MutableList<String>
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "title" to title,
            "message" to msg,
            "category" to category,
            "attachments" to imageArray
        )

        return functions
            .getHttpsCallable("feedback-add")
            .call(data)
    }


    @JvmStatic
    fun followUser(followId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "user_id" to followId
        )
        return functions.getHttpsCallable("user-follow")
            .call(data)
    }

    @JvmStatic
    fun unfollowUser(followId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "user_id" to followId
        )
        return functions.getHttpsCallable("user-unfollow")
            .call(data)
    }

    @JvmStatic
    fun getUserFromFollowing(uid: String, followId: String): Task<DocumentSnapshot> {
        if (uid.isEmpty() || followId.isEmpty()) {
            throw IllegalArgumentException("Invalid UID or FollowID")
        }
        return FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("following").document(followId).get()
    }

    @JvmStatic
    fun getFollowers(uid: String): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("followers").get()
    }

    @JvmStatic
    fun getFollowings(uid: String): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("following").get()
    }

    @JvmStatic
    fun getChatNode(recipientId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "recipient_id" to recipientId
        )
        return functions.getHttpsCallable("chat-init")
            .call(data)
    }


    @JvmStatic
    fun blockUser(followId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "user_id" to followId
        )
        return functions.getHttpsCallable("user-block")
            .call(data)
    }

    @JvmStatic
    fun unBlockUser(followId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "user_id" to followId
        )
        return functions.getHttpsCallable("user-unblock")
            .call(data)
    }

    @JvmStatic
    fun getUserFromBlocked(uid: String, followId: String): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("blocked").document(followId).get()
    }

    @JvmStatic
    fun getActivityProgress(): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val acts = listOf(
            "open_tutorial",
            "scribble_on_your_canvas",
            "draw_strokes",
            "save_drawing",
            "post_drawing_to_gallery"
        )

        val data = hashMapOf<String, Any>(
            "query_by" to "activity",
            "activities" to acts
        )
        return functions.getHttpsCallable("activity-progress")
            .call(data)
    }

    @JvmStatic
    fun getStoreProducts(page: Int = 1): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data.put("filter_by", "")
        data.put("page", page)
        data.put("per_page", 25)

        return functions.getHttpsCallable("product-list")
            .call(data)
    }

    @JvmStatic
    fun getStoreProducts(page: Int = 1, filterBY: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data.put("filter_by", filterBY)
        data.put("page", page)
        data.put("per_page", 25)

        return functions.getHttpsCallable("product-list")
            .call(data)
    }

    @JvmStatic
    fun sendFlag() {
        val functions = FirebaseFunctions.getInstance()

        val data = hashMapOf<String, Any>() // Create an empty HashMap
        data["android"] = true
        data["app"] = StringConstants.APP_NAME

        functions.getHttpsCallable("user-flag")
            .call(data)
    }

    @JvmStatic
    fun redeemProduct(productId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>(
            "product_id" to productId,
        )
        return functions.getHttpsCallable("product-redeem")
            .call(data)
    }

    @JvmStatic
    fun viewTutorial(tutorialId: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>(
            "id" to tutorialId,
        )
        return functions.getHttpsCallable("tutorial-view")
            .call(data)
    }

    fun getCountries(): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        return functions
            .getHttpsCallable("user-country")
            .call()
    }

    fun getUserRank(country: String? = null): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>()
        val function = functions
            .getHttpsCallable("statistic-leaderboard")
        return if (country == null) {
            function.call(data)
        } else {
            data["country"] = country
            function.call(data)
        }
    }

    @JvmStatic
    fun fetchCommunityList(
        page: Int = 1,
        perPage: Int = 10,
        filters: HashMap<String, String>? = null,
        sortBy: HashMap<String, String>? = null,
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = HashMap<String, Any>()

        // Set default pagination parameters
        data["page"] = page
        data["per_page"] = perPage

        // Add sort_by parameter
        sortBy?.let {
            data.putAll(it)

            // Convert filters to a JSON-like string for logging
            val jsonLikeFilters = it.entries.joinToString(", ", "{", "}") {
                "\"${it.key}\":\"${it.value}\""
            }
            Log.e("Firebase_Firestore", "SORT_BY fetchDrawingStagingList: $jsonLikeFilters")
        } ?: run {
            Log.e("Firebase_Firestore", "SORT_BY fetchDrawingStagingList: {}")
        }


        filters?.let {
            data.putAll(it)

            // Convert filters to a JSON-like string for logging
            val jsonLikeFilters = it.entries.joinToString(", ", "{", "}") {
                "\"${it.key}\":\"${it.value}\""
            }
            Log.e("Firebase_Firestore", "fetchDrawingStagingList: $jsonLikeFilters")
        } ?: run {
            Log.e("Firebase_Firestore", "fetchDrawingStagingList: {}")
        }

        return functions.getHttpsCallable("communityPost-list").call(data)
    }

    fun sendOTP(
        email: String = "",
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>(
            "email" to email,
        )
        return functions.getHttpsCallable("auth-sendOTP")
            .call(data)
    }

    fun verifyOTP(
        email: String = "",
        otp: String = "",
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>(
            "email" to email,
            "otp" to otp
        )
        return functions.getHttpsCallable("auth-verifyOTP")
            .call(data)
    }

    fun resetPassword(
        password: String = ""
    ): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf<String, Any>(
            "password" to password
        )
        return functions.getHttpsCallable("auth-resetPassword")
            .call(data)
    }


    fun getBigPoints(
    ): Task<DocumentSnapshot> {
        return FirebaseFirestore.getInstance().collection("screens")
            .document("big-points")
            .get()
    }

    fun String.countDrawingView() {
        FirebaseFirestoreApi.viewDrawing(
            this
        ).addOnCompleteListener { task ->

        }
    }

    @JvmStatic
    fun getAppBanners(
    ): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("app_banners").get()
    }

}
