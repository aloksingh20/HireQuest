package com.example.gethired.Repository

import androidx.lifecycle.MutableLiveData
import com.example.gethired.Callback.AppreciationCallback
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Appreciation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AppreciationRepository(tokenManager: TokenManager) {

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

    suspend fun addAppreciation(appreciation: Appreciation,userProfileId: Long):ApiResult<Appreciation?>{
        return safeApiCall { retrofitAPI.addAppreciation(appreciation,userProfileId).execute() }
    }

    suspend fun updateAppreciation(appreciation: Appreciation,appreciationId: Long):ApiResult<Appreciation?>{
        return safeApiCall { retrofitAPI.updateAppreciation(appreciation,appreciationId).execute() }
    }

    suspend fun getAllAppreciation(userProfileId: Long):ApiResult<List<Appreciation>?>{
        return safeApiCall { retrofitAPI.getAllAppreciation(userProfileId).execute() }
    }

    suspend fun deleteAppreciation(appreciationId: Long):ApiResult<Boolean>{
         return safeApiCall { retrofitAPI.deleteAppreciation(appreciationId).execute() }
    }


}