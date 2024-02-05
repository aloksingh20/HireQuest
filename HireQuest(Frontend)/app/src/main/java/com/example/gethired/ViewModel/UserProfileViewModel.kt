package com.example.gethired.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.gethired.Callback.CandidateCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gethired.Repository.UserProfileRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.entities.UserProfileDto
import com.example.gethired.entities.UserProfile
import kotlinx.coroutines.launch

class UserProfileViewModel(tokenManager: TokenManager,context: Context) :ViewModel() {
    private val userProfileRepository= UserProfileRepository(tokenManager, context)

    fun updateUserProfile(userProfileId:Long,userProfileDto: UserProfileDto,callback: CandidateCallback){
        userProfileRepository.updateUserProfile(userProfileId,userProfileDto,object :CandidateCallback{
            override fun onCandidateAdded(userProfileDto: UserProfileDto) {
                callback.onCandidateAdded(userProfileDto)
            }

            override fun onCandidateError() {
                callback.onCandidateError()
            }

        })
    }

    fun getCandidateDto(userId:Long,callback: CandidateCallback){
        viewModelScope.launch {
            userProfileRepository.getCandidateProfileDto(userId,object : CandidateCallback {
                override fun onCandidateAdded(userProfileDto: UserProfileDto) {
                    callback.onCandidateAdded(userProfileDto)
                }

                override fun onCandidateError() {
                    callback.onCandidateError()
                }

            })
        }
    }

    fun getAllCandidateProfile(newText: String,role:String,pageNo:Int, pageSize:Int,sortBy:String,sortDir:String): LiveData<List<UserProfile>>{
        return userProfileRepository.getAllUserProfile(newText,role,pageNo,pageSize,sortBy,sortDir)
    }


//    val candidateProfiles = response.body()!!
//                    val parsedUsers = Gson().fromJson(response.toString(), Array<UserProfile>::class.java).toList()

}