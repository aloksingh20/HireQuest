package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Project
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProjectRepository(tokenManager: TokenManager) {
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

    suspend fun addProject(userProfileId:Long, project: Project):ApiResult<Project>{
       return safeApiCall { retrofitAPI.addProject(project,userProfileId).execute() }
    }
    suspend fun updateProject(projectId:Long, project: Project) : ApiResult<Project>{
       return safeApiCall { retrofitAPI.updateProject(project,projectId) .execute()}
    }

    suspend fun deleteProject(projectId: Long):ApiResult<Boolean>{
        return safeApiCall { retrofitAPI.deleteProject(projectId).execute() }
    }

    suspend fun getAllProject(userProfileId: Long): ApiResult<List<Project>> {
        return safeApiCall { retrofitAPI.getAllProject(userProfileId) .execute()}
    }
}