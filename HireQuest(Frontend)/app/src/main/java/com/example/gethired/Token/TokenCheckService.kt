package com.example.gethired.Token

import android.app.Application
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.auth0.android.jwt.JWT
import com.example.gethired.Callback.LoginCallback
import com.example.gethired.CommonFunction
import com.example.gethired.LoginActivity
import com.example.gethired.Repository.RegisterLoginRepository
import com.example.gethired.entities.LoginDto
import io.jsonwebtoken.ExpiredJwtException
import okhttp3.Request

class TokenCheckService : JobService() {
    private val registerLoginRepository = RegisterLoginRepository()

    val tokenManager = TokenManager(applicationContext)

    private var sharedPref = applicationContext.getSharedPreferences("login_preference", Context.MODE_PRIVATE)

    override fun onStartJob(params: JobParameters?): Boolean {
        // Check token expiry here
        Log.d("job-service-start", "Job started")
        checkTokenExpiryAndLogout()
        return true // Return false as the task is not reschedulable
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("job-service-stop", "Job started")
        return true // Return false to indicate that the job should not be rescheduled
    }

    private fun checkTokenExpiryAndLogout() {
        // Similar to the checkTokenExpiryAndLogout method in MainActivity
        // Perform token expiry check and logout if needed
        val isAboutToExpire=isTokenAboutToExpire(tokenManager.getToken().toString())
        if(isAboutToExpire){
            tokenManager.clearToken()
            try {
                requestNewToken { newToken ->
                    if (newToken != null) {
                        tokenManager.clearToken()
                        tokenManager.saveToken(newToken)
                    }else{
                        CommonFunction.SharedPrefsUtil.clearUserResponseFromSharedPreferences()

                        // Redirect to the login screen
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
            catch (e: java.lang.Exception){
                e.printStackTrace()
            }

        }


    }
    private fun isTokenAboutToExpire(token: String): Boolean {
        val jwt = JWT(token)
        val expirationTime = jwt.expiresAt?.time ?: return false

        val currentTime = System.currentTimeMillis()
        val tenMinutesInMillis = 15 * 60 * 1000 // 10 minutes in milliseconds

        return expirationTime - currentTime <= tenMinutesInMillis
    }
    private fun requestNewToken(callback: (String?) -> Unit) {
        val pass=sharedPref.getString("password","")
        val user = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
        val loginDto = LoginDto(user!!.username, pass!!, "", "")

        registerLoginRepository.loginUser(loginDto, object : LoginCallback {
            override fun onResponseLogin(loginResponse: LoginResponse) {
                callback(loginResponse.accessToken)
            }

            override fun onErrorLogin() {
                callback(null)
            }
        })
    }


}
