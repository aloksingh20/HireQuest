package com.example.gethired.entities

data class UserProfile(
    val id: Long,
    val username: String,
    val name: String,
    val email: String,
    val headline: String,
    val birthdate: String,
    val currentOccupation: String,
    val status: Boolean,
    val phone: String,
    val isRecuriter: Int,
    val gender: String,
    val about: String,
    val imageData: String
)