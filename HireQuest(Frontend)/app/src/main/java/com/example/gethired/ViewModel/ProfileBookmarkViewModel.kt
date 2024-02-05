package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.ProfileBookmarkRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.entities.UserProfile

class ProfileBookmarkViewModel(tokenManager: TokenManager):ViewModel() {
    private val repository=ProfileBookmarkRepository(tokenManager )
//
//    private val _bookmarkOperationResult = repository.bookmarkOperationResult
//
//    suspend fun bookmarkProfile(hrId: Long, userProfileId: Long) {
//        repository.bookmarkProfile(hrId, userProfileId)
//    }
//
//    suspend fun removeBookmarkProfile(hrId: Long, userProfileId: Long) {
//        repository.removeBookmarkProfile(hrId, userProfileId)
//    }
//    suspend fun getAllBookmarkedProfile(hrId: Long):LiveData<List<UserProfile>>{
//        return repository.getAllBookmarkedProfile(hrId)
//    }
}