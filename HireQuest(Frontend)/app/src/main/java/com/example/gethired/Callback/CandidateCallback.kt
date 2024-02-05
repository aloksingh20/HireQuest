package com.example.gethired.Callback
import com.example.gethired.entities.UserProfileDto

interface CandidateCallback {
    fun onCandidateAdded(userProfileDto: UserProfileDto)
    fun onCandidateError()
}