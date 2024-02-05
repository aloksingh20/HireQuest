package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.ExperienceRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Experience

class ExperienceViewModel(tokenManager: TokenManager) :ViewModel() {
    private val experienceRepository= ExperienceRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdExperience = MutableLiveData<Experience>(null)
    val createdExperience: LiveData<Experience> = _createdExperience

    private val _updatedExperience = MutableLiveData<Experience>(null)
    val updatedExperience: LiveData<Experience> = _updatedExperience

    private val _experienceList = MutableLiveData<List<Experience>?>(null)
    val experienceList : LiveData<List<Experience>?> = _experienceList

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
    suspend fun addExperience(experience: Experience, userProfileId:Long){
        executeApiCall(
            call = { experienceRepository.addExperience(userProfileId,experience) },
            onSuccess = { _createdExperience.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun updateExperience(experience: Experience, experienceId: Long){
        executeApiCall(
            call = { experienceRepository.updateExperience(experienceId,experience) },
            onSuccess = { _updatedExperience.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun getAllExperience(userProfileId: Long) {
        executeApiCall(
            call = { experienceRepository.getAllExperience(userProfileId) },
            onSuccess = { _experienceList.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun deleteExperience(experienceId: Long){
        experienceRepository.deleteExperience(experienceId)
    }
}