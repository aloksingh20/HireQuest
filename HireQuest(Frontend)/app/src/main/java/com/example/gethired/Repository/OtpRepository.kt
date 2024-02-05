package com.example.gethired.Repository

import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.OtpResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException

class OtpRepository() {
    private val retrofitAPI: ApiService = RetrofitClient().getApiService()
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

    suspend fun sendOtp(email:String):ApiResult<OtpResponse>{
        return safeApiCall { retrofitAPI.sendOtp(email) .awaitResponse()}
    }

    suspend fun verify(otpCode:String,email:String):ApiResult<com.example.gethired.entities.Response> {
        return safeApiCall { retrofitAPI.verifyOtp(otpCode,email).awaitResponse() }
    }

}