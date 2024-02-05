package com.example.gethired.Room

import androidx.room.*

@Dao
interface MeetingDao {

    @Query("SELECT * FROM meeting_table WHERE user = :user OR hr= :user AND ( date > :date OR (date = :date AND time >= :time)) ORDER BY date ASC, time ASC")
    suspend fun getUpcomingMeetings(user: String, time: String, date: String): List<MeetingDto>

    @Query("SELECT * FROM meeting_table WHERE user = :user OR hr= :user AND ( date < :date OR (date = :date AND time < :time)) ORDER BY date ASC, time ASC")
    suspend fun getPastMeetings(user: String, time: String, date: String): List<MeetingDto>

    @Insert
    suspend fun insertNewMeetings(meetingDtoList: List<MeetingDto>)

    @Insert
    suspend fun insertNewMeeting(meetingDto: MeetingDto): Long // Returns inserted meetingId

    @Update
    @Transaction
    suspend fun updateMeeting(meetingDto: MeetingDto)

    @Delete
    suspend fun deleteMeeting(meetingDto: MeetingDto)

    @Query("SELECT * FROM meeting_table WHERE meetingId = :meetingId")
    fun getMeeting(meetingId: Int): MeetingDto
}
