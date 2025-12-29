
package com.example.akillikampusapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akillikampusapp.data.model.AppUser
import com.example.akillikampusapp.data.remote.UserService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserListScreen(
    onBack: () -> Unit
) {
    val userService = remember { UserService() }

    var users by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userService.getAllUsers { list, err ->
            if (err != null) {
                error = err
            } else {
                users = list.filter { it.role == "student" }
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kayıtlı Öğrenciler") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Text(
                        text = "Hata: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                users.isEmpty() -> {
                    Text(
                        text = "Kayıtlı öğrenci yok",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(users) { user ->
                            UserCard(
                                user = user,
                                onDelete = { userId ->
                                    userService.deleteUserDoc(userId) { success, err ->
                                        if (success) {
                                            users = users.filterNot { it.uid == userId }
                                        } else {
                                            error = err
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: AppUser,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = user.fullName ?: "İsimsiz Kullanıcı")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Email: ${user.email}")
            Text("Öğrenci No: ${user.studentNumber}")
            Text("Bölüm: ${user.department}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onDelete(user.uid) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Kullanıcıyı Sil")
            }
        }
    }
}
