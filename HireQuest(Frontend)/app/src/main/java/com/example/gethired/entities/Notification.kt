package com.example.gethired.entities

import java.time.LocalDateTime

data class Notification(
    val id: Long,
    val title: String,
    val body: String,
    val senderUsername: String,
    val receiverUsername: String,
    val notificationType: String,
    var readStatus: Boolean,
    val timestamp: String
)
