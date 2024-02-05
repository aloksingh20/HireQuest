package com.example.gethired.entities

data class Chat(
    val id: Long,
    val roomId:Long,
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val timestamp: String,
    val seen: Boolean
)