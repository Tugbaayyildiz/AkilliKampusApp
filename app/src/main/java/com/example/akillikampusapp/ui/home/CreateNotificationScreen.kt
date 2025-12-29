package com.example.akillikampusapp.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.akillikampusapp.data.model.Notification
import com.example.akillikampusapp.data.remote.NotificationService
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateNotificationScreen(
    navController: NavController,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val service = remember { NotificationService() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Genel") }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImageUri = it
        }

    //  Compose uyumlu KONUM OKUMA (observeForever YOK)
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val lat by savedStateHandle
        ?.getStateFlow<Double?>("selected_lat", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    val lng by savedStateHandle
        ?.getStateFlow<Double?>("selected_lng", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(lat, lng) {
        selectedLat = lat
        selectedLng = lng
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Yeni Bildirim Oluştur",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        /* ===== BİLDİRİM TÜRÜ ===== */

        Text(
            text = "Bildirim Türü",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Genel", "Güvenlik", "Sağlık").forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        /* ===== BAŞLIK ===== */

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Başlık", fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        /* ===== AÇIKLAMA ===== */

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(16.dp))

        /* ===== KONUM ===== */

        Button(
            onClick = { navController.navigate("map_picker") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📍 Konum Seç")
        }

        selectedLat?.let {
            Spacer(Modifier.height(8.dp))
            Text("Seçilen Konum: $selectedLat , $selectedLng")
        }

        Spacer(Modifier.height(16.dp))

        /* ===== FOTOĞRAF (İSTEĞE BAĞLI) ===== */

        Button(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷 Fotoğraf Seç (İsteğe Bağlı)")
        }

        selectedImageUri?.let {
            Spacer(Modifier.height(8.dp))
            Text("Fotoğraf seçildi")
        }

        error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))

        /* ===== GÖNDER ===== */

        Button(
            onClick = {
                error = null

                when {
                    title.isBlank() -> error = "Başlık boş olamaz"
                    description.isBlank() -> error = "Açıklama boş olamaz"
                    selectedLat == null || selectedLng == null ->
                        error = "Konum seçmelisin"
                    else -> {
                        val notification = Notification(
                            title = title,
                            description = description,
                            date = SimpleDateFormat(
                                "dd.MM.yyyy HH:mm",
                                Locale.getDefault()
                            ).format(Date()),
                            createdAt = System.currentTimeMillis(),
                            type = selectedType,
                            lat = selectedLat!!,
                            lng = selectedLng!!,
                            imageUrl = selectedImageUri?.toString() ?: "" //  İSTEĞE BAĞLI
                        )

                        service.addNotification(notification) { ok, err ->
                            if (ok) onDone()
                            else error = err ?: "Bildirim gönderilemedi"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bildirimi Gönder")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Geri Dön")
        }
    }
}
