package com.example.gethired.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.LoginActivity
import com.example.gethired.OnBackPressedListener
import com.example.gethired.R
import com.example.gethired.ViewModel.RegisterLoginViewModel
import com.example.gethired.entities.UserDto
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordFragment : Fragment() , OnBackPressedListener {

    lateinit var password:TextInputEditText
    lateinit var passwordTextInputLayout: TextInputLayout
    lateinit var confirmPassword:TextInputEditText
    lateinit var confirmPasswordTextInputLayout: TextInputLayout
    lateinit var updatePassword:MaterialButton
    lateinit var loadingAnimation: LottieAnimationView
    var isPasswordValid = false
    private var isPopupVisible = false

    lateinit var  errorColor: ColorStateList
    lateinit var  validColor: ColorStateList
    lateinit var available: ColorStateList

    private lateinit var registerLoginViewModel: RegisterLoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView=inflater.inflate(R.layout.fragment_change_password, container, false)
        registerLoginViewModel= ViewModelProvider(this)[RegisterLoginViewModel::class.java]
        val emailFromSendOtpActivity = arguments?.getString("email")

        password=rootView.findViewById(R.id.changePassword_password)
        passwordTextInputLayout=rootView.findViewById(R.id.changePassword_password_heading)
        confirmPassword=rootView.findViewById(R.id.changePassword_confirm_password)
        confirmPasswordTextInputLayout=rootView.findViewById(R.id.changePassword_confirm_password_heading)
        updatePassword=rootView.findViewById(R.id.changePassword_updatePasswordBtn)
        loadingAnimation=rootView.findViewById(R.id.loading_animation)

        errorColor= ContextCompat.getColorStateList(requireContext(), R.color.red)!!
        validColor= ContextCompat.getColorStateList(requireContext(), R.color.text)!!
        available= ContextCompat.getColorStateList(requireContext(), R.color.isAvailable)!!


        password.addTextChangedListener(passwordTextWatcher)
        confirmPassword.addTextChangedListener(confirmPasswordTextWatcher)

        updatePassword.setOnClickListener {
            if(isPasswordValid){
                loadingAnimation.visibility=View.VISIBLE
                updatePassword.visibility=View.GONE
                registerLoginViewModel.updatePassword(emailFromSendOtpActivity.toString(),password.text.toString(),object :UpdateUserCallback{
                    override fun onUserUpdated(updatedUserDto: UserDto) {
                        showChangePasswordCompleted()
                        rootView.alpha= 0.3F
                    }

                    override fun onUpdateUserError() {
                        updatePassword.visibility=View.VISIBLE
                        loadingAnimation.visibility=View.GONE
                        Toast.makeText(requireContext(),"Error on updating password, Try in some time!",Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
        return rootView

    }

    private val passwordTextWatcher =object : TextWatcher {
        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val password = s.toString()

            if (password.isEmpty()) {
                isPasswordValid=false
                passwordTextInputLayout.helperText="Password is Empty"
                passwordTextInputLayout.boxStrokeColor=
                    ContextCompat.getColor(requireContext(), R.color.red)
            } else if(password.length<6){
                isPasswordValid=false
                passwordTextInputLayout.helperText="length 6 or more"
                passwordTextInputLayout.setHelperTextColor(errorColor)
                passwordTextInputLayout.hintTextColor = errorColor
                passwordTextInputLayout.boxStrokeColor=
                    ContextCompat.getColor(requireContext(), R.color.red)
            }
            else{
                isPasswordValid=true
                passwordTextInputLayout.helperText=null
                passwordTextInputLayout.setHelperTextColor(validColor)
                passwordTextInputLayout.hintTextColor = available
                passwordTextInputLayout.boxStrokeColor=
                    ContextCompat.getColor(requireContext(), R.color.isAvailable)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val confirmPasswordTextWatcher =object :TextWatcher {
        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val confirmPassword = s.toString()

            if (!confirmPassword.equals(password.text.toString())) {
                confirmPasswordTextInputLayout.helperText="Password does not matches"
                confirmPasswordTextInputLayout.hintTextColor=errorColor
                confirmPasswordTextInputLayout.boxStrokeColor=ContextCompat.getColor(requireContext(),
                    R.color.red
                )
            }
            else{
                confirmPasswordTextInputLayout.helperText="Password matches"
                confirmPasswordTextInputLayout.setHelperTextColor(available)
                confirmPasswordTextInputLayout.hintTextColor = available
                confirmPasswordTextInputLayout.boxStrokeColor=ContextCompat.getColor(requireContext(),
                    R.color.isAvailable
                )
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun showChangePasswordCompleted(){

        isPopupVisible=true
        // Create and show the popup menu
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.after_password_change_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Set background drawable
        popupWindow.animationStyle= R.style.PopupAnimation
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
// Set outside touch-ability
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        popupWindow.isOutsideTouchable = false
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(requireView().findViewById(R.id.changePassword_password), Gravity.CENTER, 0, 0)
        val backToLogin=popupView.findViewById<MaterialButton>(R.id.backToLogin)
        backToLogin.setOnClickListener {
            val intent=Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            removePreviousFragments()
            parentFragmentManager.popBackStack()
            isPopupVisible = false


        }

    }
    override fun onBackPressed() {
        if (isPopupVisible) {
            // If the popup is visible, do nothing on back button press
        } else {
            // If the popup is not visible, perform the default back button action
//            requireActivity().onBackPressed()
        }
    }
    fun removePreviousFragments(){
        val fragmentManager = childFragmentManager // or getChildFragmentManager() if you are in a Fragment
        val backStackCount = fragmentManager.backStackEntryCount

        for (i in 0 until backStackCount) {
            fragmentManager.popBackStackImmediate()
        }
    }

}