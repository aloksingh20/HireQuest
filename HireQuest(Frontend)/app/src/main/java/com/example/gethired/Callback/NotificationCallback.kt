package com.example.gethired.Callback

import com.example.gethired.entities.Notification

interface NotificationCallback {
    fun onNotificationResponse(notification: Notification)
    fun onNotificationError()
}