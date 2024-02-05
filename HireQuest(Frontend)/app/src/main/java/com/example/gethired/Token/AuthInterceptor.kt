package com.example.gethired.Token

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val jwtToken = tokenManager.getToken()

        if (jwtToken != null && tokenManager.isTokenValid()) {
            val newRequest: Request = originalRequest.newBuilder()
                .header("Authorization", "Bearer $jwtToken")
                .build()
            return chain.proceed(newRequest)
        }

        // No valid token, proceed with original request
        return chain.proceed(originalRequest)
    }
}
