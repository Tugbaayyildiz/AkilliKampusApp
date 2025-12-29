package com.example.akillikampusapp.data.model

data class AppUser(
    val uid: String = "",
    val fullName: String? = "",
    val email: String = "",
    val studentNumber: String = "",
    val department: String = "",
    val role: String = "student"
)
