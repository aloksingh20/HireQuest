package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.PdfRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Pdf
import okhttp3.MultipartBody

class PdfViewModel(tokenManager: TokenManager): ViewModel() {

    private val pdfRepository=PdfRepository(tokenManager)
    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdPdf = MutableLiveData<Pdf?>(null)
    val createdPdf: LiveData<Pdf?> = _createdPdf

    private val _updatedPdf = MutableLiveData<Pdf?>(null)
    val updatedPdf: LiveData<Pdf?> = _updatedPdf

    private val _pdfList = MutableLiveData<List<Pdf>?>(null)
    val pdfList : LiveData<List<Pdf>?> = _pdfList

    private val _isPdfDeleted = MutableLiveData(false)
    val isPdfDeleted :LiveData<Boolean> = _isPdfDeleted

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
    suspend fun addPdf(userProfileId:Long, file: MultipartBody.Part){
        executeApiCall(
            call = { pdfRepository.addPdf(userProfileId,file) },
            onSuccess = { _createdPdf.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun deletePdf(pdfId:Long){
        executeApiCall(
            call = { pdfRepository.deletePdf(pdfId) },
            onSuccess = { _isPdfDeleted.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun getAllPdf(userProfileId: Long){
        executeApiCall(
            call = { pdfRepository.getAllPdf(userProfileId) },
            onSuccess = { _pdfList.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun downloadPdf(pdfId: Long){
         pdfRepository.downloadPdf(pdfId)
    }
}