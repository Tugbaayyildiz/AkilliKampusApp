

package com.example.akillikampusapp.data.remote

import com.example.akillikampusapp.data.model.AppUser
import com.google.firebase.firestore.FirebaseFirestore

class UserService {

    private val db = FirebaseFirestore.getInstance()

    /* =====================================================
       👤 KULLANICI OLUŞTURMA / OKUMA
       ===================================================== */

    fun saveUser(
        uid: String,
        fullName: String,
        email: String,
        studentNumber: String,
        department: String,
        role: String = "student",
        onResult: (Boolean, String?) -> Unit
    ) {
        val userData = mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "studentNumber" to studentNumber,
            "department" to department,
            "role" to role
        )

        db.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getUserRole(uid: String, onResult: (String?) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.getString("role"))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getAllUsers(onResult: (List<AppUser>, String?) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(AppUser::class.java)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e ->
                onResult(emptyList(), e.message)
            }
    }

    fun deleteUserDoc(
        userId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    /* =====================================================
       ⭐ TAKİP SİSTEMİ (SADECE TAKİP – BİLDİRİM YOK)
       ===================================================== */

    fun followNotification(
        userId: String,
        notificationId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("followedNotifications")
            .document(notificationId)
            .set(mapOf("followedAt" to System.currentTimeMillis()))
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun unfollowNotification(
        userId: String,
        notificationId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("followedNotifications")
            .document(notificationId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getFollowedNotificationIds(
        userId: String,
        onResult: (List<String>) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("followedNotifications")
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.documents.map { it.id }
                onResult(ids)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getFollowerCount(
        notificationId: String,
        onResult: (Int) -> Unit
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener { users ->
                var count = 0
                var processed = 0

                users.documents.forEach { user ->
                    db.collection("users")
                        .document(user.id)
                        .collection("followedNotifications")
                        .document(notificationId)
                        .get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) count++
                            processed++
                            if (processed == users.size()) {
                                onResult(count)
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(0)
            }
    }
}
