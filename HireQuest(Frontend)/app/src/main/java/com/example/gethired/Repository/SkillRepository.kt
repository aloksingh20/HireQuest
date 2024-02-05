package com.example.gethired.Repository

import com.example.gethired.Callback.CommonCallback
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SkillRepository(tokenManager: TokenManager) {

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
    suspend fun getAllSkills(userProfileId: Long):ApiResult<List<String>>{
        return safeApiCall { retrofitAPI.getAllSkill(userProfileId).execute() }
    }

    suspend fun addSkill(skill: List<String>, userProfileId:Long):ApiResult<List<String>> {
        return safeApiCall { retrofitAPI.addSkill(skill,userProfileId) .execute()}
    }
}