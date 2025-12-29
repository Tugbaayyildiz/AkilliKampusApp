package com.example.akillikampusapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var info by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Şifre Sıfırlama") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // E-posta alanı
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta adresi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Şifre sıfırlama butonu
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                info = if (task.isSuccessful) {
                                    "Şifre sıfırlama bağlantısı e-posta adresine gönderildi."
                                } else {
                                    "Hata: ${task.exception?.message}"
                                }
                            }
                    } else {
                        info = "Lütfen e-posta girin."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Şifre Sıfırla")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bilgi / hata mesajı
            info?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
