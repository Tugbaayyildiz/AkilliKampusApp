package com.example.akillikampusapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akillikampusapp.data.model.UserNotification
import com.example.akillikampusapp.data.remote.NotificationService
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserNotificationsScreen(
    onBack: () -> Unit
) {
    val service = remember { NotificationService() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var list by remember { mutableStateOf<List<UserNotification>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (userId != null) {
            service.getUserNotifications(userId) {
                list = it
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔙 GERİ
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Geri")
        }

        Spacer(Modifier.height(12.dp))

        // 🗑️ TÜMÜNÜ TEMİZLE
        if (list.isNotEmpty()) {
            OutlinedButton(
                onClick = {
                    userId?.let {
                        service.clearAllUserNotifications(it) { ok ->
                            if (ok) {
                                list = emptyList()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Tüm Bildirimleri Temizle")
            }

            Spacer(Modifier.height(12.dp))
        }

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            list.isEmpty() -> {
                Text("Henüz bildirimin yok")
            }

            else -> {
                LazyColumn {
                    items(list) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = item.message,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(Modifier.height(8.dp))

                                // 🗑️ TEK BİLDİRİM SİL
                                OutlinedButton(
                                    onClick = {
                                        userId?.let {
                                            service.deleteUserNotification(
                                                userId = it,
                                                notificationDocId = item.id
                                            ) { ok ->
                                                if (ok) {
                                                    list = list.filterNot { it.id == item.id }
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Sil")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
