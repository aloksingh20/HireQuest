package com.example.gethired

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.NotificationViewModel
import com.example.gethired.ViewModel.RegisterLoginViewModel
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.NotificationViewModelFactory
import com.example.gethired.utils.Lists
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: Switch
    private lateinit var darkModeSwitch:Switch
    private lateinit var changePasswordBtn: CardView
    private lateinit var backButton:ImageView


    private lateinit var logoutBtn:CardView

    private lateinit var registerLoginViewModel: RegisterLoginViewModel

    private lateinit  var sharedPref: SharedPreferences
    private var currentUser: UserDto?=null

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var tokenManager: TokenManager

    private lateinit  var notificationSharedPref: SharedPreferences

    private lateinit var networkManager: NetworkManager
    private  var isConnectedToInternet:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        networkManager = NetworkManager(this)
        lifecycleScope.launch(Dispatchers.Main) { // Launch coroutine on Main dispatcher
            networkManager.getNetworkConnectivityFlow()
                .flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher
                .collect {   // Collect flow on Main dispatcher
                    // Update UI or perform actions based on network state
                    if (it.isConnected) {
                        // Network available
//                        showSnackBar("Internet is restored","ok")

                    } else {
                        // Network lost
                        isConnectedToInternet = false
                        showSnackBar("No network, Please connect to internet", "retry")
                    }
                }

        }


        tokenManager = TokenManager(this)
        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        notificationSharedPref =
            getSharedPreferences(NOTIFICATION_PREF_FILE_NAME, Context.MODE_PRIVATE)
        notificationViewModel = ViewModelProvider(
            this,
            NotificationViewModelFactory(tokenManager)
        )[NotificationViewModel::class.java]

        currentUser = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()

        registerLoginViewModel = ViewModelProvider(this)[RegisterLoginViewModel::class.java]

        backButton = findViewById(R.id.back_button)
        notificationSwitch = findViewById(R.id.setting_notification_switch)
        darkModeSwitch = findViewById(R.id.setting_darkMode_switch)
        changePasswordBtn = findViewById(R.id.setting_password_container)
        logoutBtn = findViewById(R.id.setting_logout_container)


        backButton.setOnClickListener {
            onBackPressed()
        }
        val isChecked = notificationSharedPref.getBoolean("all", true)
        if (notificationSharedPref.getBoolean("all", true)) {

            ifChecked(notificationSwitch)
        } else {
            ifUnChecked(notificationSwitch)
        }
        notificationSwitch.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (notificationSwitch.isChecked) {
                    ifChecked(notificationSwitch)
                    notificationViewModel.unMuteAllNotification(currentUser!!.id)
                    notificationViewModel.isAllUnMuted.observe(this@SettingActivity){isUnMuted->
                        if (!isUnMuted) {
                            Toast.makeText(this@SettingActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                            ifUnChecked(notificationSwitch)
                        } else {
                            ifChecked(notificationSwitch)
                            unMuteAll()
                        }
                    }

                }
                if (!notificationSwitch.isChecked) {
                    ifUnChecked(notificationSwitch)
                    notificationViewModel.muteAllNotification(currentUser!!.id)
                    notificationViewModel.isAllMuted.observe(this@SettingActivity){isMuted->
                        if (!isMuted!!) {
                            Toast.makeText(this@SettingActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                            ifChecked(notificationSwitch)
                        } else {
                            ifUnChecked(notificationSwitch)
                            muteAll()
                        }
                    }
                }

            }
        }
        darkModeSwitch.isChecked = isNightModeEnabled()
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update the night mode based on the switch state
            updateNightMode(this, isChecked)

            if (isChecked) {
                ifChecked(darkModeSwitch)
            } else {
                ifUnChecked(darkModeSwitch)
            }
        }
        changePasswordBtn.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }


        logoutBtn.setOnClickListener {
//            val bottomSheetFragment = Logout_BottomSheet_Fragment()
//            bottomSheetFragment.show(supportFragmentManager, "bottom_sheet_tag")
//
//            bottomSheetFragment.show(supportFragmentManager, "bottom_sheet_tag")
            val bottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
//                val bottomSheetDialogWindow=bottomSheetDialog.window
//            bottomSheetDialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val view = layoutInflater.inflate(R.layout.logout_confirmation_popup, null)
            bottomSheetDialog.setContentView(view)

            bottomSheetDialog.show()
            val confirmButton:MaterialButton=view.findViewById(R.id.logout_confirmation_popup_logoutBtn)
            val cancelButton:MaterialButton=view.findViewById(R.id.logout_confirmation_popup_cancelBtn)

            confirmButton.setOnClickListener{

                if(isConnectedToInternet==true){
                    registerLoginViewModel.logout(currentUser!!.username).observe(this){
                        if(it){
                            val editor = sharedPref.edit()
                            editor.clear()
                            editor.apply()

                            val notificationSharedPreferencesEditor=notificationSharedPref.edit()
                            notificationSharedPreferencesEditor.clear()
                            notificationSharedPreferencesEditor.apply()


                            val intent=Intent(this,LoginActivity::class.java)
                            intent.putExtra("username",currentUser!!.username)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,"Something went wrong!",Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    showSnackBar("No network, Please connect to internet","retry")
                }
            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

        }



    }
    @SuppressLint("CommitPrefEdits")
    private fun muteAll() {
        val editor=notificationSharedPref.edit()
        val notificationPref=Lists().notificationPref
        for(i in notificationPref){
            editor.putBoolean(i,false)
        }
        editor.putBoolean("all",false)
        editor.apply()

    }

    private fun unMuteAll() {
        val editor=notificationSharedPref.edit()
        val notificationPref=Lists().notificationPref
        for(i in notificationPref){
            editor.putBoolean(i,true)
        }
        editor.putBoolean("all",true)
        editor.apply()
    }
    private fun ifChecked(switch: Switch){
        switch.isChecked=true
        switch.backgroundTintList= ContextCompat.getColorStateList(this,R.color.on)
        switch.trackTintList=ContextCompat.getColorStateList(this,R.color.on)
        switch.thumbTintList=ContextCompat.getColorStateList(this,R.color.white)

    }
    private fun ifUnChecked(switch: Switch) {
        switch.isChecked=false
        switch.backgroundTintList = ContextCompat.getColorStateList(this, R.color.off)
        switch.trackTintList = ContextCompat.getColorStateList(this, R.color.off)
        switch.thumbTintList = ContextCompat.getColorStateList(this, R.color.text)

    }
    private fun isNightModeEnabled(): Boolean {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        return if(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES){
            ifChecked(darkModeSwitch)
            true
        }else{
            ifUnChecked(darkModeSwitch)
            false
        }
    }
    private fun updateNightMode(context: Context, isNightMode: Boolean) {
        // Set the night mode and save the state to SharedPreferences
        val nightModeValue = if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightModeValue)
        saveNightModeToPrefs(context, nightModeValue)
    }

    private fun saveNightModeToPrefs(context: Context, nightMode: Int) {
        val prefs = context.getSharedPreferences("night_mode_prefs", Context.MODE_PRIVATE).edit()
        prefs.putInt("night_mode", nightMode)
        prefs.apply()
    }
    private fun showSnackBar(message: String,action:String){
        isConnectedToInternet=true
        val snackbar = Snackbar.make(findViewById(R.id.setting_activity), message, Snackbar.LENGTH_SHORT)
        snackbar.setAction(action) {
            // Handle retry logic
        }
        snackbar.show()
    }
}