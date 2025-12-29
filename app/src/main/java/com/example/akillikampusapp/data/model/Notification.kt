package com.example.akillikampusapp.data.model

data class Notification(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val createdAt: Long = 0L,
    val status: String = "Açık",
    val type: String = "Genel",
    val imageUrl: String = "" ,  //  FOTOĞRAF
    val lat: Double = 0.0,
    val lng: Double = 0.0

)
