package com.example.gethired.websocket

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.gethired.ChattingActivity
import com.example.gethired.MainActivity
import com.example.gethired.R
import com.example.gethired.Room.MessageRoomDto
import com.example.gethired.ViewModel.ChatViewModel
import com.example.gethired.entities.Chat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class WebSocketService : Service() {

    private lateinit var webSocket: WebSocket
    private var token:String=""
    private var userId:Long=0
    private lateinit var chatViewModel: ChatViewModel

    fun setChatViewModel(viewModel: ChatViewModel) {
        chatViewModel = viewModel
    }
    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }
    fun getWebSocketInstance(): WebSocket {
        return webSocket
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        token = intent?.getStringExtra("TOKEN") ?: ""
        userId= intent?.getLongExtra("LOGGED_IN_USER",0)!!
        createWebSocket()
        return START_STICKY
    }

    private fun createWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("ws://192.168.1.5:8080/ws/$token").build()
        webSocket = client.newWebSocket(request, MyWebSocketListener())
    }

    override fun onDestroy() {
        super.onDestroy()

        // Clean up resources when the service is stopped
        webSocket.close(1000, "Service stopped")
    }

    inner class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {

            // This is called when the connection is established
            Log.d("WebSocket", "Connection opened")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // Handle incoming messages
            Log.d("WebSocket", "Received message: $text")
            val chatData = Gson().fromJson(text, Chat::class.java)
            val messageData=Gson().fromJson(text,MessageRoomDto::class.java)
            if(chatData.receiverId==userId&&!ChattingActivity.isVisible){
                // Broadcast the received message
                sendNotification(chatData)

            }
            else if(chatData.receiverId==userId&&ChattingActivity.isVisible){
                val intent = Intent("NEW_MESSAGE_RECEIVED")
                intent.putExtra("message", text)
                sendBroadcast(intent)

            }

            // Show a notification
//            showNotification(this@WebSocketService ,text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // Handle incoming messages in bytes if needed
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            // This is called when the connection is about to be closed
            Log.d("WebSocket", "Connection closing: $code, $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // This is called in case of an error
            Log.d("WebSocket", "Connection failure: ${t.message}")
        }
    }
    @SuppressLint("ServiceCast")
    private fun sendNotification(message: Chat) {
        // Create an intent that opens the ChattingActivity when the notification is tapped
        val intent = Intent(this, ChattingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("user_id",message.senderId)
        intent.putExtra("receiver",message.receiverId)
        intent.putExtra("roomId",message.roomId)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE) // Use FLAG_IMMUTABLE

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Build the notification
        val CHANNEL_ID="1234"
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.hq_logo)
            .setContentTitle("New message") // Customize the title as needed
            .setContentText(message.content) // Customize the notification content
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android Oreo and higher
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Chat Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // Notify
        notificationManager.notify(0, notificationBuilder.build())
    }

    inner class LocalBinder : Binder() {
        fun getService(): WebSocketService {
            // Return this instance of WebSocketService so clients can call public methods
            return this@WebSocketService
        }
    }

    private fun Chat.toMessageRoomDto(): MessageRoomDto {
        val chatTimeStamp = LocalDateTime.parse(this.timestamp.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))

        return MessageRoomDto(
            // Map the properties from Chat to MessageRoomDto
            id = 0,
            roomId = this.roomId,
            senderId = this.senderId,
            receiverId = this.receiverId,
            content = this.content,
            timeStamp = chatTimeStamp,
            seen = this.seen
        )
    }

}
