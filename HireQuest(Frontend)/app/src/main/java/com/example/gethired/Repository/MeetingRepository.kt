package com.example.gethired.Repository

import android.content.Context
import com.example.gethired.Room.AppDatabase
import com.example.gethired.Room.MeetingDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Meeting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MeetingRepository(tokenManager: TokenManager, context: Context) {
    private val retrofitAPI: ApiService = RetrofitClient(tokenManager).getApiService()
    private val roomDatabase: AppDatabase = AppDatabase.getDatabase(context)
    private suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> Response<T>): ApiResult<T> {
        return try {
            withContext(dispatcher) {
                val response = apiCall()
                if (response.isSuccessful) {
                    ApiResult.Success(response.body())
                } else {
                    ApiResult.Error(HttpException(response))
                }
            }
        } catch (e: IOException) {
            ApiResult.Error(e)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun createMeeting(meeting: Meeting): ApiResult<Meeting?> {
        return safeApiCall { retrofitAPI.createMeeting(meeting) }
    }

    suspend fun updateMeeting(user: String, hr: String, meeting: Meeting): ApiResult<Meeting? >{
        return safeApiCall { retrofitAPI.updateMeeting(user, hr, meeting) }
    }

    suspend fun getAllPastMeetings(user: String):ApiResult<List<Meeting>? >{
        return safeApiCall { retrofitAPI.getAllPastMeeting(user) }
    }

    suspend fun getAllUpcomingMeetings(user: String): ApiResult<List<Meeting>?> {
        return safeApiCall { retrofitAPI.getAllMeeting(user) }
    }
//    Room db

    suspend fun getUpcomingMeetingsFromRoom(user: String, time: String, date: String): List<MeetingDto> {
        return withContext(Dispatchers.IO) {
            roomDatabase.meetingDao().getUpcomingMeetings(user, time, date)
        }
    }

    suspend fun getPastMeetingsFromRoom(user: String, time: String, date: String): List<MeetingDto> {
        return withContext(Dispatchers.IO) {
            roomDatabase.meetingDao().getPastMeetings(user, time, date)
        }
    }

    suspend fun insertNewMeetingsToRoom(meetingDto: List<MeetingDto>) {
        withContext(Dispatchers.IO) {
            roomDatabase.meetingDao().insertNewMeetings(meetingDto)
        }
    }

    suspend fun insertNewMeetingToRoom(meetingDto: MeetingDto): Long {
        return withContext(Dispatchers.IO) {
            roomDatabase.meetingDao().insertNewMeeting(meetingDto)
        }
    }

    suspend fun updateMeetingInRoom(meetingDto: MeetingDto) {
        withContext(Dispatchers.IO) {
            roomDatabase.meetingDao().updateMeeting(meetingDto)
        }
    }

    suspend fun deleteMeetingFromRoom(meetingDto: MeetingDto) {
        withContext(Dispatchers.IO) {
            roomDatabase.meetingDao().deleteMeeting(meetingDto)
        }
    }
    suspend fun getMeetingFromRoom(meetingId:Int):MeetingDto{
        return withContext(Dispatchers.IO){
            roomDatabase.meetingDao().getMeeting(meetingId )
        }
    }
}
