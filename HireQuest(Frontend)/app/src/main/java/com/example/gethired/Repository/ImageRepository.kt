package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Image
import com.example.gethired.exception.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class ImageRepository(tokenManager: TokenManager){

    private val apiService  = RetrofitClient(tokenManager).getApiService()

    suspend fun addProfileImage(userId: Long, file: MultipartBody.Part): ResponseBody {
        return withContext(Dispatchers.IO) {
            val response: Response<ResponseBody> = apiService.uploadImage(userId, file).awaitResponse()
            if (response.isSuccessful) {
                response.body() ?: throw ResponseException("Image not found in response")
            } else {
                throw ResponseException("Failed to upload image: ${response.errorBody()?.string()}")
            }
        }
    }

    suspend fun getUserProfilePicture(userId: Long): ResponseBody {
        return withContext(Dispatchers.IO) {
            val response: Response<ResponseBody> = apiService.getUserProfilePicture(userId).awaitResponse()
            if (response.isSuccessful) {
                response.body() ?: throw ResponseException("Image not found in response")
            } else {
                throw ResponseException("Failed to get image: ${response.errorBody()?.string()}")
            }
        }
    }

}
