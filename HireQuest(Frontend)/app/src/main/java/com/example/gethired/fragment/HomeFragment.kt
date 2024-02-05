package com.example.gethired.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.CommonFunction
import com.example.gethired.NotificationActivity
import com.example.gethired.PREF_FILE_NAME
import com.example.gethired.R
import com.example.gethired.Room.AppDatabase
import com.example.gethired.Room.MeetingDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.MeetingViewModel
import com.example.gethired.ViewModel.UserProfileViewModel
import com.example.gethired.adapter.MeetingAdapter
import com.example.gethired.alarmManager.AlarmReceiver
import com.example.gethired.entities.Meeting
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.MeetingViewModelFactory
import com.example.gethired.factory.UserProfileViewModelFactory
import com.example.gethired.reminder.ReminderWorker
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var appBar: MaterialToolbar
    private lateinit var title:TextView

    private lateinit var upcomingMeetingBtn:TextView
    private lateinit var pastMeetingBtn:TextView
    private lateinit var meetingRecyclerView: RecyclerView
    private lateinit var meetingLoading: LottieAnimationView
    private lateinit var noMeetingText:TextView

    private lateinit var notificationIcon:ImageView

    private var upcomingMeetingList:MutableList<Meeting> = mutableListOf()
    private var pastMeetingList:MutableList<Meeting> = mutableListOf()
    private lateinit var meetingAdapter: MeetingAdapter

//    private lateinit var candidateName: EditText

    private lateinit var tokenManager:TokenManager
    private lateinit var meetingViewModel:MeetingViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel

    private var user: UserDto? = null
    private  lateinit var sharedPref: SharedPreferences
    private lateinit var roomDatabase: AppDatabase

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        sharedPref=  requireContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        roomDatabase=AppDatabase.getDatabase(requireContext())

        appBarLayout = rootView.findViewById(R.id.appbar_container)
        appBar=rootView.findViewById(R.id.appBar)
        title=rootView.findViewById(R.id.title)

        upcomingMeetingBtn=rootView.findViewById(R.id.upcomingTab)
        pastMeetingBtn=rootView.findViewById(R.id.pastTab)
        notificationIcon=rootView.findViewById(R.id.notification_icon)

        meetingRecyclerView=rootView.findViewById(R.id.meetingRecyclerView)
        meetingLoading=rootView.findViewById(R.id.meetingLoadingBar)
        noMeetingText=rootView.findViewById(R.id.noMeeting)

        tokenManager= TokenManager(requireContext())
        meetingViewModel=
            ViewModelProvider(this, MeetingViewModelFactory(tokenManager,requireContext()))[MeetingViewModel::class.java]
        userProfileViewModel=
            ViewModelProvider(this, UserProfileViewModelFactory(tokenManager, requireContext()))[UserProfileViewModel::class.java]

        user = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()

        val nestedScrollView = rootView.findViewById<NestedScrollView>(R.id.nestedScrollView)
        var isHidden = false
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY && !isHidden) {
                // Scrolling down, animate AppBarLayout back to its position
//                appBarLayout.visibility = View.GONE
                appBar.visibility=View.GONE
                appBarLayout.animate().translationY(0f).setDuration(300).start()
//                appBar.animate().translationY(0f).setDuration(300).start()
                isHidden = true
            }
            else if (scrollY < oldScrollY&& isHidden) {
                // Scrolling up, hide appbar
                appBarLayout.animate().translationY(-appBarLayout.height.toFloat()).setDuration(300).start()
//                appBar.animate().translationY(-appBar.height.toFloat()).setDuration(300).start()
                isHidden = false
//                appBarLayout.visibility =View.VISIBLE
                appBar.visibility=View.VISIBLE
            }
        }

        val username=user!!.name.split(" ")[0]
        val time=LocalTime.now()
        if(time.hour in 4..11){
            title.text="Good morning, ${username}"
        }else if(time.hour in 12 downTo 5){
            title.text="Good afternoon, ${username}"
        }else{
            title.text="Good evening, ${username}"
        }

        notificationIcon.setOnClickListener {
            val intent= Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(intent)
        }

        meetingAdapter=MeetingAdapter(upcomingMeetingList,requireContext(),sharedPref,user!!.isRecuriter)
        meetingRecyclerView.adapter=meetingAdapter
        meetingRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        meetingLoading.visibility=View.VISIBLE
        meetingRecyclerView.visibility=View.GONE
        noMeetingText.visibility=View.GONE
