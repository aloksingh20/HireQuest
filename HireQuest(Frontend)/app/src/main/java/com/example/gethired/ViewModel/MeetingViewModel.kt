package com.example.gethired.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gethired.Callback.MeetingCallback
import com.example.gethired.Repository.MeetingRepository
import com.example.gethired.Room.MeetingDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Meeting
import com.example.gethired.exception.HireQuestException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MeetingViewModel(tokenManager: TokenManager, context: Context) : ViewModel() {
    private val meetingRepository = MeetingRepository(tokenManager,context)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdMeeting = MutableLiveData<Meeting?>(null)
    val createdMeeting: LiveData<Meeting?> = _createdMeeting

    private val _updatedMeeting = MutableLiveData<Meeting?>(null)
    val updatedMeeting: LiveData<Meeting?> = _updatedMeeting

    private val _allUpComingMeetings = MutableLiveData<List<Meeting>?>(null)
    val allUpComingMeetings: LiveData<List<Meeting>?> = _allUpComingMeetings

    private val _allPastMeetings = MutableLiveData<List<Meeting>?>(null)
    val allPastMeetings: LiveData<List<Meeting>?> = _allPastMeetings

    private val _fetchedMeetingDto = MutableLiveData<MeetingDto?>(null)
    val fetchedMeetingDto: LiveData<MeetingDto?> = _fetchedMeetingDto

    private suspend fun <T> executeApiCall(
        call: suspend () -> ApiResult<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
        _loading.value = true
        _error.value = null

        try {
            when (val result = call()) {
                is ApiResult.Success -> onSuccess(result.data!!)
                is ApiResult.Error -> onError(result.exception.localizedMessage ?: "Unknown error")
            }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Unknown error")
        } finally {
            _loading.value = false
        }
    }

    suspend fun createMeeting(meeting: Meeting) {
        executeApiCall(
            call = { meetingRepository.createMeeting(meeting) },
            onSuccess = { _createdMeeting.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun updateMeeting(user: String, hr: String, meeting: Meeting) {
        executeApiCall(
            call = { meetingRepository.updateMeeting(user, hr, meeting) },
            onSuccess = { _updatedMeeting.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun getAllUpcomingMeetings(user: String) {
        executeApiCall(
            call = { meetingRepository.getAllUpcomingMeetings(user) },
            onSuccess = { _allUpComingMeetings.value = it },
            onError = { _error.value = it }
        )
    }

    suspend fun getAllPastMeetings(user: String) {
        executeApiCall(
            call = { meetingRepository.getAllPastMeetings(user) },
            onSuccess = { _allPastMeetings.value = it },
            onError = { _error.value = it }
        )
    }


    fun getAllUpComingMeetingFromRoom(user: String,time:String,date:String){
        _loading.value=true
        viewModelScope.launch {
            val allUpcomingMeetingsFetched=meetingRepository.getUpcomingMeetingsFromRoom(user,time,date)
            _allUpComingMeetings.value=null
        }
    }

    fun insertMeetingToRoom(meetingDto: MeetingDto){
        viewModelScope.launch {
            val meetingInserted = meetingRepository.insertNewMeetingToRoom(meetingDto)
        }
    }

    fun getMeetingFromRoom(meetingId:Int){
        viewModelScope.launch {
            val meetingDto = meetingRepository.getMeetingFromRoom(meetingId)
            _fetchedMeetingDto.value=meetingDto

        }
    }
    fun deleteMeetingFromRoom(meetingDto: MeetingDto){
        viewModelScope.launch {
            meetingRepository.deleteMeetingFromRoom(meetingDto )
        }
    }

}