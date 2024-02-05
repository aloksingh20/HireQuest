package com.example.gethired

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.gethired.utils.FontInterceptor
import com.google.firebase.FirebaseApp
import io.github.inflationx.viewpump.ViewPump


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CommonFunction.SharedPrefsUtil.init(this)

        nightMode()

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(FontInterceptor())
                .build()
        )
        FirebaseApp.initializeApp(this)

    }

    private fun getNightModeFromPrefs(): Int {
        val prefs = getSharedPreferences("night_mode_prefs", MODE_PRIVATE)
        return prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    private fun nightMode(){
        val nightMode = getNightModeFromPrefs()

        // Set the night mode for the entire application
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

}
