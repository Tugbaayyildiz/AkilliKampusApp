package com.example.akillikampusapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akillikampusapp.data.model.Notification
import com.example.akillikampusapp.data.remote.NotificationService
import com.example.akillikampusapp.data.remote.UserService
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NotificationListScreen(
    isAdmin: Boolean,
    onBack: () -> Unit
) {
    val notificationService = remember { NotificationService() }
    val userService = remember { UserService() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var followedIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var followerCountMap by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var selectedStatus by remember { mutableStateOf("Tümü") }
    var selectedType by remember { mutableStateOf("Tümü") }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFollowed by remember { mutableStateOf(false) }

    var editingNotificationId by remember { mutableStateOf<String?>(null) }
    var editedDescription by remember { mutableStateOf("") }

    var deleteCandidateId by remember { mutableStateOf<String?>(null) }

    fun refreshNotifications() {
        notificationService.getAllNotifications { list, err ->
            notifications = list
            error = err
            isLoading = false

            if (isAdmin) {
                list.forEach { n ->
                    userService.getFollowerCount(n.id) { count ->
                        followerCountMap = followerCountMap + (n.id to count)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshNotifications()
        currentUserId?.let { uid ->
            userService.getFollowedNotificationIds(uid) {
                followedIds = it
            }
        }
    }

    val filteredNotifications = notifications.filter { n ->
        (selectedStatus == "Tümü" || n.status == selectedStatus) &&
                (selectedType == "Tümü" || n.type == selectedType) &&
                (searchQuery.isBlank() ||
                        n.title.contains(searchQuery, true) ||
                        n.description.contains(searchQuery, true)) &&
                (!showOnlyFollowed || followedIds.contains(n.id))
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Geri Dön")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ara (başlık veya açıklama)") }
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Tümü", "Açık", "İnceleniyor", "Çözüldü").forEach {
                FilterChip(
                    selected = selectedStatus == it,
                    onClick = { selectedStatus = it },
                    label = { Text(it) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Tümü", "Genel", "Güvenlik", "Sağlık").forEach {
                FilterChip(
                    selected = selectedType == it,
                    onClick = { selectedType = it },
                    label = { Text(it) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (!isAdmin) {
            FilterChip(
                selected = showOnlyFollowed,
                onClick = { showOnlyFollowed = !showOnlyFollowed },
                label = { Text("Takip Edilenler") }
            )
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text("Hata: $error", color = MaterialTheme.colorScheme.error)
            }
            filteredNotifications.isEmpty() -> {
                Text("Filtreye uygun bildirim yok")
            }
            else -> {
                LazyColumn {
                    items(filteredNotifications) { n ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {

                                Text(n.title, style = MaterialTheme.typography.titleMedium)
                                Text(n.description)

                                Spacer(Modifier.height(4.dp))

                                Text("Tarih: ${n.date}")
                                Text("Durum: ${n.status}")
                                Text("Tür: ${n.type}")

                                if (isAdmin) {
                                    Text(
                                        "Takip Eden Sayısı: ${followerCountMap[n.id] ?: 0}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                if (!isAdmin && currentUserId != null) {
                                    val isFollowed = followedIds.contains(n.id)
                                    Spacer(Modifier.height(8.dp))
                                    Button(onClick = {
                                        if (isFollowed) {
                                            userService.unfollowNotification(currentUserId, n.id) { _, _ ->
                                                followedIds = followedIds - n.id
                                            }
                                        } else {
                                            userService.followNotification(currentUserId, n.id) { _, _ ->
                                                followedIds = followedIds + n.id
                                            }
                                        }
                                    }) {
                                        Text(if (isFollowed) "Takipten Çıkar" else "Takip Et")
                                    }
                                }

                                if (isAdmin) {
                                    Spacer(Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                        Button(onClick = {
                                            notificationService.updateStatus(n.id, "İnceleniyor") { ok, _ ->
                                                if (ok) {
                                                    refreshNotifications()
                                                    notificationService.notifyFollowers(
                                                        n.id,
                                                        "Bildirim Güncellendi",
                                                        "“${n.title}” başlıklı bildirim inceleniyor durumuna alındı."
                                                    )
                                                }
                                            }
                                        }) { Text("İnceleniyor") }

                                        Button(onClick = {
                                            notificationService.updateStatus(n.id, "Çözüldü") { ok, _ ->
                                                if (ok) {
                                                    refreshNotifications()
                                                    notificationService.notifyFollowers(
                                                        n.id,
                                                        "Bildirim Çözüldü",
                                                        "“${n.title}” başlıklı bildirimin durumu çözüldü."
                                                    )
                                                }
                                            }
                                        }) { Text("Çözüldü") }

                                        OutlinedButton(onClick = {
                                            deleteCandidateId = n.id
                                        }) {
                                            Text("Sil")
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Button(onClick = {
                                        editingNotificationId = n.id
                                        editedDescription = n.description
                                    }) {
                                        Text("Açıklamayı Düzenle")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteCandidateId != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidateId = null },
            title = { Text("Bildirim Silinsin mi?") },
            text = { Text("Bu işlem geri alınamaz.") },
            confirmButton = {
                Button(onClick = {
                    val id = deleteCandidateId!!
                    notificationService.deleteNotification(id) { _, _ ->
                        refreshNotifications()
                        deleteCandidateId = null
                    }
                }) { Text("Sil") }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidateId = null }) {
                    Text("İptal")
                }
            }
        )
    }

    if (editingNotificationId != null) {
        AlertDialog(
            onDismissRequest = { editingNotificationId = null },
            title = { Text("Açıklamayı Düzenle") },
            text = {
                OutlinedTextField(
                    value = editedDescription,
                    onValueChange = { editedDescription = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val id = editingNotificationId!!
                    notificationService.updateDescription(id, editedDescription) { ok, _ ->
                        if (ok) {
                            refreshNotifications()
                            notificationService.notifyFollowers(
                                id,
                                "Bildirim Güncellendi",
                                "“${notifications.first { it.id == id }.title}” başlıklı bildirimin açıklaması güncellendi."
                            )
                        }
                        editingNotificationId = null
                    }
                }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { editingNotificationId = null }) {
                    Text("İptal")
                }
            }
        )
    }
}
