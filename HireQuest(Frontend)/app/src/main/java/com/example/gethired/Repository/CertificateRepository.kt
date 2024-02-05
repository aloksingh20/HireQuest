package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Certificate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class CertificateRepository(tokenManager: TokenManager) {

    private val retrofitAPI: ApiService = RetrofitClient(tokenManager).getApiService()

    private suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> Response<T>): ApiResult<T> {
        return try {
            withContext(dispatcher) {
                val response = apiCall()
                if (response.isSuccessful) {
                    ApiResult.Success(response.body())
                } else {
                    ApiResult.Error(HttpException(response))
                }
            }
        } catch (e: IOException) {
            ApiResult.Error(e)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun addCertificate(candidateId:Long, certificate: Certificate):ApiResult<Certificate?>{
        return safeApiCall { retrofitAPI.addCertificate(certificate,candidateId).execute() }
    }

    suspend fun updateCertificate(certificateId:Long,certificate:Certificate):ApiResult<Certificate?>{
        return safeApiCall { retrofitAPI.updateCertificate(certificateId,certificate).execute() }
    }

    suspend fun deleteCertificate(certificateId: Long):ApiResult<Boolean?>{
        return safeApiCall { retrofitAPI.deleteCertificate(certificateId).execute() }
    }

    suspend fun getAllCertificate(userProfileId: Long):ApiResult<List<Certificate>?>{
        return safeApiCall { retrofitAPI.getAllCertificate(userProfileId).execute() }
    }
}