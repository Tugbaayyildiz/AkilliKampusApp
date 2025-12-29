package com.example.akillikampusapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akillikampusapp.data.remote.NotificationService

@Composable
fun AdminHomeScreen(
    onViewUsers: () -> Unit,
    onViewNotifications: () -> Unit,
    onLogout: () -> Unit
) {


    var showEmergencyDialog by remember { mutableStateOf(false) }
    var emergencyTitle by remember { mutableStateOf("") }
    var emergencyMessage by remember { mutableStateOf("") }

    val notificationService = remember { NotificationService() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Admin Panel",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onViewUsers,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Kayıtlı Kullanıcıları Gör")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onViewNotifications,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Bildirimleri Gör")
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  ACİL DURUM BUTONU (YENİ)
        Button(
            onClick = { showEmergencyDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("🚨 Acil Durum Yayınla")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Çıkış Yap")
        }
    }

    //  ACİL DURUM POPUP
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = { Text("Acil Durum Bildirimi") },
            text = {
                Column {
                    OutlinedTextField(
                        value = emergencyTitle,
                        onValueChange = { emergencyTitle = it },
                        label = { Text("Başlık") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = emergencyMessage,
                        onValueChange = { emergencyMessage = it },
                        label = { Text("Açıklama") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notificationService.sendEmergencyToAllUsers(
                            title = emergencyTitle,
                            message = emergencyMessage
                        )
                        emergencyTitle = ""
                        emergencyMessage = ""
                        showEmergencyDialog = false
                    }
                ) {
                    Text("Yayınla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}
