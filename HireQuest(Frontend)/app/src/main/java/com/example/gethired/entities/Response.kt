package com.example.gethired.entities

import retrofit2.http.HTTP

data class Response(
    val url:String,
    val message:String,
    val status:Int
)
