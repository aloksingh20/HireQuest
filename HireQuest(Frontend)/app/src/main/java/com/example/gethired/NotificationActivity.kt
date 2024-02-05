package com.example.gethired

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.contextaware.withContextAvailable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.NotificationViewModel
import com.example.gethired.adapter.NotificationAdapter
import com.example.gethired.entities.Notification
import com.example.gethired.factory.NotificationViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActivity : AppCompatActivity() {

    private lateinit var emptyText: TextView
    private lateinit var backButton: ImageView
    private lateinit var loadingBar:LottieAnimationView

    private var notificationList: MutableList<Notification> = mutableListOf()
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var tokenManager: TokenManager

    private lateinit var notificationSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        tokenManager = TokenManager(this)

        val user = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()

        notificationViewModel = ViewModelProvider(
            this,
            NotificationViewModelFactory(tokenManager)
        )[NotificationViewModel::class.java]
        notificationSharedPreferences =
            getSharedPreferences(NOTIFICATION_PREF_FILE_NAME, Context.MODE_PRIVATE)

        emptyText = findViewById(R.id.notification_empty_text)
        backButton = findViewById(R.id.backBtn)
        loadingBar = findViewById(R.id.notificationLoadingBar)

        notificationRecyclerView = findViewById(R.id.notification_recyclerView)

        if (!::notificationAdapter.isInitialized) {
            notificationAdapter = NotificationAdapter(notificationList, this)
            notificationRecyclerView.adapter = notificationAdapter
            notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        }
        lifecycleScope.launch {
            notificationViewModel.getAllNotification(user!!.username)
        }
        notificationViewModel.notificationList.observe(this@NotificationActivity) { notifications ->

            if (notifications.isNullOrEmpty()) {
                emptyText.visibility = View.VISIBLE
            } else {

                emptyText.visibility = View.GONE
                notificationRecyclerView.visibility=View.VISIBLE
                notificationList.clear()
                notificationList.addAll(notifications)
                notificationAdapter.updateData(notificationList)
            }
        }
        notificationViewModel.error.observe(this@NotificationActivity) { error ->
            if (error.isNullOrEmpty()) {

            } else {

            }
        }
        notificationViewModel.loading.observe(this@NotificationActivity) { isLoading ->
            if (isLoading) {
                loadingBar.visibility=View.VISIBLE
            } else {
                loadingBar.visibility=View.GONE
            }
        }


        notificationAdapter.setOnEditIconClickListener(object :
            NotificationAdapter.OnEditIconClickListener {
            override fun onEditIconClick(position: Int) {
                notificationEditBottomSheet(position)
            }
        })

        notificationAdapter.setOnItemClickListener(object :
            NotificationAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewSelectedNotification(position)
                }
            }

        })

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private suspend fun viewSelectedNotification(position: Int) {
        val currNotification = notificationList[position]
        notificationViewModel.updateNotification(currNotification.id)
        notificationViewModel.updatedNotification.observe(this@NotificationActivity) {
            currNotification.readStatus = true
            notificationAdapter.notifyItemChanged(position)
        }
        notificationViewModel.error.observe(this@NotificationActivity) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notificationEditBottomSheet(position: Int) {
        val bottomSheetDialog =
            BottomSheetDialog(this@NotificationActivity, R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.notification_bottomsheet_popup, null)
        bottomSheetDialog.setContentView(view)

        val deleteNotification =
            view.findViewById<RelativeLayout>(R.id.notification_bottomSheet_delete_notification_container)
        val muteUnMuteNotification =
            view.findViewById<RelativeLayout>(R.id.notification_bottomSheet_turnOff_notification_container)
        val muteIcon =
            view.findViewById<ImageView>(R.id.notification_bottomSheet_offNotification_icon)
        val muteTitle =
            view.findViewById<TextView>(R.id.notification_bottomSheet_offNotification_title)
        val muteDescription =
            view.findViewById<TextView>(R.id.notification_bottomSheet_offNotification_sub_title)
        val closeBtn = view.findViewById<ImageView>(R.id.notification_bottomSheet_clearIcon)
        val currNotification = notificationList[position]

        val isMuted =
            notificationSharedPreferences.getBoolean(currNotification.notificationType, true)

        if (isMuted) {
            muteTitle.text = "Turn On"
            muteDescription.text = "Start receiving this type of notifications"
            muteIcon.setImageResource(R.drawable.icon_notification_inactive)
        } else {
            muteTitle.text = "Turn Off"
            muteDescription.text = "Stop receiving this type of notifications"
            muteIcon.setImageResource(R.drawable.icon_off_notification)
        }

        deleteNotification.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                notificationViewModel.deleteNotification(currNotification.id)
            }
            notificationList.removeAt(position)
            notificationAdapter.notifyItemRemoved(position)
            bottomSheetDialog.dismiss()
        }

        muteUnMuteNotification.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                notificationViewModel.updateNotificationPreference(
                    currNotification.notificationType,
                    CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()!!.id
                )
            }
            val editor = notificationSharedPreferences.edit()
            editor.putBoolean(currNotification.notificationType, !isMuted)
            editor.apply()
            bottomSheetDialog.dismiss()
        }

        closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.show()
    }
}