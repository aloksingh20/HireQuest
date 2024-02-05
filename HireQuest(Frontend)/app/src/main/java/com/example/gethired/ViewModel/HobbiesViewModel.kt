package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Callback.CommonCallback
import com.example.gethired.Repository.HobbiesRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Experience

class HobbiesViewModel (tokenManager: TokenManager): ViewModel(){
    private val hobbiesRepository=HobbiesRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _addedHobbies = MutableLiveData<List<String>>(null)
    val addedHobbies: LiveData<List<String>> = _addedHobbies

    private val _hobbiesList = MutableLiveData<List<String>>(null)
    val hobbiesList: LiveData<List<String>> = _hobbiesList

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
    suspend fun addHobbies(hobbies: List<String>,userProfileId:Long){
        executeApiCall(
            call = { hobbiesRepository.addHobbies(hobbies,userProfileId) },
            onSuccess = { _addedHobbies.value = it },
            onError = { _error.value = it }
        )
    }


    suspend fun getAllHobbies(userProfileId: Long){
        executeApiCall(
            call = { hobbiesRepository.getAllHobbies(userProfileId) },
            onSuccess = { _hobbiesList.value = it },
            onError = { _error.value = it }
        )
    }
}
