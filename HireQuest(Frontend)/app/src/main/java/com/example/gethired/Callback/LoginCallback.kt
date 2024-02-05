package com.example.gethired.Callback

import com.example.gethired.Token.LoginResponse

interface LoginCallback {
    fun onResponseLogin(loginResponse: LoginResponse)
    fun onErrorLogin()
}