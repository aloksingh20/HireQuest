package com.example.gethired.alarmManager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.gethired.R
import com.example.gethired.Room.AppDatabase
import com.example.gethired.Room.MeetingDto
import com.example.gethired.reminder.CancelNotificationReceiver
import kotlinx.coroutines.*

class AlarmReceiver : BroadcastReceiver() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private var notificationJob: Job? = null
    private var ringtone: Ringtone? = null
    private val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    private val channelId = "meeting_reminder_channel"



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        val meetingId = intent.getIntExtra("meetingId", -1)
        if (meetingId != -1) {
            coroutineScope.launch {
                val meetingDto = fetchMeetingById(meetingId,context)
                createMeetingNotification(context, meetingDto)
            }
        } else {
            // Handle missing meeting ID in intent
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SuspiciousIndentation")
    private suspend fun createMeetingNotification(context: Context, meetingDto: MeetingDto) {
        // Create notification channel (if needed)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(
            context,
            "meeting_reminder_channel",
            "Meeting Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notification = NotificationCompat.Builder(context, "meeting_reminder_channel")
            .setContentTitle("Meeting Reminder")
            .setContentText("You have a meeting scheduled at ${meetingDto.time}")
            .setSmallIcon(R.drawable.icon_notification)
            .setOngoing(true) // Make it non-removable
            .addAction(android.R.drawable.ic_delete, "Cancel", getCancelPendingIntent(context)) // Pass context here
            .build()


            notificationManager.notify(meetingDto.meetingId, notification)

        // Start a coroutine to play ringtone and handle timeout
        notificationJob = CoroutineScope(Dispatchers.IO).launch {
            ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtone?.play()
            withContext(Dispatchers.IO) { // Ensure delay respects cancellation
                delay(30000) // 30-second timeout

                stopRingtoneAndUpdateNotification(meetingDto,meetingDto.meetingId,context)
            }
        }

    }

    private fun stopRingtoneAndUpdateNotification(meetingDto: MeetingDto,notificationId: Int, context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationJob?.cancel() // Cancel coroutine
        ringtone?.stop()

        // Update notification to be removable
        val channel = notificationManager.getNotificationChannel(channelId)
        channel.setSound(null, null) // Remove sound from channel
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Meeting Reminder")
            .setContentText("You have a meeting scheduled at ${meetingDto.time}")
            .setSmallIcon(R.drawable.icon_notification)
            .setOngoing(false) // Make it removable
            .build()
        notificationManager.notify(notificationId, notification)
    }


    private fun fetchMeetingById(meetingId: Int, context: Context): MeetingDto {
        return AppDatabase.getDatabase(context).meetingDao().getMeeting(meetingId)
    }

    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        importance: Int
    ) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        ).apply {
            setSound(null, null) // Initially set no sound for the channel
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getCancelPendingIntent(context: Context): PendingIntent {
        // Create pending intent for cancel action (replace with your actual action)
        return PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, CancelNotificationReceiver::class.java).apply {
                                                                          putExtra("notificationId",1)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun getRingtoneUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }
}
