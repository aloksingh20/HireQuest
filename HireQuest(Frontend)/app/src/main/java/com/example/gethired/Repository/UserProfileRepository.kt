package com.example.gethired.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gethired.Callback.CandidateCallback
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.UserProfileDto
import com.example.gethired.entities.UserProfile
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileRepository(tokenManager: TokenManager, private val context: Context){
    private val retrofitAPI: ApiService = RetrofitClient(tokenManager).getApiService()


    fun updateUserProfile(userProfileId:Long, userProfileDto: UserProfileDto, callback: CandidateCallback){
        val call :Call<UserProfileDto> = retrofitAPI.updateUserProfile(userProfileId,userProfileDto)

        call.enqueue(object :Callback<UserProfileDto>{
            override fun onResponse(
                call: Call<UserProfileDto>,
                response: Response<UserProfileDto>
            ) {
                if(response.isSuccessful) {
                    if(response.body()!=null){
                        callback.onCandidateAdded(response.body()!!)
                    }else{
                        callback.onCandidateError()
                    }
                }
            }
            override fun onFailure(call: Call<UserProfileDto>, t: Throwable) {
                callback.onCandidateError()
            }
        })
    }

    fun getCandidateProfileDto(userId:Long, callback: CandidateCallback){
        val call: Call<UserProfileDto> = retrofitAPI.getCandidateProfile(userId)
        call.enqueue(object : Callback<UserProfileDto> {
            override fun onResponse(call: Call<UserProfileDto>, response: Response<UserProfileDto>) {
                if (response.isSuccessful) {
                    val candidateProfileDto = response.body()
                    if (candidateProfileDto != null) {
                        callback.onCandidateAdded(candidateProfileDto)
                    }
                } else {
                    callback.onCandidateError()
                }
            }
            override fun onFailure(call: Call<UserProfileDto>, t: Throwable) {
                callback.onCandidateError()
            }
        })
    }

    fun getAllUserProfile(newText: String,role:String,pageNo:Int, pageSize:Int,sortBy:String,sortDir:String): MutableLiveData<List<UserProfile>> {

        val userProfiles=MutableLiveData<List<UserProfile>>()

        val call: Call<List<String>> = retrofitAPI.getAllUserProfile(newText,role,pageNo,pageSize,sortBy,sortDir)
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val candidateProfiles = response.body()!!
                    val profiles= mutableListOf<UserProfile>()
                    for (i in candidateProfiles){
                        val parsedUsers = Gson().fromJson(i, UserProfile::class.java)
                        profiles.add(parsedUsers)
                    }
                    userProfiles.value=profiles
                    Log.d("repository", response.body()!!.size.toString())
                } else {
                    userProfiles.value = mutableListOf()
                }
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.d("user-profile-list",t.message.toString())
            }
        })
        return userProfiles
    }
}