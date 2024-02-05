package com.example.gethired.Callback

import com.example.gethired.entities.RegistrationResponse

interface RegisterUserCallback {
    fun onUserRegistrationResponse(registerUserResponse:RegistrationResponse)
    fun onUserRegistrationError(errorCode:Int)
}