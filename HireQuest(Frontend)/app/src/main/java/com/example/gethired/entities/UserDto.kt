package com.example.gethired.entities

import java.io.Serializable

data class UserDto(
    val id:Long,
    val birthdate: String?,
    var currentOccupation: String?,
    val email: String,
    var headline: String?,
    var name: String,
    val phone: String?,
    var status: Boolean?,
    var username: String,
    var isRecuriter:Int?,
    val gender:String?

) : Serializable