package com.example.gethired.entities

data class LoginDto (
    val username:String,
val password:String,
    val fcmToken:String,
    val googleIdToken:String
        )