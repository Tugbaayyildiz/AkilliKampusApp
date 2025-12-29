package com.example.akillikampusapp.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.akillikampusapp.data.model.Notification

import com.example.akillikampusapp.data.remote.NotificationService

@Composable
fun AddNotificationScreen(
    notificationService: NotificationService,
    onNotificationAdded: () -> Unit
) {
    val context = LocalContext.current
 //kullanıcıın yazdığı her şey state de tutuluyor
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Açık") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Başlık") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Tarih") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Durum (Açık / İnceleniyor / Çözüldü)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val newNotification = Notification(
                    title = title,
                    description = description,
                    date = date,
                    status = status
                )

                notificationService.addNotification(
                    notification = newNotification
                ) { success, errorMessage ->

                    if (success) {
                        onNotificationAdded()
                    } else {
                        // İstersen Toast basabilirsin
                        println("Hata: $errorMessage")
                    }
                }
            }
        ) {
            Text("Bildirimi Gönder")
        }

    }
}
//kullanici ekleme ekranı
