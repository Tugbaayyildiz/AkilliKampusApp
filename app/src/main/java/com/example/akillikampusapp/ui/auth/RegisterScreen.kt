package com.example.akillikampusapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.auth.FirebaseAuth
import com.example.akillikampusapp.data.remote.UserService
import com.example.akillikampusapp.data.remote.FirebaseAuthService

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authService = remember { FirebaseAuthService() }
    val userService = remember { UserService() }

    var fullName by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val departments = listOf(
        "Bilgisayar Müh.",
        "Elektrik Müh.",
        "Makine Müh.",
        "İşletme",
        "Mimarlık"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Kayıt Ol", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Ad Soyad
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Ad Soyad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Öğrenci Numarası
        OutlinedTextField(
            value = studentNumber,
            onValueChange = { studentNumber = it },
            label = { Text("Öğrenci Numarası") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Şifre
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre (min 6 karakter)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bölüm Dropdown
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedDepartment,
                onValueChange = {},
                label = { Text("Birim / Bölüm") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                departments.forEach { dept ->
                    DropdownMenuItem(
                        text = { Text(dept) },
                        onClick = {
                            selectedDepartment = dept
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorText != null) {
            Text(text = errorText!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Kayıt Butonu
        Button(
            onClick = {
                errorText = null

                if (fullName.isBlank() || studentNumber.isBlank() ||
                    email.isBlank() || selectedDepartment.isBlank()
                ) {
                    errorText = "Lütfen tüm alanları doldurun."
                    return@Button
                }

                if (password.length < 6) {
                    errorText = "Şifre en az 6 karakter olmalı."
                    return@Button
                }

                isLoading = true

                authService.register(email, password) { success, error ->
                    if (success) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                        userService.saveUser(
                            uid = uid,
                            fullName = fullName,
                            email = email,
                            studentNumber = studentNumber,
                            department = selectedDepartment
                        ) { saved, saveError ->
                            isLoading = false
                            if (saved) onRegisterSuccess()
                            else errorText = saveError
                        }

                    } else {
                        isLoading = false
                        errorText = error
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading)
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else
                Text("Kayıt Ol")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Zaten hesabın var mı? Giriş yap")
        }
    }
}
