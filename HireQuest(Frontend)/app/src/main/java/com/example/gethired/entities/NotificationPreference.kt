package com.example.gethired.entities

data class NotificationPreference(
	val id: Long,
	val userId: Long,
 	val notificationType: String,
 	val muted: Boolean,
)
