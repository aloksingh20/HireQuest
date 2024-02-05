package com.example.gethired.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.rxjava3.annotations.NonNull
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime


@Entity(tableName = "meeting_table")
data class MeetingDto (
    @PrimaryKey(autoGenerate = false)
    val meetingId:Int,
    val user:String,
    val hr:String,
    val date:String,
    val time:String,
    val link:String,
    val isAttended:Boolean,
    val isReminderOn:Boolean
)
