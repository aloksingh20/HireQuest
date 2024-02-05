package com.example.gethired.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.LoginActivity
import com.example.gethired.R
import com.example.gethired.ViewModel.OtpViewModel
import com.example.gethired.entities.OtpResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordGetOtp : Fragment() {
    private lateinit var backToLogin: ImageView
    private lateinit var emailEditText: TextInputEditText
    private lateinit var backToSignUp: TextView
    private lateinit var sendOtp: MaterialButton
    lateinit var loadingAnimation: LottieAnimationView

    private lateinit var otpViewModel: OtpViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView=inflater.inflate(R.layout.fragment_forgot_password_get_otp, container, false)
        emailEditText=rootView.findViewById(R.id.forgotPasswordEmailEditText)
        backToLogin=rootView.findViewById(R.id.forgotPasswordBackBtn)
        backToSignUp=rootView.findViewById(R.id.joinUsBtn)
        sendOtp=rootView.findViewById(R.id.forgotPasswordGetOtp)
        loadingAnimation=rootView.findViewById(R.id.loading_animation)

        otpViewModel= ViewModelProvider(this)[OtpViewModel::class.java]
        backToSignUp.setOnClickListener {
            moveToSignUp()
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        backToLogin.setOnClickListener {
            val intent= Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Finish the current activity
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        }

        sendOtp.setOnClickListener {
            if(emailEditText.text.toString().isNotEmpty()||isValidEmail(emailEditText.text.toString())){
                sendOtp.visibility=View.GONE
                loadingAnimation.visibility=View.VISIBLE
                sendOtpFun()
            }else{
                Toast.makeText(requireContext(),"Please enter valid Email", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }

    private fun sendOtpFun() {
        CoroutineScope(Dispatchers.IO).launch {
            otpViewModel.sendOtp(emailEditText.text.toString())
            otpViewModel.createdOtp.observe(viewLifecycleOwner){otpResponse->
                if(otpResponse!=null){
                    moveToOtpVerification()
                }
            }
            otpViewModel.error.observe(viewLifecycleOwner){ error->
                sendOtp.visibility=View.VISIBLE
                loadingAnimation.visibility=View.GONE
                Toast.makeText(requireContext(),"Please try in sometime",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveToOtpVerification() {
        val bundle = Bundle()
        bundle.putString("email", emailEditText.text.toString())

//         Create an instance of your GetOtpFragment
        val enterOtpFragment = ForgotPasswordEnterOtp()
        enterOtpFragment.arguments=bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.getOtpFrameLayout, enterOtpFragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()

    }


    private fun moveToSignUp(){
        val intent= Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Finish the current activity
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)


    }
    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}