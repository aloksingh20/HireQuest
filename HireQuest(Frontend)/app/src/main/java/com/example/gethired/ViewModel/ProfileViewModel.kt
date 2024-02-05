package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.ProfileLinkRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Profile

class ProfileViewModel(tokenManager: TokenManager) : ViewModel() {
    private val profileLinkRepository = ProfileLinkRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdProfileLink = MutableLiveData<Profile>(null)
    val createdProfileLink: LiveData<Profile> = _createdProfileLink

    private val _updatedProfileLink = MutableLiveData<Profile>(null)
    val updatedProfileLink: LiveData<Profile> = _updatedProfileLink

    private val _profileLinkList = MutableLiveData<List<Profile>?>(null)
    val profileLinkList: LiveData<List<Profile>?> = _profileLinkList

    private suspend fun <T> executeApiCall(
        call: suspend () -> ApiResult<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
        _loading.value = true
        _error.value = null

        try {
            when (val result = call()) {
                is ApiResult.Success -> onSuccess(result.data!!)
                is ApiResult.Error -> onError(result.exception.localizedMessage ?: "Unknown error")
            }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Unknown error")
        } finally {
            _loading.value = false
        }
    }

    suspend fun addProfileLink(profile: Profile, userProfileId: Long) {
        executeApiCall(
            call = { profileLinkRepository.addProfileLink(profile, userProfileId) },
            onSuccess = { _createdProfileLink.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun updateProfileLink(profile: Profile, profileLinkId: Long) {
        executeApiCall(
            call = { profileLinkRepository.updateProfileLink(profile, profileLinkId) },
            onSuccess = { _updatedProfileLink.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun deleteProfileLink(profileLinkId: Long) {
        profileLinkRepository.deleteProfileLink(profileLinkId)
    }

    suspend fun getAllProfileLink(userProfileId: Long) {
        executeApiCall(
            call = { profileLinkRepository.getAllProfileLink(userProfileId) },
            onSuccess = { _profileLinkList.value = it },
            onError = { _error.value = it }
        )
    }

}