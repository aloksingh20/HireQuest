package com.example.gethired.Repository

import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Pdf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PdfRepository(private val tokenManager: TokenManager) {

    private val retrofitAPI: ApiService =
        RetrofitClient(tokenManager).getApiService()

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

    suspend fun addPdf(userProfileId: Long, file: MultipartBody.Part):ApiResult<Pdf> {
        return safeApiCall { retrofitAPI.addPdf(userProfileId,file).execute() }
    }

    suspend fun deletePdf(pdfId: Long):ApiResult<Boolean> {
        return safeApiCall { retrofitAPI.deletePdf(pdfId).execute() }
    }

    suspend fun getAllPdf(userProfileId: Long): ApiResult<List<Pdf>> {
       return safeApiCall { retrofitAPI.getAllPdf(userProfileId).execute() }
    }

    suspend fun downloadPdf(pdfId: Long): ApiResult<ResponseBody> {
        return safeApiCall { retrofitAPI.downloadPdf(pdfId).execute() }
    }

}