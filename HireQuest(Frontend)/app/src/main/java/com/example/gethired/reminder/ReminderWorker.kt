package com.example.gethired.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters // Add this import
import com.example.gethired.R
import com.example.gethired.Room.AppDatabase
import com.example.gethired.Room.MeetingDto
import kotlinx.coroutines.*
import javax.inject.Inject

class ReminderWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val roomDatabase: AppDatabase = AppDatabase.getDatabase(context)
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val channelId = "meeting_reminder_channel"
    private val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    private var ringtone: Ringtone? = null
    private var notificationJob: Job? = null

    private var meetingDto:MeetingDto?=null

    override fun doWork(): Result {
        val meetingId = inputData.getInt("meetingId", -1)

        if (meetingId > 0) {
            val meeting = fetchMeetingById(meetingId)
            meetingDto=meeting
            createMeetingNotification()
        }

        return Result.success()
    }

    private fun createMeetingNotification() {
        // Create notification channel (if needed)
        createNotificationChannel(
            channelId,
            "Meeting Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Meeting Reminder")
            .setContentText("You have a meeting scheduled at ${meetingDto!!.time}")
            .setSmallIcon(R.drawable.icon_notification)
            .setOngoing(true) // Make it non-removable
            .addAction(android.R.drawable.ic_delete, "Cancel", cancelPendingIntent)
            .build()

        val notificationId = meetingDto!!.meetingId


        notificationManager.notify(notificationId, notification)

        // Start a coroutine to play ringtone and handle timeout
        notificationJob = CoroutineScope(Dispatchers.IO).launch {
            ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
            ringtone?.play()
            withContext(Dispatchers.IO) { // Ensure delay respects cancellation
                delay(30000) // 30-second timeout
                stopRingtoneAndUpdateNotification(notificationId)
            }
        }
    }

    private fun stopRingtoneAndUpdateNotification(notificationId: Int) {
        notificationJob?.cancel() // Cancel coroutine
        ringtone?.stop()

        // Update notification to be removable
        val channel = notificationManager.getNotificationChannel(channelId)
        channel.setSound(null, null) // Remove sound from channel
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Meeting Reminder")
            .setContentText("You have a meeting scheduled at ${meetingDto!!.time}")
            .setSmallIcon(R.drawable.icon_notification)
            .setOngoing(false) // Make it removable
            .build()
        notificationManager.notify(notificationId, notification)
    }

    private fun fetchMeetingById(meetingId: Int): MeetingDto {
        return roomDatabase.meetingDao().getMeeting(meetingId)
    }

    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        ).apply {
            setSound(null, null) // Initially set no sound for the channel
        }
        notificationManager.createNotificationChannel(channel)
    }


    private val cancelPendingIntent: PendingIntent by lazy {
        // Create pending intent for cancel action (replace with your actual action)
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, CancelNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

//    companion object{
//        fun createWorker(context: Context):ReminderWorker{
//            return ReminderWorker(context,WorkerParameters)
//        }
//    }


}