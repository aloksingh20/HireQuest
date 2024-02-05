package com.example.gethired.entities

data class Certificate(
    val id:Int,
    val certificateTitle:String,
    val issuedBy:String,
    val start:String,
    val end:String,
    val certificateUrl:String,
    val description:String
)
