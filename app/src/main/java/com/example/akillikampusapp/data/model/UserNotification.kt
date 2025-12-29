package com.example.akillikampusapp.data.model

data class UserNotification(
    val id: String = "",
    val message: String = "",
    val createdAt: Long = 0,
    val notificationId: String = "",
    val isRead: Boolean = false
)
