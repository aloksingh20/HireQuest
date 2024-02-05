package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.CertificateRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Appreciation
import com.example.gethired.entities.Certificate

class CertificateViewModel(tokenManager: TokenManager) :ViewModel() {
    private val certificateRepository=CertificateRepository(tokenManager)
    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdCertificate = MutableLiveData<Certificate?>(null)
    val createdCertificate: LiveData<Certificate?> = _createdCertificate

    private val _updatedCertificate = MutableLiveData<Certificate?>(null)
    val updatedCertificate: LiveData<Certificate?> = _updatedCertificate

    private val _certificateList = MutableLiveData<List<Certificate>?>(null)
    val certificateList : LiveData<List<Certificate>?> = _certificateList

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
    suspend fun addCertificate(certificate: Certificate,userProfileId:Long){
       executeApiCall(
           call = { certificateRepository.addCertificate(userProfileId,certificate)},
           onSuccess = { _createdCertificate.value = it },
           onError = { _error.value = it }
       )
    }

    suspend fun updateCertificate(certificate: Certificate,certificateId:Long){
        executeApiCall(
            call = { certificateRepository.updateCertificate(certificateId,certificate)},
            onSuccess = { _updatedCertificate.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun getAllCertificate(userProfileId: Long){
        executeApiCall(
            call = { certificateRepository.getAllCertificate(userProfileId ) },
            onSuccess = { _certificateList.value = it },
            onError = { _error.value = it }
        )
    }

     suspend fun deleteCertificate(certificateId: Long){
        certificateRepository.deleteCertificate(certificateId)
    }
}