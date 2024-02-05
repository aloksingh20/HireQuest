package com.example.gethired.entities

data class Meeting(
    val id:Int,
    val user:String,
    val hr:String,
    val date:String,
    val time:String,
    val link:String,
    val isAttended:Boolean
)
