package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.RecentSearch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RecentSearchRepository(tokenManager: TokenManager) {

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
    suspend fun addRecentSearch(recentSearchDto: RecentSearch):ApiResult<RecentSearch?> {
        return safeApiCall { retrofitAPI.addRecentSearch(recentSearchDto) .execute()}
    }
    suspend fun getAllRecentSearches(userId: Long):ApiResult<List<RecentSearch>?> {
       return safeApiCall { retrofitAPI.getAllRecentSearch(userId) .execute()}
    }

    suspend fun deleteRecentSearch(recentSearchId: Long):ApiResult<Boolean?> {
        return safeApiCall { retrofitAPI.deleteRecentSearch(recentSearchId).execute() }
    }


    suspend fun deleteAllRecentSearches(userId: Long):ApiResult<Boolean?> {
        return safeApiCall { retrofitAPI.deleteAllRecentSearch(userId).execute() }
    }
}