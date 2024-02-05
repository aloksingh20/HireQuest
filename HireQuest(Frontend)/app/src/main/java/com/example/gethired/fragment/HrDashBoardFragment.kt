package com.example.gethired.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.CommonFunction
import com.example.gethired.NotificationActivity
import com.example.gethired.PREF_FILE_NAME
import com.example.gethired.R
import com.example.gethired.Room.AppDatabase
import com.example.gethired.Room.MeetingDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.MeetingViewModel
import com.example.gethired.ViewModel.NotificationViewModel
import com.example.gethired.ViewModel.UserProfileViewModel
import com.example.gethired.adapter.DateAdapter
import com.example.gethired.adapter.MeetingAdapter
import com.example.gethired.adapter.SearchedMeetingCandidateAdapter
import com.example.gethired.adapter.TimeAdapter
import com.example.gethired.alarmManager.AlarmReceiver
import com.example.gethired.entities.Meeting
import com.example.gethired.entities.NotificationRequest
import com.example.gethired.entities.UserDto
import com.example.gethired.entities.UserProfile
import com.example.gethired.factory.MeetingViewModelFactory
import com.example.gethired.factory.NotificationViewModelFactory
import com.example.gethired.factory.UserProfileViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.example.gethired.utils.Lists
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs


class HrDashBoardFragment : Fragment() {
    private lateinit var title:TextView

    private lateinit var notificationIcon : ImageView
    private lateinit var scheduleMeeting : LinearLayout
    private var selectedTime=""
    private var selectedDate=""

    private lateinit var upcomingMeetings : TextView
    private lateinit var pastMeetings : TextView
    private lateinit var meetingRecyclerView : RecyclerView
    private lateinit var meetingLoading : LottieAnimationView
    private lateinit var noMeetingText :TextView

    private lateinit var candidateName : EditText


    private lateinit var date: TextInputEditText
    private lateinit var time: TextInputEditText

    private lateinit var tokenManager :TokenManager
    private lateinit var meetingViewModel :MeetingViewModel
    private lateinit var userProfileViewModel : UserProfileViewModel
    private  lateinit var notificationViewModel : NotificationViewModel

    private var upcomingMeetingList : MutableList<Meeting> = mutableListOf()
    private var pastMeetingList : MutableList<Meeting> = mutableListOf()
    private var meetingAdapterList : MutableList<Meeting> = mutableListOf()
    private lateinit var meetingAdapter : MeetingAdapter

    private var candidateProfileList: MutableList<UserProfile> = mutableListOf()
    private lateinit var searchedMeetingCandidateAdapter:SearchedMeetingCandidateAdapter

    private var user: UserDto? = null
    private  lateinit var sharedPref: SharedPreferences
    private lateinit var roomDatabase: AppDatabase

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_hr_dash_board, container, false)

