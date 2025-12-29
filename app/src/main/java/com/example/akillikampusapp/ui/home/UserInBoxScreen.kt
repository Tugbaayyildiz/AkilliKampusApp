package com.example.akillikampusapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class UserNotification(
    val title: String = "",
    val message: String = "",
    val createdAt: Long = 0L
)

@Composable
fun UserInboxScreen(
    onBack: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var notifications by remember { mutableStateOf<List<UserNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (userId == null) return@LaunchedEffect

        db.collection("user_notifications")
            .document(userId)
            .collection("items")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snap ->
                notifications = snap.documents.mapNotNull {
                    it.toObject(UserNotification::class.java)
                }
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Geri Dön")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            notifications.isEmpty() -> {
                Text("Henüz size gelen bildirim yok")
            }

            else -> {
                LazyColumn {
                    items(notifications) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(notif.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(notif.message)
                            }
                        }
                    }
                }
            }
        }
    }
}
