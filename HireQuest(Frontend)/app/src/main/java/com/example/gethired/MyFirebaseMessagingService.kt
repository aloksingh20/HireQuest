package com.example.gethired

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.UserDto
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Log the message data for debugging (optional)
        Log.d("FCM", "Message received: ${remoteMessage.data}")

        // Extract the title and body from the notification payload
        val title = remoteMessage.notification?.title ?: "Default Title"
        val body = remoteMessage.notification?.body ?: "Default Body"

        // Handle displaying the notification to the user
        // You can use the NotificationManager to show the notification on the device's notification tray
        // For demonstration, let's log the notification here
        Log.d("FCM", "Notification Title: $title")
        Log.d("FCM", "Notification Body: $body")
        showNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

//        val userDto = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
//        if(userDto!=null){
//            updateFcmToken(userDto,token)
//        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(title: String, message: String) {
        // Create a notification channel (required for Android 8.0 and above).
        val channelId = "default_channel"
        val channelName = "Default Channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Default Channel for GetHired Notifications"
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(channel)

        // Create an intent to open the app when the notification is clicked.
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.icon_chat_active)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification.
        notificationManager.notify(0, notificationBuilder.build())
    }


    private fun updateFcmToken(user:UserDto,fcmToken:String) {
        val retrofitAPI: ApiService = RetrofitClient(TokenManager(applicationContext)).getApiService()

        retrofitAPI.updateFcmToken(user.username,fcmToken).enqueue(object :Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d("fem-token-update",t.message.toString())
            }

        })


    }

}