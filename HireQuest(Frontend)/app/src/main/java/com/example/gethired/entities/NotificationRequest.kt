package com.example.gethired.entities

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    val senderUsername: String,
    val title: String,
    val body: String,
    val receiverUsername: String,
    val notificationType: String
)
