package com.example.gethired.Callback

interface UsernameAvailabilityCallback {
    fun onUsernameAvailable(isAvailable: Boolean)
    fun onUsernameCheckError()
}