package com.example.gethired.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging

class FcmToken {


    fun getFcmToken(): MutableLiveData<String> {
        val fcmToken = MutableLiveData<String> ()
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                // Token retrieval succeeded
                fcmToken.value=token
                // Now you can proceed with your registration logic here
            }
            .addOnFailureListener { exception ->
                // Token retrieval failed
                Log.e("FCM", "Fetching FCM registration token failed", exception)
                // Handle the error if token retrieval fails
            }

        return fcmToken
    }
}