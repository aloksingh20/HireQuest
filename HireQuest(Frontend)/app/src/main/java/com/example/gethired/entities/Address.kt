package com.example.gethired.entities

data class Address(
    val id:Int,
    val city: String,
    val country: String,
    val pincode: String,
    val state: String,
    val street: String
)