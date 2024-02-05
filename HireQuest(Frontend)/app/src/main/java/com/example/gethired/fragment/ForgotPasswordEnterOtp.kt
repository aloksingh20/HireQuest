package com.example.gethired.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.ResponseCallback
import com.example.gethired.R
import com.example.gethired.ViewModel.OtpViewModel
import com.example.gethired.entities.OtpResponse
import com.example.gethired.entities.Response
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordEnterOtp : Fragment() {

    private lateinit var code1: EditText
    private lateinit var code2: EditText
    private lateinit var code3: EditText
    private lateinit var code4: EditText
    private lateinit var code5: EditText
    private lateinit var email: TextView
    private lateinit var changeEmail: TextView
    private lateinit var resendTimerLayout: LinearLayout
    private lateinit var timer: TextView
    private lateinit var verifyOtpBtn: MaterialButton
    private lateinit var resend: TextView
    lateinit var loadingAnimation: LottieAnimationView

    private lateinit var otpViewModel: OtpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =
            inflater.inflate(R.layout.fragment_forgot_password_enter_otp, container, false)
        startTimer()
        val emailFromSendOtpActivity = arguments?.getString("email")

        otpViewModel = ViewModelProvider(this)[OtpViewModel::class.java]

        code1 = rootView.findViewById(R.id.otp1)
        code2 = rootView.findViewById(R.id.otp2)
        code3 = rootView.findViewById(R.id.otp3)
        code4 = rootView.findViewById(R.id.otp4)
        code5 = rootView.findViewById(R.id.otp5)
        resendTimerLayout = rootView.findViewById(R.id.enterOtp_resendLayout)
        resend = rootView.findViewById(R.id.resendOtp)
        timer = rootView.findViewById(R.id.resendTimerTextView)
        email = rootView.findViewById(R.id.enterOtp_email)
        changeEmail = rootView.findViewById(R.id.enterOtp_changeEmail)
        verifyOtpBtn = rootView.findViewById(R.id.enterOtp_verificationBtn)
        loadingAnimation = rootView.findViewById(R.id.loading_animation)

        email.setText(emailFromSendOtpActivity)

        changeEmail.setOnClickListener {
            val getOtpFragment = ForgotPasswordGetOtp()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.getOtpFrameLayout, getOtpFragment)
            transaction.addToBackStack(null) // Optional: Add to back stack
            transaction.commit()
            removeAllFragmentsFromBackStack()
        }
        code1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    code2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        code2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    code3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        code3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    code4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        code4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    code5.requestFocus()
                    // Move focus to the next field
                    // Implement similar logic for other fields
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        code5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        code2.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (code2.text.isEmpty()) {
                    code1.requestFocus()
                }
            }
            false
        }
        code3.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (code3.text.isEmpty()) {
                    code2.requestFocus()
                }
            }
            false
        }
        code4.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (code4.text.isEmpty()) {
                    code3.requestFocus()
                }
            }
            false
        }
        code5.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (code5.text.isEmpty()) {
                    code4.requestFocus()
                }
            }
            false
        }

        resend.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                otpViewModel.sendOtp(emailFromSendOtpActivity.toString())
                otpViewModel.createdOtp.observe(viewLifecycleOwner) {
                    resend.visibility = View.GONE
                    resendTimerLayout.visibility = View.VISIBLE
                    startTimer()
                }
            }
        }


        verifyOtpBtn.setOnClickListener {
            val otp = "${code1.text}${code2.text}${code3.text}${code4.text}${code5.text}"
            loadingAnimation.visibility = View.VISIBLE
            verifyOtpBtn.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {
                otpViewModel.verifyOtp(otp, emailFromSendOtpActivity.toString())
                otpViewModel.verifyOtp.observe(viewLifecycleOwner) { response ->
                    val bundle = Bundle()
                    bundle.putString("email", emailFromSendOtpActivity.toString())

                    val changePasswordFragment = ChangePasswordFragment()
                    changePasswordFragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.getOtpFrameLayout, changePasswordFragment)
                    transaction.addToBackStack(null) // Optional: Add to back stack
                    transaction.commit()
                    removeAllFragmentsFromBackStack()
                }
                otpViewModel.error.observe(viewLifecycleOwner) { error ->
                    loadingAnimation.visibility = View.GONE
                    verifyOtpBtn.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Try again, Enter correct Otp",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }


        return rootView
    }
        private fun removeAllFragmentsFromBackStack() {
            val fragmentManager = childFragmentManager
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        private fun startTimer() {
            val initialTimeMillis: Long = 30 * 1000

            // Create a countdown timer
            val countdownTimer = object : CountDownTimer(initialTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Update the TextView with the remaining seconds
                    val secondsRemaining = millisUntilFinished / 1000

                    val formattedTime = if (secondsRemaining < 10) {
                        "0$secondsRemaining"
                    } else {
                        secondsRemaining.toString()
                    }

                    // Update the TextView with the formatted time

                    timer.text = "00:" + formattedTime
                }

                override fun onFinish() {
                    // Timer has finished, you can perform actions here
                    timer.text = "00:00"
                    resendTimerLayout.visibility = View.GONE
                    resend.visibility = View.VISIBLE
                }
            }

            // Start the countdown timer
            countdownTimer.start()
        }
    }