//        createOverlayView()
        user = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
        sharedPref=  requireContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

        tokenManager = TokenManager(requireContext())
        meetingViewModel = ViewModelProvider(
            this,
            MeetingViewModelFactory(tokenManager,requireContext())
        )[MeetingViewModel::class.java]
        userProfileViewModel = ViewModelProvider(
            this,
            UserProfileViewModelFactory(tokenManager, requireContext())
        )[UserProfileViewModel::class.java]
        notificationViewModel = ViewModelProvider(this,NotificationViewModelFactory(tokenManager))[NotificationViewModel::class.java]

        title = rootView.findViewById(R.id.title)
        notificationIcon = rootView.findViewById(R.id.notification_icon)
        scheduleMeeting = rootView.findViewById(R.id.create_new_meeting_layout)

        upcomingMeetings = rootView.findViewById(R.id.upcomingMeetingTab)
        pastMeetings = rootView.findViewById(R.id.pastMeetingTab)
        meetingRecyclerView = rootView.findViewById(R.id.meetingRecyclerView)
        meetingLoading = rootView.findViewById(R.id.meetingLoadingBar)
        noMeetingText = rootView.findViewById(R.id.noMeeting)

        meetingAdapter = MeetingAdapter(upcomingMeetingList, requireContext(), sharedPref,user!!.isRecuriter)
        meetingRecyclerView.adapter = meetingAdapter
        meetingRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (user!!.name.isNotEmpty()) {
            title.text = "Hello, ${user!!.name.split(" ")[0]}"
        }
        notificationIcon.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        }

        meetingLoading.visibility = View.VISIBLE
        meetingRecyclerView.visibility = View.GONE
        noMeetingText.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            meetingViewModel.getAllUpcomingMeetings(user!!.username)
        }
        meetingViewModel.allUpComingMeetings.observe(viewLifecycleOwner){meetings->
            if(meetings.isNullOrEmpty()){
                upcomingMeetingList= mutableListOf()
            }else{
                upcomingMeetingList.clear()
                upcomingMeetingList.addAll(meetings)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            meetingViewModel.getAllPastMeetings(user!!.username)
        }
        meetingViewModel.allPastMeetings.observe(viewLifecycleOwner){meetings->
            if(meetings.isNullOrEmpty()){
                pastMeetingList= mutableListOf()
            }else{
                pastMeetingList.clear()
                pastMeetingList.addAll(meetings)
            }
        }

        upcomingMeeting()

        upcomingMeetings.setOnClickListener {
            pastMeetings.background =
                resources.getDrawable(R.drawable.home_meeting_fragment_background_inactive)
            upcomingMeetings.background =
                resources.getDrawable(R.drawable.home_meeting_fragment_background_active)
            pastMeetings.setTextColor(resources.getColor(R.color.black))
            upcomingMeetings.setTextColor(resources.getColor(R.color.white))

            upcomingMeeting()
        }
        pastMeetings.setOnClickListener {

            pastMeetings.background =
                resources.getDrawable(R.drawable.home_meeting_fragment_background_active)
            upcomingMeetings.background =
                resources.getDrawable(R.drawable.home_meeting_fragment_background_inactive)
            pastMeetings.setTextColor(resources.getColor(R.color.white))
            upcomingMeetings.setTextColor(resources.getColor(R.color.black))
            pastMeetings()

        }

        meetingViewModel.createdMeeting.observe(viewLifecycleOwner){

            if(it!=null){
                sendMeetingCreatedNotification(it)
                upcomingMeetingList.add(it)
                meetingAdapter.notifyDataSetChanged()
            }else{
                noMeetingText.visibility=View.VISIBLE
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

        scheduleMeeting.setOnClickListener {
            createNewMeeting()
        }

        return rootView
    }

    private fun createNewMeeting() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.schedule_meeting_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        var isSearched = false;
        var isCandidateValid = false;
        var selectedPosition = -1

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        candidateName =
            popupView.findViewById(R.id.schedule_meeting_popup_candidate_name_editText)
        val meetingLink: TextInputEditText =
            popupView.findViewById(R.id.schedule_meeting_popup_meeting_link_editText)
        val candidateSearchLoadingAnimation =
            popupView.findViewById<LottieAnimationView>(R.id.meeting_candidate_search_loading_animation)
        val candidateSearchNoResult: TextView =
            popupView.findViewById(R.id.meeting_candidate_search_noResult)
        date = popupView.findViewById(R.id.schedule_meeting_popup_meeting_date)
        time = popupView.findViewById(R.id.schedule_meeting_popup_meeting_time)
        val submitBtn: MaterialButton =
            popupView.findViewById(R.id.schedule_meeting_popup_save_button)
        val submitLoadingBar: LottieAnimationView =
            popupView.findViewById(R.id.submit_loadingBar)

        val bodyContainer: LinearLayout =
            popupView.findViewById(R.id.schedule_meeting_popup_body_container)
        val searchedCandidateRecyclerView =
            popupView.findViewById<RecyclerView>(R.id.schedule_meeting_popup_candidate_search_recyclerview)

        searchedMeetingCandidateAdapter =
            SearchedMeetingCandidateAdapter(candidateProfileList)
        searchedCandidateRecyclerView.adapter = searchedMeetingCandidateAdapter
        searchedCandidateRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        backButton.setOnClickListener {
            popupWindow.dismiss()
        }
        if (!isSearched) {
            // Modify the TextWatcher to update RecyclerView visibility
            candidateName.addTextChangedListener(object : TextWatcher {
                private val DELAY = 1000L // Delay in milliseconds
                private val handler = Handler(Looper.getMainLooper())
                private var lastQuery = ""

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    candidateSearchLoadingAnimation.visibility = View.VISIBLE
                    isSearched = false
                    lastQuery = s.toString().trim()

                    if (lastQuery == s.toString().trim()) {
                        if (s.toString().isEmpty() || candidateName.text.isEmpty()) {
                            searchedCandidateRecyclerView.visibility = View.GONE
                            bodyContainer.visibility = View.VISIBLE
                            candidateSearchNoResult.visibility = View.GONE
                        } else {
                            if (selectedPosition == -1) {
                                // No item selected, show the RecyclerView
                                searchedCandidateRecyclerView.visibility = View.VISIBLE
                                bodyContainer.visibility = View.GONE
                                candidateSearchNoResult.visibility = View.GONE

                                userProfileViewModel.getAllCandidateProfile(
                                    s.toString().trim(),"",
                                    0,
                                    100,
                                    "id",
                                    "ASC"
                                )
                                    .observe(requireActivity()) {
                                        candidateProfileList.clear()
                                        candidateSearchLoadingAnimation.visibility = View.GONE
                                        if (it.isEmpty()) {
                                            candidateSearchNoResult.visibility = View.VISIBLE
                                            searchedCandidateRecyclerView.visibility = View.GONE
                                            isCandidateValid = false
                                        } else {
                                            candidateSearchNoResult.visibility = View.GONE
                                            searchedCandidateRecyclerView.visibility = View.VISIBLE
                                            bodyContainer.visibility = View.GONE
                                        }
                                        candidateProfileList.clear()
                                        candidateProfileList.addAll(it)
                                        searchedMeetingCandidateAdapter.notifyDataSetChanged()
                                    }
                            } else {
                                // An item is selected, hide the RecyclerView
                                searchedCandidateRecyclerView.visibility = View.GONE
                                bodyContainer.visibility = View.VISIBLE
                                candidateSearchNoResult.visibility = View.GONE
                            }
                        }
                    } else {
                        candidateSearchLoadingAnimation.visibility = View.GONE
                        searchedCandidateRecyclerView.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
        if(candidateName.text.isEmpty()){
            candidateProfileList.clear()
            searchedMeetingCandidateAdapter.notifyDataSetChanged()
            searchedCandidateRecyclerView.visibility=View.GONE
            bodyContainer.visibility = View.VISIBLE
            candidateSearchNoResult.visibility=View.GONE
        }

        time.setOnClickListener {

            timePickerFun()

        }

        date.setOnClickListener {
            datePickerFun()
        }

        searchedMeetingCandidateAdapter.setOnItemClickListener(object :
            SearchedMeetingCandidateAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                selectedPosition = position

                // Set the candidateName EditText with the selected profile's username
                candidateName.setText(candidateProfileList[position].username)

                // Hide the RecyclerView
                searchedCandidateRecyclerView.visibility = View.GONE
                candidateSearchNoResult.visibility = View.GONE
                bodyContainer.visibility = View.VISIBLE

                // Clear focus from the candidateName EditText
                candidateName.clearFocus()

                // Update the flag
                isSearched = true
                isCandidateValid = true
            }
        })


        submitBtn.setOnClickListener {
            if (isCandidateValid) {
                submitLoadingBar.visibility = View.VISIBLE
                submitBtn.visibility = View.GONE
                if (!isValidUrl(meetingLink.text.toString())) {
                    submitLoadingBar.visibility = View.GONE
                    meetingLink.setTextColor(resources.getColor(R.color.red))
                } else if (!isValidUrl(meetingLink.text.toString()) || candidateName.text.toString()
                        .isEmpty() || date.text.toString().isEmpty() || time.text.toString()
                        .isEmpty()
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill required fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    submitLoadingBar.visibility = View.GONE
                    submitBtn.visibility = View.VISIBLE
                } else {
                    val meeting = Meeting(
                        0,
                        candidateName.text.toString(),
                        user!!.username,
                        date.text.toString(),
                        time.text.toString(),
                        meetingLink.text.toString(),
                        false
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        meetingViewModel.createMeeting(meeting)
                    }

                    submitLoadingBar.visibility = View.GONE
                    submitBtn.visibility = View.VISIBLE
                    noMeetingText.visibility = View.GONE
                    popupWindow.dismiss()

                }
            }
            else{
                Toast.makeText(requireContext(), "Enter valid user",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun timePickerFun() {

        val hour = listOf("00","01","02","03","04","05","06","07","08","09","10","11")
        val minute = getMinutes()
        val amPm = listOf("AM","PM")
        val currentDate=LocalTime.now()

        val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.time_selection_bottomsheet_layout, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        val viewPagerHour : ViewPager2?=bottomSheetDialog.findViewById(R.id.view_pager_hour)
        val viewPagerMinute:ViewPager2?=bottomSheetDialog.findViewById(R.id.view_pager_minute)
        val viewPagerAmPm : ViewPager2?=bottomSheetDialog.findViewById(R.id.view_pager_amPm)
        val saveButton : MaterialButton?=bottomSheetDialog.findViewById(R.id.saveButton)
        val cancelButton :MaterialButton?=bottomSheetDialog.findViewById(R.id.cancelButton)
        if (viewPagerHour != null) {
            val currHour=currentDate.hour % 12
            viewPagerHour.clipToPadding = false
            viewPagerHour.clipChildren = false
            viewPagerHour.offscreenPageLimit = 1
            viewPagerHour.setPadding(120, 0, 120, 0) // Adjust padding as needed

            val adapter= DateAdapter(requireContext(),hour)
            viewPagerHour.adapter =adapter

            viewPagerHour.setCurrentItem(currHour-1,true)
            adapter.setSelectedItem(currHour-1)
            viewPagerHour.setPageTransformer { page, position ->
                val absPosition = abs(position)
                page.scaleY = 1 - (absPosition * 0.3f) // Scale the page
            }

            viewPagerHour.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.setSelectedItem(position)

                }
            })
        }else{
            Log.d("viewpager","sorry it is null")
        }
        if (viewPagerMinute != null) {

            val currMinute = currentDate.minute
            viewPagerMinute.clipToPadding = false
            viewPagerMinute.clipChildren = false
            viewPagerMinute.offscreenPageLimit = 1
            viewPagerMinute.setPadding(120, 0, 120, 0) // Adjust padding as needed

            val adapter= DateAdapter(requireContext(),minute)
            viewPagerMinute.adapter =adapter

            viewPagerMinute.setCurrentItem(currMinute,true)
            adapter.setSelectedItem(currMinute)
            viewPagerMinute.setPageTransformer { page, position ->
                val absPosition = abs(position)
                page.scaleY = 1 - (absPosition * 0.3f) // Scale the page
            }

            viewPagerMinute.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.setSelectedItem(position)

                }
            })
        }else{
            Log.d("viewpager","sorry it is null")
        }


        if (viewPagerAmPm != null) {

            viewPagerAmPm.clipToPadding = false
            viewPagerAmPm.clipChildren = false
//            viewPagerAmPm.offscreenPageLimit = 1
            viewPagerAmPm.setPadding(0, 50, 0, 50) // Adjust padding as needed

            val adapter= DateAdapter(requireContext(),amPm)
            viewPagerAmPm.adapter =adapter

            viewPagerAmPm.setCurrentItem(0,true)
            adapter.setSelectedItem(0)
            viewPagerAmPm.setPageTransformer { page, position ->
//                val absPosition = abs(position)
//                page.scaleY = 1 - (absPosition * 0.5f) // Scale the page
            }

            viewPagerAmPm.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.setSelectedItem(position)

                }
            })
        }else{
            Log.d("viewpager","sorry it is null")
        }




        saveButton?.setOnClickListener {
            val h = hour[viewPagerHour?.currentItem ?: 0]
            val min = minute[viewPagerMinute?.currentItem?:0]
            val period = amPm[viewPagerAmPm?.currentItem?:0]
            selectedTime = "$h:$min $period"
            time.setText(selectedTime)
            bottomSheetDialog.dismiss()

        }

        cancelButton?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


    }

    private fun datePickerFun(){
        var selectedMonth=""
        var selectedDay=""
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.start_date_picker_popup, null)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.show()
        // Customize and handle date selection logic here if needed
        val viewPagerMonth: ViewPager2? = bottomSheetDialog.findViewById(R.id.view_pager_month)
        val title: TextView? = bottomSheetDialog.findViewById(R.id.start_date_title)
        val saveBtn: MaterialButton? = bottomSheetDialog.findViewById(R.id.start_date_picker_selectDate_Button)
        val cancelBtn: MaterialButton? = bottomSheetDialog.findViewById(R.id.start_date_picker_cancel_Button)
        val viewPagerDays: ViewPager2? = bottomSheetDialog.findViewById(R.id.view_pager_year)

        val currentDate = LocalDateTime.now()
        val currMonth = currentDate.month.value

        title?.text = "Select date"
        val days = mutableListOf<String>()
        val daysAdapter = DateAdapter(requireContext(), days)
        viewPagerDays?.adapter = daysAdapter

        val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        if (viewPagerDays != null) {
            viewPagerDays.clipToPadding = false
            viewPagerDays.clipChildren = false
            viewPagerDays.offscreenPageLimit = 1
            viewPagerDays.setPadding(140, 0, 140, 0) // Adjust padding as needed

            viewPagerDays.setPageTransformer { page, position ->
                val absPosition = Math.abs(position)
                page.scaleY = 1 - (absPosition * 0.3f) // Scale the page
            }

            viewPagerDays.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    daysAdapter.setSelectedItem(position)


                }
            })
        }else{
            Log.d("viewpager","sorry it is null")
        }
        if (viewPagerMonth != null) {
            viewPagerMonth.clipToPadding = false
            viewPagerMonth.clipChildren = false
            viewPagerMonth.offscreenPageLimit = 1
            viewPagerMonth.setPadding(140, 0, 140, 0) // Adjust padding as needed

            val adapter = DateAdapter(requireContext(), months)
            viewPagerMonth.adapter = adapter

            viewPagerMonth.setCurrentItem(currMonth - 1, true)
            adapter.setSelectedItem(currMonth - 1)

            viewPagerMonth.setPageTransformer { page, position ->
                val absPosition = Math.abs(position)
                page.scaleY = 1 - (absPosition * 0.3f) // Scale the page
            }

            viewPagerMonth.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.setSelectedItem(position)
                    val selectedMonth = months[position]
                    val daysInMonth = getDaysInMonth(months[position])
                    days.clear()
                    days.addAll( daysInMonth)
                    daysAdapter.setItems(daysInMonth)
                    viewPagerDays?.setCurrentItem(0, true)

                }
            })
        } else {
            Log.d("viewpager", "Sorry it is null")
        }


        saveBtn?.setOnClickListener {
            selectedMonth=months[viewPagerMonth?.currentItem ?: 0]
            selectedDay = days[viewPagerDays?.currentItem?: 0]
            date.setText("$selectedDay-$selectedMonth-${LocalDate.now().year}")
            bottomSheetDialog.dismiss()
        }
        cancelBtn?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

    }
    private fun sendMeetingCreatedNotification(meeting : Meeting) {
        val notificationRequest = NotificationRequest(meeting.hr, "Meeting"," scheduled meeting",meeting.user,Lists().notificationPref[3])
        CoroutineScope(Dispatchers.IO).launch{
            notificationViewModel.sendNotification(notificationRequest)
        }
    }
    private fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
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

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleMeeting(meeting: Meeting){

//        showOverlayView()
        val alarmManager = requireContext().getSystemService( Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("meetingId", meeting.id) // Pass meeting data
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), meeting.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("dd-M-yyyy")
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

    private fun removeMeetingFromSchedule(currMeeting: Meeting) {
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

    // Function to get the number of days in a month
    private fun getDaysInMonth(month: String): List<String> {

        val days = mutableListOf<String>()
        for (day in 1..31) {
            days.add(String.format("%02d", day))
        }
        return when(month){
            "02" ->{
                days.subList(0,28)
            }
            "04","06","09","11" ->{
                days.subList(0,30)
            }
            else ->{
                days
            }
        }
    }

    private fun getMinutes():List<String>{
        val minutes = mutableListOf<String>()
        for(minute in 0 .. 59){
            minutes.add(String.format("%02d", minute))
        }
        return minutes
    }
}