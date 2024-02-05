package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Callback.LoginCallback
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.Repository.RegisterLoginRepository
import com.example.gethired.Token.LoginResponse
import com.example.gethired.entities.GoogleSignInClass
import com.example.gethired.entities.LoginDto
import com.example.gethired.entities.UserDto

class RegisterLoginViewModel:ViewModel() {
    private val registerLoginRepository=RegisterLoginRepository()
    fun loginUser(loginDto: LoginDto, callback: LoginCallback){
        registerLoginRepository.loginUser(loginDto,object : LoginCallback {
            override fun onResponseLogin(loginResponse: LoginResponse) {
                callback.onResponseLogin(loginResponse)
            }

            override fun onErrorLogin() {
                callback.onErrorLogin()
            }

        })
    }

    fun loginUserUsingGoogle(loginDto: GoogleSignInClass, callback: LoginCallback){
        registerLoginRepository.loginUserUsingGoogle(loginDto,object : LoginCallback {
            override fun onResponseLogin(loginResponse: LoginResponse) {
                callback.onResponseLogin(loginResponse)
            }

            override fun onErrorLogin() {
                callback.onErrorLogin()
            }

        })
    }
    fun updatePassword(email: String,password:String,callback: UpdateUserCallback){
        registerLoginRepository.updatePassword(email,password,object : UpdateUserCallback {
            override fun onUserUpdated(updatedUserDto: UserDto) {
                callback.onUserUpdated(updatedUserDto)
            }
            override fun onUpdateUserError() {
                callback.onUpdateUserError()
            }
        })
    }

    fun getFcmToken(username:String):LiveData<Boolean>{
        return registerLoginRepository.getFcmToken(username)
    }

    fun logout(username: String):LiveData<Boolean>{
        return registerLoginRepository.logout(username)
    }
}