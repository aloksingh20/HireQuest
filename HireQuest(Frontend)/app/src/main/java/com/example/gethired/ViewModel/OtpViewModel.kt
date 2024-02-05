package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Callback.ResponseCallback
import com.example.gethired.Repository.OtpRepository
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.OtpResponse
import com.example.gethired.entities.Pdf
import com.example.gethired.entities.Response

class OtpViewModel() : ViewModel() {
    private val otpRepository= OtpRepository()

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdOtp = MutableLiveData<OtpResponse?>(null)
    val createdOtp: LiveData<OtpResponse?> = _createdOtp

    private val _verifyOtp = MutableLiveData<Response?>(null)
    val verifyOtp: LiveData<Response?> = _verifyOtp
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
    suspend fun sendOtp(email:String){
       executeApiCall(
           call = { otpRepository.sendOtp(email) },
           onSuccess = { _createdOtp.value = it },
           onError = { _error.value = it }
       )
    }
    suspend fun verifyOtp(otpCode:String,email: String){
        executeApiCall(
            call = { otpRepository.verify(otpCode,email) },
            onSuccess = { _verifyOtp.value = it },
            onError = { _error.value = it }
        )
    }

}