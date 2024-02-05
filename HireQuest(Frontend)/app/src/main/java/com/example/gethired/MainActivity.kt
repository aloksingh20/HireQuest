package com.example.gethired

import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.ChatViewModel
import com.example.gethired.ViewModel.NotificationViewModel
import com.example.gethired.entities.NotificationPreference
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.ChatViewModelFactory
import com.example.gethired.factory.NotificationViewModelFactory
import com.example.gethired.fragment.*
import com.example.gethired.websocket.WebSocketManager
import com.example.gethired.websocket.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val NOTIFICATION_PREF_FILE_NAME = "notification_preference"

class MainActivity : AppCompatActivity() {
    private lateinit var homeBtn:LinearLayout
    private lateinit var searchBtn:LinearLayout
    private lateinit var chatBtn:LinearLayout
    private lateinit var profileBtn:LinearLayout

    private lateinit var home_icon:ImageView
    private lateinit var search_icon:ImageView
    private lateinit var chat_icon:ImageView
    private lateinit var profile_icon:ImageView


    private lateinit var webSocketService: WebSocketService
    private var bound = false
    private var currentFragment: Fragment? = null
    private  var token: String=""
    private lateinit var tokenManager:TokenManager
    private lateinit var chatViewModel:ChatViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private var notificationPreferences:List<NotificationPreference> = emptyList()
    private var user: UserDto? = null

    private lateinit  var sharedPref: SharedPreferences

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences(NOTIFICATION_PREF_FILE_NAME, Context.MODE_PRIVATE)
        CommonFunction.SharedPrefsUtil.init(applicationContext)

        user = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
        tokenManager=TokenManager(this@MainActivity)


        token=tokenManager.getToken().toString()
         // Get the token from somewhere
        val serviceIntent = Intent(this, WebSocketService::class.java)
        serviceIntent.putExtra("TOKEN", token)
        serviceIntent.putExtra("LOGGED_IN_USER", user!!.id)
        startService(serviceIntent)

        // Bind to the service to access its methods
        val bindIntent = Intent(this, WebSocketService::class.java)
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        chatViewModel = ViewModelProvider(this, ChatViewModelFactory(tokenManager, this))[ChatViewModel::class.java]
        notificationViewModel= ViewModelProvider(this, NotificationViewModelFactory(tokenManager))[NotificationViewModel::class.java]



        homeBtn=findViewById(R.id.home)
        searchBtn=findViewById(R.id.search)
        chatBtn=findViewById(R.id.chat)
        profileBtn=findViewById(R.id.profile)

        home_icon=findViewById(R.id.home_icon)
        search_icon=findViewById(R.id.search_icon)
        chat_icon=findViewById(R.id.chat_icon)
        profile_icon=findViewById(R.id.profile_icon)
//        if (savedInstanceState == null) {
//            val defaultFragment = HomeFragment() // Replace with the actual fragment class
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, defaultFragment)
//                .commit()
//        }


        if(user!!.isRecuriter== 1){
            replace(HrDashBoardFragment())
        }else{
            replace(HomeFragment())
        }

        resetButtons()
        home_icon.setImageResource(R.drawable.icon_home_active)


        lifecycleScope.launch {
            updateNotificationPreferences(user!!.id)
        }

        homeBtn.setOnClickListener {
            if (currentFragment !is HomeFragment) {
                resetButtons()

                if (user!!.isRecuriter == 1) {
                    home_icon.setImageResource(R.drawable.icon_home_active)
                    replace(HrDashBoardFragment(), isBackward = currentFragment is SearchFragment || currentFragment is ChatFragment)
                } else {
                    home_icon.setImageResource(R.drawable.icon_home_active)
                    replace(HomeFragment(), isBackward = currentFragment is SearchFragment || currentFragment is ChatFragment)
                }
            }
        }

        searchBtn.setOnClickListener {
            if (currentFragment !is SearchFragment) {
                resetButtons()
                search_icon.setImageResource(R.drawable.icon_search_active)
                replace(SearchFragment(), isBackward = currentFragment is ChatFragment)
            }
        }

        chatBtn.setOnClickListener {
            if (currentFragment !is ChatFragment) {
                resetButtons()
                chat_icon.setImageResource(R.drawable.icon_chat_active)
                replace(ChatFragment(), isBackward = false) // No need to slide back from here
            }
        }

        profileBtn.setOnClickListener {
            resetButtons()
            profile_icon.setImageResource(R.drawable.icon_profile_active)
            val i = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(i)
        }




    }
    private fun replace(fragment: Fragment, isBackward: Boolean = false) {
        currentFragment = fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (isBackward) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        fragmentTransaction.replace(R.id.FL, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }


    private fun resetButtons() {

        home_icon.setImageResource(R.drawable.icon_home)

        search_icon.setImageResource(R.drawable.icon_search)

        chat_icon.setImageResource(R.drawable.icon_chat)

        profile_icon.setImageResource(R.drawable.icon_profile)

    }

    override fun onResume() {
        super.onResume()

        // Reset buttons and backgrounds when MainActivity resumes
        resetButtons()
        when (currentFragment) {
            is HrDashBoardFragment -> {
                home_icon.setImageResource(R.drawable.icon_home_active)
            }
            is HomeFragment -> {
                home_icon.setImageResource(R.drawable.icon_home_active)
            }
            is SearchFragment -> {
                search_icon.setImageResource(R.drawable.icon_search_active)
            }
            is ChatFragment -> {
                chat_icon.setImageResource(R.drawable.icon_chat_active)
            }
        }

//        when (currentFragment) {
//            is SearchFragment -> {
//                if (currentFragment!!.isVisible) {
//                    // Update UI with saved search query and progress
//                    (currentFragment as SearchFragment).restoreState(outState)
//                }
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as WebSocketService.LocalBinder
            webSocketService = binder.getService()
            webSocketService.setChatViewModel(chatViewModel)
            bound = true
            // Assign the service to the WebSocketManager

            WebSocketManager.webSocketService = webSocketService
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun updateNotificationPreferences(userId:Long){
//        CoroutineScope(Dispatchers.IO).launch {
////            notificationViewModel.getNotificationPreference(userId)
//            notificationViewModel.notificationPreferencesList.observe(this@MainActivity){notificationPreferencesList->
//                if(notificationPreferencesList!=null){
//                    notificationPreferences=notificationPreferencesList
//                    val editor = sharedPref.edit()
//
//                    var isAllMuted=true
//                    for(i in notificationPreferences){
//                        editor.putBoolean(i.notificationType,i.muted)
//                        if(!i.muted){
//                            isAllMuted=false
//                        }
//                    }
//                    editor.putBoolean("all",isAllMuted)
//                    editor.apply()
//                }
//            }
//        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//
//        if (currentFragment is SearchFragment && currentFragment!!.isVisible) {
//            outState.putBundle("searchFragmentState", (currentFragment as SearchFragment).onSaveInstanceState())
//        }
//
//    }

}