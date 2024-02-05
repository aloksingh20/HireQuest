package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Callback.AppreciationCallback
import com.example.gethired.Repository.AppreciationRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Appreciation
import com.example.gethired.entities.Education
import com.example.gethired.entities.Meeting

class AppreciationViewModel (tokenManager: TokenManager):ViewModel(){
    private val appreciationRepository=AppreciationRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdAppreciation = MutableLiveData<Appreciation?>(null)
    val createdAppreciation: LiveData<Appreciation?> = _createdAppreciation

    private val _updatedAppreciation = MutableLiveData<Appreciation?>(null)
    val updatedAppreciation: LiveData<Appreciation?> = _updatedAppreciation

    private val _appreciationList = MutableLiveData<List<Appreciation>?>(null)
    val appreciationList : LiveData<List<Appreciation>?> = _appreciationList

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

    suspend fun addAppreciation(appreciation: Appreciation,userProfileId: Long){
        executeApiCall(
            call = { appreciationRepository.addAppreciation(appreciation,userProfileId)},
            onSuccess = { _createdAppreciation.value = it },
            onError = { _error.value = it}
        )
    }

    suspend fun updateAppreciation(appreciation: Appreciation,appreciationId: Long){
        executeApiCall(
            call = { appreciationRepository.updateAppreciation(appreciation,appreciationId)},
            onSuccess = { _createdAppreciation.value = it },
            onError = { _error.value = it}
        )
    }

    suspend fun getAllAppreciation(userProfileId: Long){
        executeApiCall(
            call = { appreciationRepository.getAllAppreciation(userProfileId)},
            onSuccess = { _appreciationList.value = it },
            onError = { _error.value = it}
        )
    }

   suspend fun deleteAppreciation(appreciationId: Long){
        appreciationRepository.deleteAppreciation(appreciationId)
    }

}
