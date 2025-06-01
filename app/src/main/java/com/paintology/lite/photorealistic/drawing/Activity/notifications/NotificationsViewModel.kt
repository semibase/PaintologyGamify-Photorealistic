package com.paintology.lite.photorealistic.drawing.Activity.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.CommunityPostNotification
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.Notification
import com.paintology.lite.photorealistic.drawing.Activity.notifications.models.NotificationAdmin

class NotificationsViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val notifications: MutableLiveData<List<Notification>> = MutableLiveData()
    private val notificationsCount: MutableLiveData<List<Notification>> = MutableLiveData()
    private val specificDrawing: MutableLiveData<NewDrawing?> = MutableLiveData()
    private val specificAdminNotification: MutableLiveData<NotificationAdmin?> = MutableLiveData()
    private val notificationsAdmin: MutableLiveData<List<NotificationAdmin>> = MutableLiveData()

    val totalNotifications = MutableLiveData<Int>()
    val totalNotificationsAdmin = MutableLiveData<Int>()

    fun getNotifications(): LiveData<List<Notification>> {
        return notifications
    }

    fun getNotificationsCount(): LiveData<List<Notification>> {
        return notificationsCount
    }

    fun loadNotifications(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificationList = mutableListOf<Notification>()
                    for (doc in snapshot.documents) {
                        val notification = doc.toObject(Notification::class.java)
                        notification?.let { model ->
                            if (!model.delete) {
                                model.id = doc.id  // Set the document ID
                                notificationList.add(model)
                            }
                        }
                    }
                    notifications.value = notificationList
                } else {
                    notifications.value = emptyList()
                }
            }
    }

    fun loadNotificationsForCount(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificationList = mutableListOf<Notification>()
                    for (doc in snapshot.documents) {
                        val notification = doc.toObject(Notification::class.java)
                        notification?.let { model ->
                            if (!model.delete && !model.read) {
                                model.id = doc.id  // Set the document ID
                                notificationList.add(model)
                            }
                        }
                    }
                    notificationsCount.value = notificationList
                } else {
                    notificationsCount.value = emptyList()
                }
            }
    }

    fun getSpecificDrawing(): LiveData<NewDrawing?> {
        return specificDrawing
    }


    fun loadSpecificDrawing(documentID: String) {
        firestore.collection("drawings")
            .document(documentID)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the error here
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val drawing = snapshot.toObject(NewDrawing::class.java)
                    // Assuming `specificDrawing` is a list and you want to update it with one item
                    drawing?.let {
                        it.id = snapshot.id
                        specificDrawing.value = it
                    } ?: run {
                        specificDrawing.value = null
                    }
                } else {
                    specificDrawing.value = null
                }
            }
    }

    fun getNotificationsAdmin(): LiveData<List<NotificationAdmin>> {
        return notificationsAdmin
    }

    fun loadNotificationsAdmin() {
        firestore.collection("notifications")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificationList = mutableListOf<NotificationAdmin>()
                    for (doc in snapshot.documents) {
                        val notification = doc.toObject(NotificationAdmin::class.java)
                        notification?.let { model ->
                            model.id = doc.id
                            notificationList.add(model)
                        }
                    }
                    notificationsAdmin.value = notificationList
                } else {
                    notificationsAdmin.value = emptyList()
                }
            }
    }

    fun loadNotificationsCount(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    var totalNotificationCount = 0
                    for (doc in snapshot.documents) {
                        val notification = doc.toObject(Notification::class.java)
                        notification?.let { model ->
                            if (!model.delete) {
                                totalNotificationCount++
                            }
                        }
                    }
                    totalNotifications.value = totalNotificationCount
                } else {
                    totalNotifications.value = 0
                }
            }
    }

    fun loadNotificationsAdminCount() {
        firestore.collection("notifications")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    var totalNotificationCount = 0
                    for (doc in snapshot.documents) {
                        val notification = doc.toObject(NotificationAdmin::class.java)
                        notification?.let { model ->
                            totalNotificationCount++
                        }
                    }
                    totalNotificationsAdmin.value = totalNotificationCount
                } else {
                    totalNotificationsAdmin.value = 0
                }
            }
    }

    fun fetchCommunityPost(
        documentId: String,
        onSuccess: (CommunityPostNotification) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("community_posts").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val communityPost = CommunityPostNotification(document.data as Map<String, Any>)
                    onSuccess(communityPost)
                } else {
                    onFailure(Exception("Document not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getSpecificAdminNotification(): LiveData<NotificationAdmin?> {
        return specificAdminNotification
    }

    fun fetchAdminNotification(documentId: String) {
        firestore.collection("notifications")
            .document(documentId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the error here
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val drawing = snapshot.toObject(NotificationAdmin::class.java)
                    // Assuming `specificDrawing` is a list and you want to update it with one item
                    drawing?.let {
                        specificAdminNotification.value = it
                    } ?: run {
                        specificAdminNotification.value = null
                    }
                } else {
                    specificAdminNotification.value = null
                }
            }
    }
}

