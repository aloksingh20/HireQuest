package com.example.gethired.Repository

import com.example.gethired.Callback.RegisterUserCallback
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.Callback.UsernameAvailabilityCallback
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.RegisterDto
import com.example.gethired.entities.RegistrationResponse
import com.example.gethired.entities.UserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(val tokenManager: TokenManager) {

    private val retrofitAPI: ApiService = RetrofitClient(tokenManager).getApiService()

    fun createUser(registerDto: RegisterDto, callback: RegisterUserCallback){
        val call :Call<RegistrationResponse> = retrofitAPI.createUser(registerDto)
        call.enqueue(object :Callback<RegistrationResponse>{
            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
            ) {
                if(response.isSuccessful&&response.body()!=null){
                    callback.onUserRegistrationResponse(response.body()!!)
                }else{
                    callback.onUserRegistrationError(response.code())
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                callback.onUserRegistrationError(t.hashCode())
            }

        })
    }
    fun updateUser(userDto: UserDto, userId:Long, callback: UpdateUserCallback){
        val call: Call<UserDto> =retrofitAPI.updateUser(userDto,userId)
        call.enqueue(object:Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    response.body()?.let { callback.onUserUpdated(it) }
                }else{
                    callback.onUpdateUserError()
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callback.onUpdateUserError()
            }

        })
    }
    fun checkUsername(username: String, callback: UsernameAvailabilityCallback) {
        val call: Call<Boolean> = retrofitAPI.checkIfUserIsPresent(username)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() != null) {
                    val isAvailable = response.body()!!
                    callback.onUsernameAvailable(isAvailable)
                } else {
                    callback.onUsernameCheckError()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onUsernameCheckError()
            }
        })
    }
    fun checkEmail(email: String, callback: UsernameAvailabilityCallback) {
        val call: Call<Boolean> = retrofitAPI.checkIfEmailIsPresent(email)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() != null) {
                    val isAvailable = response.body()!!
                    callback.onUsernameAvailable(isAvailable)
                } else {
                    callback.onUsernameCheckError()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onUsernameCheckError()
            }
        })
    }

    public fun getUser(token:String,callback:UpdateUserCallback){
        val call :Call<UserDto> = retrofitAPI.getUser(token)
        call.enqueue(object :Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    callback.onUserUpdated(response.body()!!)
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callback.onUpdateUserError()
            }

        })
    }

    public fun getUserInfo(username:String,callback:UpdateUserCallback){
        val call :Call<UserDto> = retrofitAPI.getUserInfo(username)
        call.enqueue(object :Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    callback.onUserUpdated(response.body()!!)
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callback.onUpdateUserError()
            }

        })
    }
    public fun getUserById(userId:Long,callback:UpdateUserCallback){
        val call :Call<UserDto> = retrofitAPI.getUserInfoById(userId)
        call.enqueue(object :Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    if(response.body()!=null){
                        callback.onUserUpdated(response.body()!!)
                    }
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callback.onUpdateUserError()
            }

        })
    }



}