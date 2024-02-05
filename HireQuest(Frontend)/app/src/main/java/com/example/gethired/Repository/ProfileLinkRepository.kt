package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProfileLinkRepository(tokenManager: TokenManager) {
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
    suspend fun addProfileLink(profile: Profile, userProfileId:Long):ApiResult<Profile>{
        return safeApiCall { retrofitAPI.addProfileLink(profile, userProfileId).execute() }
    }

    suspend fun updateProfileLink(profile: Profile, profileLinkId:Long):ApiResult<Profile>{
        return safeApiCall { retrofitAPI.updateProfileLink(profile,profileLinkId).execute() }
    }

    suspend fun deleteProfileLink(profileLinkId:Long):ApiResult<Boolean>{
        return safeApiCall { retrofitAPI.deleteProfileLink(profileLinkId).execute() }
    }
    suspend fun getAllProfileLink(userProfileId: Long): ApiResult<List<Profile>> {
        return safeApiCall { retrofitAPI.getAllProfileLink(userProfileId ).execute() }
    }

}