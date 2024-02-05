package com.example.gethired.Callback

import com.example.gethired.entities.UserDto

interface UpdateUserCallback {
    fun onUserUpdated(updatedUserDto: UserDto)
    fun onUpdateUserError()
}