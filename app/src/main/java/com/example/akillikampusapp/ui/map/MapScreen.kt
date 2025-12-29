package com.example.akillikampusapp.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.akillikampusapp.data.model.Notification
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@Composable
fun MapScreen(
    notifications: List<Notification>,
    onDetail: (String) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()

    var selectedNotification by remember { mutableStateOf<Notification?>(null) }

    //Bildirimler geldiyse kamerayı ilk bildirime götür
    LaunchedEffect(notifications) {
        val first = notifications.firstOrNull()
        if (first != null && first.lat != 0.0 && first.lng != 0.0) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(first.lat, first.lng),
                    14f
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            notifications.forEach { notif ->
                if (notif.lat != 0.0 && notif.lng != 0.0) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(notif.lat, notif.lng)
                        ),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            when (notif.type) {
                                "Güvenlik" -> BitmapDescriptorFactory.HUE_RED
                                "Sağlık" -> BitmapDescriptorFactory.HUE_GREEN
                                else -> BitmapDescriptorFactory.HUE_BLUE
                            }
                        ),
                        onClick = {
                            selectedNotification = notif
                            true
                        }
                    )
                }
            }
        }

        selectedNotification?.let {
            PinInfoCard(
                notification = it,
                onClose = { selectedNotification = null },
                onDetail = onDetail
            )
        }
    }
}

@Composable
fun PinInfoCard(
    notification: Notification,
    onClose: () -> Unit,
    onDetail: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(notification.title, style = MaterialTheme.typography.titleMedium)
            Text("Tür: ${notification.type}")

            val minutesAgo =
                (System.currentTimeMillis() - notification.createdAt) / 60000
            Text("$minutesAgo dakika önce")

            Spacer(Modifier.height(10.dp))

            Row {
                TextButton(onClick = onClose) {
                    Text("Kapat")
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = { onDetail(notification.id) }) {
                    Text("Detayı Gör")
                }
            }
        }
    }
}
