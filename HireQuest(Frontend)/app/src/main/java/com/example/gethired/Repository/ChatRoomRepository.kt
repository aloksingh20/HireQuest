package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Chat
import com.example.gethired.entities.ChatRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ChatRoomRepository(tokenManager: TokenManager) {
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

    suspend fun getAllChat(userId: Long): ApiResult<List<ChatRoom>> {
        return safeApiCall { retrofitAPI.getAllChattingList(userId) .execute()}
    }

    suspend fun sendChatRequest(senderId: Long, receiverId: Long, sampleMessage: Chat): ApiResult<Boolean> {
        return safeApiCall { retrofitAPI.sendChatRequest(senderId, receiverId, sampleMessage).execute() }
    }

    suspend fun acceptChatRequest(chatRoomId: Long): ApiResult<Boolean> {
        return safeApiCall { retrofitAPI.acceptChatRequest(chatRoomId).execute() }
    }

    suspend fun deleteChatRequest(chatRoomId: Long): ApiResult<Boolean> {
        return safeApiCall { retrofitAPI.deleteChatRequest(chatRoomId).execute() }
    }
}
