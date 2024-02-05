package com.example.gethired.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.RecentSearchRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Project
import com.example.gethired.entities.RecentSearch
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader

class RecentSearchViewModel(tokenManager: TokenManager):ViewModel() {

    private val recentSearchRepository =RecentSearchRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdRecentSearch = MutableLiveData<RecentSearch>(null)
    val createdRecentSearch: LiveData<RecentSearch> = _createdRecentSearch

    private val _updatedRecentSearch = MutableLiveData<RecentSearch>(null)
    val updatedRecentSearch: LiveData<RecentSearch> = _updatedRecentSearch

    private val _recentSearchList = MutableLiveData<List<RecentSearch>?>(null)
    val recentSearchList: LiveData<List<RecentSearch>?> = _recentSearchList
    // LiveData for loading state and errors
    private val _isDeleted = MutableLiveData(false)
    val isDeleted: LiveData<Boolean> = _isDeleted
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
    suspend fun addRecentSearch(recentSearchDto: RecentSearch) {
        executeApiCall(
            call = { recentSearchRepository.addRecentSearch(recentSearchDto) },
            onSuccess = { _createdRecentSearch.value = it },
            onError = { _error.value = it }
        )
    }
    suspend fun getAllRecentSearches(userId: Long) {
        executeApiCall(
            call = { recentSearchRepository.getAllRecentSearches(userId) },
            onSuccess = { _recentSearchList.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun deleteRecentSearch(recentSearchId: Long) {
        executeApiCall(
            call = { recentSearchRepository.deleteRecentSearch(recentSearchId) },
            onSuccess = { _isDeleted.value = it },
            onError = { _error.value = it }
        )
    }


    suspend fun deleteAllRecentSearches(userId: Long) {
        executeApiCall(
            call = { recentSearchRepository.deleteAllRecentSearches(userId) },
            onSuccess = { _isDeleted.value = it },
            onError = { _error.value = it }
        )
    }

}