package com.example.gethired.SealedClasses

sealed class Errors{
    data class BadRequest(val errorMessage: String): Errors()
    data class Unauthorized(val errorMessage: String): Errors()
    data class NotFound(val errorMessage: String): Errors()
    data class Unknown(val errorMessage: String): Errors()
}
