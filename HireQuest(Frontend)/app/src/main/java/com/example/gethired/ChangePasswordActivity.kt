package com.example.gethired

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var backButton:ImageView
    private lateinit var currentPassword:TextInputEditText
    private lateinit var newPassword:TextInputEditText
    private lateinit var confirmPassword:TextInputEditText
    private lateinit var submitButton:MaterialButton
    private lateinit var submitButtonLoadingAnimation : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        backButton=findViewById(R.id.back_button)
        currentPassword=findViewById(R.id.change_password_current_password_edittext)
        newPassword=findViewById(R.id.change_password_new_password_edittext)
        confirmPassword=findViewById(R.id.change_password_confirm_password_edittext)
        submitButton=findViewById(R.id.change_password_submit_button)
        submitButtonLoadingAnimation=findViewById(R.id.submit_button_loading_animation)

        submitButton.setOnClickListener {

        }


    }
}