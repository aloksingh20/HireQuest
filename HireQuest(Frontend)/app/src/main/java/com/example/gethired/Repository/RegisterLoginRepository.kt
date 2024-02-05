package com.example.gethired.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gethired.Callback.LoginCallback
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.Token.LoginResponse
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.GoogleSignInClass
import com.example.gethired.entities.LoginDto
import com.example.gethired.entities.UserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterLoginRepository {

    private val retrofitAPI: ApiService = RetrofitClient().getApiService()

    fun loginUser(loginDto: LoginDto, callback: LoginCallback){
        val call : Call<LoginResponse> =retrofitAPI.loginUser(loginDto)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    callback.onResponseLogin(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onErrorLogin()
            }

        })
    }
    fun loginUserUsingGoogle(loginDto: GoogleSignInClass, callback: LoginCallback){
        val call : Call<LoginResponse> =retrofitAPI.googleSignIn(loginDto)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    callback.onResponseLogin(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onErrorLogin()
            }

        })
    }
    public fun updatePassword(email:String, password:String,callback: UpdateUserCallback){
        val call:Call<UserDto> =retrofitAPI.updatePassword(email,password)
        call.enqueue(object :Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful&&response.body()!=null){
                    callback.onUserUpdated(response.body()!!)
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callback.onUpdateUserError()
            }

        })
    }

    fun getFcmToken(username:String):LiveData<Boolean>{
        val token = MutableLiveData<Boolean>()
        val call :Call<Boolean> = retrofitAPI.getFcmToken(username)
        call.enqueue(object : Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if(response.isSuccessful){
                    token.value=response.body()
                    Log.d("fcmresponse",response.toString())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d("fcm-fail-response",t.toString())
            }

        })
        return token
    }

    fun logout(username: String): LiveData<Boolean> {
        val isLogout = MutableLiveData<Boolean>()
        val call :Call<Boolean> = retrofitAPI.logout(username)
        call.enqueue(object : Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if(response.isSuccessful){
                    isLogout.value=response.body()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                isLogout.value=false
            }

        })
        return isLogout
    }
}