package com.example.akillikampusapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.example.akillikampusapp.data.remote.UserService

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToResetPassword: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val userService = UserService()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                            // Kullanıcı rolünü Firestore'dan çekiyoruz
                            userService.getUserRole(uid) { role ->
                                if (role != null) {
                                    onLoginSuccess(role)
                                } else {
                                    error = "Kullanıcı rolü bulunamadı!"
                                }
                            }

                        } else {
                            error = "Giriş başarısız: ${task.exception?.message}"
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Hesabın yok mu? Kayıt ol")
        }
        TextButton(onClick = onNavigateToResetPassword) {
            Text("Şifreni mi unuttun?")
        }

    }

}
