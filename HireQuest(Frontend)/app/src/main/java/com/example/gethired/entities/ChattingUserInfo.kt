package com.example.gethired.entities

data class ChattingUserInfo (
    val username:String,
    val image:String,
    val isRequested:Boolean,
    val isSender:Boolean,
    val roomId:Long
)