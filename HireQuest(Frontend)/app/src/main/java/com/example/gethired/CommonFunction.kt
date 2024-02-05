package com.example.gethired

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.*
import com.example.gethired.Room.MeetingDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Meeting
import com.example.gethired.entities.Project
import com.example.gethired.entities.UserDto
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CommonFunction(val context: Context) {
    fun getMonth(month: String): String {
        if (month.isEmpty()) {
            return ""
        }
        val arr = month.split("-")
        if (arr.size < 2) {
            return ""
        }
        when (arr[1].toInt()) {
            1 -> {
                return "Jan ${arr[2]}"
            }
            2 -> {
                return "Feb ${arr[2]}"
            }
            3 -> {
                return "Mar ${arr[2]}"
            }
            4 -> {
                return "Apr ${arr[2]}"
            }
            5 -> {
                return "May ${arr[2]}"
            }
            6 -> {
                return "Jun ${arr[2]}"
            }
            7 -> {
                return "Jul ${arr[2]}"
            }
            8 -> {
                return "Aug ${arr[2]}"
            }
            9 -> {
                return "Sep ${arr[2]}"
            }
            10 -> {
                return "Oct ${arr[2]}"
            }
            11 -> {
                return "Nov ${arr[2]}"
            }
            12 -> {
                return "Dec ${arr[2]}"
            }
        }
        return ""
    }

    fun toMeetingDto(meeting:Meeting): MeetingDto {
        return MeetingDto(
            meetingId = meeting.id,
            user = meeting.user,
            hr = meeting.hr,
            date = meeting.date,
            time = meeting.time,
            link = meeting.link,
            isAttended = meeting.isAttended,
            isReminderOn = true
        )
    }



    fun pickDate(from: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            from.context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Here, you can handle the selected date as you wish.
                val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                from.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    object SharedPrefsUtil {
        private lateinit var sharedPref: SharedPreferences

        // Initialize Shared Preferences
        fun init(context: Context) {
            sharedPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        }

        fun fetchUserResponseFromSharedPreferences(): UserDto? {
            val userJson = sharedPref.getString("user_details", null)
                val gson = Gson()
            if(userJson==null)return null;
            return gson.fromJson(userJson, UserDto::class.java)
        }

        fun clearUserResponseFromSharedPreferences() {
            val editor = sharedPref.edit()
            editor.remove("user_details")
            editor.remove("username")
            editor.remove("password")
            editor.apply()
        }

        fun updateUserResponseFromSharedPreferences(user:UserDto){
            val gson = Gson()
            val userJson = gson.toJson(user)
            val editor = sharedPref.edit()
            editor.remove("user_details")
            editor.remove("username")
            editor.putString("user_details", userJson)
            editor.putString("username",user.username)
            editor.apply()
        }

    }

}