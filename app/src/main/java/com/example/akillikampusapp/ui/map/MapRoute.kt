package com.example.akillikampusapp.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.akillikampusapp.data.model.Notification
import com.example.akillikampusapp.data.remote.NotificationService

@Composable
fun MapRoute(
    onDetail: (String) -> Unit
) {
    val service = remember { NotificationService() }

    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        service.getAllNotifications { list, err ->
            error = err
            notifications = list.filter { it.lat != 0.0 && it.lng != 0.0 }
            loading = false
        }
    }

    when {
        loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hata: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            MapScreen(
                notifications = notifications,
                onDetail = onDetail
            )
        }
    }
}
