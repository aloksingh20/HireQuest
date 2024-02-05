package com.example.gethired.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gethired.SealedClasses.Errors
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class ProfileBookmarkRepository(tokenManager: TokenManager) {
//    private val retrofitApi: ApiService =
//        RetrofitClient(tokenManager).getApiService()
//
//    private val _bookmarkOperationResult = MutableLiveData<CommonResult>()
//    val bookmarkOperationResult: LiveData<CommonResult> = _bookmarkOperationResult
//
//    suspend fun bookmarkProfile(hrId: Long, userProfileId: Long) {
//        suspendCancellableCoroutine<Unit> { continuation ->
//            val call = retrofitApi.addBookmarkProfile(hrId, userProfileId)
//            call.enqueue(object : Callback<Boolean> {
//                override fun onResponse(
//                    call: Call<Boolean>,
//                    response: Response<Boolean>
//                ) {
//                    _bookmarkOperationResult.postValue(if (response.isSuccessful && response.body() != null) CommonResult.Success else CommonResult.Failure(handleError(response)))
//                    continuation.cancel()
//                }
//
//                override fun onFailure(call: Call<Boolean>, t: Throwable) {
//                    _bookmarkOperationResult.postValue(CommonResult.Failure(Errors.Unknown(t.message!!)))
//                    continuation.cancel()
//                }
//            })
//        }
//    }
//
//    suspend fun removeBookmarkProfile(hrId: Long, userProfileId: Long) {
//        suspendCancellableCoroutine<Unit> { continuation ->
//            val call = retrofitApi.removeBookmarkProfile(hrId, userProfileId)
//            call.enqueue(object : Callback<Boolean> {
//                override fun onResponse(
//                    call: Call<Boolean>,
//                    response: Response<Boolean>
//                ) {
//                    _bookmarkOperationResult.postValue(if (response.isSuccessful && response.body() != null) CommonResult.Success else CommonResult.Failure(handleError(response)))
//                    continuation.cancel()
//                }
//
//                override fun onFailure(call: Call<Boolean>, t: Throwable) {
//                    _bookmarkOperationResult.postValue(CommonResult.Failure(Errors.Unknown(t.message!!)))
//                    continuation.cancel()
//                }
//            })
//        }
//    }
//
//    suspend fun getAllBookmarkedProfile(hrId: Long): LiveData<List<UserProfile>> {
//        val bookmarkedUserProfile = MutableLiveData<List<UserProfile>>()
//        try {
//            val response = retrofitApi.getBookmarkProfiles(hrId).await()
//            if (response.isNotEmpty()) {
//                bookmarkedUserProfile.value = response
//            } else {
//                // Handle unsuccessful response (e.g., empty or error)
//                bookmarkedUserProfile.value = emptyList()
//            }
//        } catch (e: Exception) {
//            bookmarkedUserProfile.value = emptyList()
//        }
//        return bookmarkedUserProfile
//    }
//
//    private fun handleError(response: Response<*>) : Errors {
//        return if (!response.isSuccessful) {
//            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//            when (response.code()) {
//                400 -> Errors.BadRequest(errorMessage)
//                401 -> Errors.Unauthorized(errorMessage)
//                404 -> Errors.NotFound(errorMessage)
//                else -> Errors.Unknown(errorMessage)
//            }
//        } else throw Exception("Successful response with null body")
//    }
//
//
//    // Modify CommonResult.Failure to inherit from Exception
//    sealed class CommonResult : Exception() {
//        object Success : CommonResult()
//        data class Failure(val error: Errors?) : CommonResult()
//    }
}