//
//        meetingViewModel.getAllMeeting(user!!.username)
//        meetingViewModel.getAllPastMeeting(user!!.username)

        upcomingMeeting()

        meetingViewModel.allUpComingMeetings.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                upcomingMeetingList.clear()
                upcomingMeetingList.addAll(it)
                meetingAdapter.update(upcomingMeetingList)
            }
        }

        meetingViewModel.allPastMeetings.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                pastMeetingList.clear()
                pastMeetingList.addAll(it)
            }
        }

        upcomingMeetingBtn.setOnClickListener {

            pastMeetingBtn.background = resources.getDrawable(R.drawable.home_meeting_fragment_background_inactive)
            upcomingMeetingBtn.background = resources.getDrawable(R.drawable.home_meeting_fragment_background_active)
            pastMeetingBtn.setTextColor(resources.getColor(R.color.black))
            upcomingMeetingBtn.setTextColor(resources.getColor(R.color.white))

            upcomingMeeting()

        }
        pastMeetingBtn.setOnClickListener {
            pastMeetingBtn.background = resources.getDrawable(R.drawable.home_meeting_fragment_background_active)
            upcomingMeetingBtn.background = resources.getDrawable(R.drawable.home_meeting_fragment_background_inactive)
            pastMeetingBtn.setTextColor(resources.getColor(R.color.white))
            upcomingMeetingBtn.setTextColor(resources.getColor(R.color.black))
            pastMeetings()

        }

        meetingViewModel.error.observe(viewLifecycleOwner){
            if(it.isNullOrEmpty()){

            }else{
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        meetingViewModel.loading.observe(viewLifecycleOwner){
            if(it){
                meetingLoading.visibility=View.VISIBLE
                meetingRecyclerView.visibility=View.GONE
            }else{
                meetingRecyclerView.visibility=View.VISIBLE
                meetingLoading.visibility=View.GONE
            }
        }

        meetingAdapter.setOnScheduleMeetingClickListener(object :MeetingAdapter.OnScheduleMeetingClickListener{
            override fun onScheduleMeetingClick(view: View, position: Int) {
                val currMeeting = upcomingMeetingList[position]
                val isScheduled = sharedPref.getBoolean("${currMeeting.id}-${user!!.username}",false)
                if(isScheduled){
                    removeMeetingFromSchedule(currMeeting)
                }else{
                    scheduleMeeting(currMeeting)
                }
            }
        })

        return rootView
    }

    private fun pastMeetings(){
        if(pastMeetingList.isEmpty()){
            noMeetingText.visibility=View.VISIBLE
            meetingAdapter.update(pastMeetingList)

        }else{
            noMeetingText.visibility=View.GONE
            meetingAdapter.update(pastMeetingList)

        }
    }

    private fun upcomingMeeting(){
        if(upcomingMeetingList.isEmpty()){
            noMeetingText.visibility=View.VISIBLE
            meetingAdapter.update(upcomingMeetingList)

        }else{
            noMeetingText.visibility=View.GONE
            meetingAdapter.update(upcomingMeetingList)

        }
    }

    private fun scheduleMeeting(meeting: Meeting) {
        val alarmManager = requireContext().getSystemService( Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("meetingId", meeting.id) // Pass meeting data
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), meeting.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val parsedTime = LocalTime.parse(meeting.time, timeFormatter)
        val parsedDate = LocalDate.parse(meeting.date, dateFormatter)

        val timeFormatter2 = DateTimeFormatter.ofPattern("hh:mm a")
        val thirtyMinutesBefore = parsedTime.minusMinutes(30)

        val formattedTime = thirtyMinutesBefore.format(timeFormatter2)

        val combinedDateTime = LocalDateTime.of(parsedDate, parsedTime)

        val meetingTimeInMilli = combinedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val calendar = Calendar.getInstance().apply {
            timeInMillis = meetingTimeInMilli - 1800000            // Trigger after 30 min
        }

        // Use exact alarms for Android 12+:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        val editor= sharedPref.edit()
        editor.putBoolean("${meeting.id}-${user!!.username}", true)
        editor.apply()

        meetingViewModel.insertMeetingToRoom(CommonFunction(requireContext()).toMeetingDto(meeting))
        CustomErrorSnackBar().showSnackbar(requireContext(),requireView(),"Interview reminder is scheduled for ${combinedDateTime.dayOfMonth} ${combinedDateTime.month.name},\n at $formattedTime")

    }
    private fun removeMeetingFromSchedule(currMeeting: Meeting){

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            currMeeting.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)
        meetingViewModel.deleteMeetingFromRoom(CommonFunction(requireContext()).toMeetingDto(currMeeting))

        // Remove the scheduled alarm from SharedPreferences or wherever it was stored
        val editor = sharedPref.edit()
        editor.remove("${currMeeting.id}-${user!!.username}")
        editor.apply()
    }



}


//
//val data = Data.Builder()
//    .putInt("meetingId", currMeeting.id)
//    .build()


//val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//val parsedTime = LocalTime.parse(currMeeting.time)
//val parsedDate = LocalDate.parse(currMeeting.date, dateFormatter)
//
//val reminderTime = LocalDateTime.now().plusHours(2) // Example: 2 hours from now
//val combinedDateTime = LocalDateTime.of(parsedDate, parsedTime)
//
//val initialDelay = combinedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()
//
//val constraints = Constraints.Builder()
//    .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // Requires internet connection
//    .setRequiresBatteryNotLow(false)
//    .setRequiresCharging(false)
//    .setRequiresDeviceIdle(false)
//    .build()
//
//val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
//    .setInputData(data)
//    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
//    .setConstraints(constraints)
//    .build()
//
//WorkManager.getInstance(requireContext()).enqueueUniqueWork("${currMeeting.id}-${currMeeting.user}",ExistingWorkPolicy.REPLACE ,reminderRequest)
