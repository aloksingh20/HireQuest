package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Notification
import com.example.gethired.entities.NotificationPreference
import com.example.gethired.entities.NotificationRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException

class NotificationRepository(tokenManager: TokenManager) {

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
    suspend fun sendNotification(request: NotificationRequest):ApiResult<Boolean?>{
        return safeApiCall { retrofitAPI.sendNotification(request) .execute()}
    }

    suspend fun updateNotification(notificationId:Long): ApiResult<Notification>{
        return safeApiCall { retrofitAPI.updateNotification(notificationId) .execute()}
    }

    suspend fun deleteNotification(notificationId: Long):ApiResult<Boolean>{
       return safeApiCall { retrofitAPI.deleteNotification(notificationId).execute() }
    }

    suspend fun getAllNotification(receiverUsername:String):ApiResult<List<Notification>>{
       return safeApiCall { retrofitAPI.getNotification(receiverUsername) .execute()}
    }

    suspend fun getAllNotificationPreference(userId: Long):ApiResult<List<NotificationPreference>>{
       return safeApiCall { retrofitAPI.getNotificationPreference(userId).execute() }
    }

    suspend fun updateNotificationPreference(notificationType:String, userId: Long):ApiResult<Boolean>{
        return safeApiCall { retrofitAPI.updateNotificationPreference(notificationType,userId).execute() }
    }

    suspend fun muteAllNotification(userId: Long):ApiResult<Boolean>{
        return safeApiCall { retrofitAPI.muteAllNotification(userId).awaitResponse() }
    }
    suspend fun unMuteAllNotification(userId: Long):ApiResult<Boolean>{
        return safeApiCall { retrofitAPI.unMuteAllNotification(userId).awaitResponse() }
    }
}