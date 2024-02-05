package com.example.gethired.ViewModel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gethired.Callback.RegisterUserCallback
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.Callback.UsernameAvailabilityCallback
import com.example.gethired.PREF_FILE_NAME
import com.example.gethired.Repository.UserRepository
import com.example.gethired.Token.LoginResponse
import com.example.gethired.Token.TokenManager
import com.example.gethired.entities.LoginDto
import com.example.gethired.entities.RegisterDto
import com.example.gethired.entities.RegistrationResponse
import com.example.gethired.entities.UserDto
import com.google.gson.Gson
import kotlinx.coroutines.launch


class UserViewModel(private val tokenManager: TokenManager) :ViewModel() {
    private val userRepository = UserRepository(tokenManager)

    fun createUser(registerDto: RegisterDto,callback: RegisterUserCallback){
        userRepository.createUser(registerDto , object :RegisterUserCallback{
            override fun onUserRegistrationResponse(registerUserResponse: RegistrationResponse) {
                callback.onUserRegistrationResponse(registerUserResponse)
            }

            override fun onUserRegistrationError(errorCode:Int) {
                callback.onUserRegistrationError(errorCode)
            }

        })
    }
    fun updateUserDetails(userDto:UserDto,userId:Long,callback: UpdateUserCallback){
        viewModelScope.launch {
            userRepository.updateUser(userDto, userId, object : UpdateUserCallback {
                override fun onUserUpdated(updatedUserDto: UserDto) {
                    callback.onUserUpdated(updatedUserDto)
                }

                override fun onUpdateUserError() {
                    callback.onUpdateUserError()
                }
            })
        }
    }

    fun checkUserName(username:String,callback: UsernameAvailabilityCallback){
        userRepository.checkUsername(username,object :UsernameAvailabilityCallback{
            override fun onUsernameAvailable(isAvailable: Boolean) {
                callback.onUsernameAvailable(isAvailable)
            }

            override fun onUsernameCheckError() {
                callback.onUsernameCheckError()
            }

        })
    }
    fun checkEmail(email:String,callback: UsernameAvailabilityCallback){
        userRepository.checkEmail(email,object :UsernameAvailabilityCallback{
            override fun onUsernameAvailable(isAvailable: Boolean) {
                callback.onUsernameAvailable(isAvailable)
            }

            override fun onUsernameCheckError() {
                callback.onUsernameCheckError()
            }

        })
    }

    fun getUser(token:String,callback: UpdateUserCallback){
        userRepository.getUser(token,object :UpdateUserCallback{
            override fun onUserUpdated(updatedUserDto: UserDto) {
                callback.onUserUpdated(updatedUserDto)
            }
            override fun onUpdateUserError() {
                callback.onUpdateUserError()
            }
        })
    }

    fun getUserInfo(username:String,callback: UpdateUserCallback){
        userRepository.getUserInfo(username,object :UpdateUserCallback{
            override fun onUserUpdated(updatedUserDto: UserDto) {
                callback.onUserUpdated(updatedUserDto)
            }
            override fun onUpdateUserError() {
                callback.onUpdateUserError()
            }
        })
    }

    fun getUserById(userId:Long,callback: UpdateUserCallback){
        userRepository.getUserById(userId,object :UpdateUserCallback{
            override fun onUserUpdated(updatedUserDto: UserDto) {
                callback.onUserUpdated(updatedUserDto)
            }
            override fun onUpdateUserError() {
                callback.onUpdateUserError()
            }
        })
    }


//    fun updateUserResponseInSharedPreferences(updatedUserResponse: UserDto) {
//        // Initialize Shared Preferences with the appropriate file name and mode
//        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
//
//        // Convert the user response to a JSON string using Gson
//        val gson = Gson()
//        val userJson = gson.toJson(updatedUserResponse)
//
//        // Get the Shared Preferences editor and update the user response
//        val editor = sharedPref.edit()
//        editor.putString("user_details", userJson)
//        editor.apply()
//    }


}