package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Callback.CommonCallback
import com.example.gethired.Repository.LanguageRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult

class LanguageViewModel(tokenManager: TokenManager) : ViewModel() {
    private var languageRepository = LanguageRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _addedLanguages = MutableLiveData<List<String>>(null)
    val addedLanguages: LiveData<List<String>> = _addedLanguages

    private val _languageList = MutableLiveData<List<String>>(null)
    val languageList: LiveData<List<String>> = _languageList

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
    suspend fun addLanguage(language: List<String>,userProfileId:Long){
        executeApiCall(
            call = { languageRepository.addLanguage(language,userProfileId) },
            onSuccess = { _addedLanguages.value = it },
            onError = { _error.value = it }
        )
    }


    suspend fun getAllLanguage(userProfileId: Long){
        executeApiCall(
            call = { languageRepository.getAllLanguages(userProfileId) },
            onSuccess = { _languageList.value = it },
            onError = { _error.value = it }
        )
    }

}