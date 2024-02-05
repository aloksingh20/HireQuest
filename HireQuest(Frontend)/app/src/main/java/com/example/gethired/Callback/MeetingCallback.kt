package com.example.gethired.Callback

import com.example.gethired.entities.Meeting

interface MeetingCallback {
    fun onMeetingResponse(meeting: Meeting)
    fun onMeetingError(message:String)
}