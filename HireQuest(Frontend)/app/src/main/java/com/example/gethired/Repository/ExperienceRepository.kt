package com.example.gethired.Repository

import androidx.lifecycle.MutableLiveData
import com.example.gethired.Callback.ExperienceCallback
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Experience
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ExperienceRepository(tokenManager: TokenManager) {
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
    suspend fun addExperience(userProfileId:Long, experience: Experience):ApiResult<Experience?>{
        return safeApiCall { retrofitAPI.addExperience(experience,userProfileId).execute() }
    }

    suspend fun updateExperience(experienceId:Long,experience: Experience):ApiResult<Experience?>{
       return safeApiCall { retrofitAPI.updateExperience(experienceId,experience).execute() }
    }

    suspend fun deleteExperience(experienceId: Long):ApiResult<Boolean>{
        return safeApiCall { retrofitAPI.deleteExperience(experienceId) .execute()}
    }

    suspend fun getAllExperience(userProfileId: Long): ApiResult<List<Experience>> {
        return safeApiCall { retrofitAPI.getAllExperience(userProfileId).execute() }
    }

}