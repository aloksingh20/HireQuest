package com.example.gethired.api

import com.example.gethired.Token.AuthInterceptor
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.retrofitInterface.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val BASE_URL = "http://192.168.1.5:8080/api/hireQuest/"
//private const val BASE_URL = "http://127.0.0.1:8080/"

class RetrofitClient(private val tokenManager: TokenManager? = null) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        tokenManager?.let { addInterceptor(AuthInterceptor(it)) }
    }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
