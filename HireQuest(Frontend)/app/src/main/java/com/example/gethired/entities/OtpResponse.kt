package com.example.gethired.entities

import java.time.Instant

data class OtpResponse (

    val id:Long,
    val email:String,
    val message:String,
    val createdAt:Instant

    )