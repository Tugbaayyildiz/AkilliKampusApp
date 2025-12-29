package com.example.akillikampusapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCreateNotification: () -> Unit,
    onViewNotifications: () -> Unit,
    onLogout: () -> Unit,
    onViewUserNotifications: () -> Unit,
    onOpenMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Ana Menü",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCreateNotification,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("➕ Yeni Bildirim Oluştur")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewNotifications,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("📄 Bildirimleri Gör")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewUserNotifications,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🔔 Bana Gelen Bildirimler")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOpenMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🗺️ Haritayı Aç")
            }
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(" Çıkış Yap")
        }
    }
}
