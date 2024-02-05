package com.example.gethired

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gethired.fragment.ForgotPasswordGetOtp

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val getOtpFragment = ForgotPasswordGetOtp()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.getOtpFrameLayout, getOtpFragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
//        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}