package com.example.akillikampusapp.data.remote

import com.example.akillikampusapp.data.model.Notification
import com.example.akillikampusapp.data.model.UserNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationService {

    private val db = FirebaseFirestore.getInstance()

    fun addNotification(notification: Notification, onResult: (Boolean, String?) -> Unit) {
        db.collection("notifications")
            .add(notification)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getAllNotifications(onResult: (List<Notification>, String?) -> Unit) {
        db.collection("notifications")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)?.copy(
                        id = doc.id
                    )
                }

                // 🔥 GERÇEK KRONOLOJİK SIRALAMA
                val sorted = list.sortedByDescending { notification ->
                    if (notification.createdAt > 0L) {
                        notification.createdAt
                    } else {
                        // Eski bildirimler için date string'den Long üret
                        try {
                            SimpleDateFormat(
                                "dd.MM.yyyy HH:mm",
                                Locale.getDefault()
                            ).parse(notification.date)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }
                }

                onResult(sorted, null)
            }
            .addOnFailureListener { e ->
                onResult(emptyList(), e.message)
            }
    }


    fun deleteNotification(notificationId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("notifications")
            .document(notificationId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun updateStatus(
        notificationId: String,
        newStatus: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (notificationId.isBlank()) {
            onResult(false, "Bildirim ID boş geldi.")
            return
        }

        db.collection("notifications")
            .document(notificationId)
            .update("status", newStatus)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun updateDescription(
        notificationId: String,
        newDescription: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("notifications")
            .document(notificationId)
            .update("description", newDescription)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // ✅ Admin bir şey güncellediğinde takipçilere "mesaj" düşürür
    fun notifyFollowers(
        notificationId: String,
        title: String,
        message: String
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener { users ->

                users.documents.forEach { userDoc ->
                    val userId = userDoc.id

                    db.collection("users")
                        .document(userId)
                        .collection("followedNotifications")
                        .document(notificationId)
                        .get()
                        .addOnSuccessListener { followDoc ->

                            if (followDoc.exists()) {
                                val data = mapOf(
                                    "title" to title,
                                    "message" to message,
                                    "notificationId" to notificationId,
                                    "createdAt" to System.currentTimeMillis(),
                                    "isRead" to false
                                )

                                db.collection("user_notifications")
                                    .document(userId)
                                    .collection("items")
                                    .add(data)
                            }
                        }
                }
            }
    }

    // ✅ Kullanıcının "Bana Gelen Bildirimler" ekranı buradan okuyacak
    fun getUserNotifications(
        userId: String,
        onResult: (List<UserNotification>) -> Unit
    ) {
        db.collection("user_notifications")
            .document(userId)
            .collection("items")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserNotification::class.java)
                        ?.copy(id = doc.id)   // ✅ EN KRİTİK SATIR
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun sendEmergencyToAllUsers(
        title: String,
        message: String
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { users ->

                users.documents.forEach { user ->

                    val userId = user.id

                    val data = mapOf(
                        "title" to title,
                        "message" to message,
                        "createdAt" to System.currentTimeMillis(),
                        "isRead" to false,
                        "type" to "EMERGENCY"
                    )

                    db.collection("user_notifications")
                        .document(userId)
                        .collection("items")
                        .add(data)
                }
            }
    }
    // 🔥 TEK BİLDİRİM SİL
    fun deleteUserNotification(
        userId: String,
        notificationDocId: String,
        onResult: (Boolean) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("user_notifications")
            .document(userId)
            .collection("items")
            .document(notificationDocId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // 🔥 TÜM BİLDİRİMLERİ TEMİZLE
    fun clearAllUserNotifications(
        userId: String,
        onResult: (Boolean) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("user_notifications")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    onResult(true)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

}
