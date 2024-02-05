package com.example.gethired

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gethired.Callback.UsernameAvailabilityCallback
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity()  {
    lateinit var emailEditText:TextInputEditText
    lateinit var emailTextInputLayout:TextInputLayout
    lateinit var continueBtn:MaterialButton
    lateinit var signIn:TextView

    lateinit var  errorColor: ColorStateList
    lateinit var  validColor: ColorStateList
    lateinit var available: ColorStateList

    var isEmailValid = false

    private lateinit var userViewModel: UserViewModel
    private lateinit var tokenManager: TokenManager

    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet:Boolean=false
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        networkManager = NetworkManager(this)           // initializing network manager

        checkForInternetConnection()

        //      initializing customErrorSnackBar
        customErrorSnackBar= CustomErrorSnackBar()

        tokenManager= TokenManager(this)
        userViewModel = ViewModelProvider(this,UserViewModelFactory(tokenManager))[UserViewModel::class.java]

        emailEditText=findViewById(R.id.joinEmailEditText)
        emailTextInputLayout=findViewById(R.id.joinEmailTextField)
        continueBtn=findViewById(R.id.continueBtn)
        emailEditText.addTextChangedListener(emailTextWatcher)
        signIn=findViewById(R.id.signInTextView)

        errorColor= ContextCompat.getColorStateList(this,R.color.red)!!
        validColor= ContextCompat.getColorStateList(this,R.color.text)!!
        available= ContextCompat.getColorStateList(this,R.color.isAvailable)!!

        signIn.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        continueBtn.setOnClickListener {
            if(isEmailValid){
                val intent=Intent(this,UserInfoActivity::class.java)
                intent.putExtra("email",emailEditText.text.toString())
                startActivity(intent)
                finish()

            }
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

        override fun afterTextChanged(s: Editable?) {
            val email = s.toString()

            if (email.isEmpty()) {
                isEmailValid = false
                emailTextInputLayout.helperText="Email is Empty"
            } else if (!isValidEmail(email)) {
                isEmailValid = false
                emailTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@RegisterActivity, R.color.red)
                emailTextInputLayout.hintTextColor=errorColor
                emailTextInputLayout.helperText= "Enter valid email"
                emailTextInputLayout.setHelperTextColor(errorColor)

            } else {
                emailTextInputLayout.helperText=null
                emailEditText.error = null
                emailTextInputLayout.hintTextColor=validColor
//                emailTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@RegisterActivity, R.color.isAvailable)
                emailTextInputLayout.setHelperTextColor(validColor)

                if(!isConnectedToInternet){
                    showCustomErrorSnackBar()
                }else{
                    isUserPresentWithThisEmil(emailEditText.text.toString())
                }


            }

        }
    }

    private fun isUserPresentWithThisEmil(email: String) {
        userViewModel.checkEmail(email,object : UsernameAvailabilityCallback {
            override fun onUsernameAvailable(isAvailable: Boolean) {
                if(!isAvailable){
                    emailTextInputLayout.setHelperTextColor(available)
                    emailTextInputLayout.boxStrokeColor= ContextCompat.getColor(this@RegisterActivity,R.color.isAvailable)
                    emailTextInputLayout.hintTextColor=available
                    isEmailValid=true
                }else{
                    emailTextInputLayout.helperText="Email is already used"
                    emailTextInputLayout.boxStrokeColor= ContextCompat.getColor(this@RegisterActivity,R.color.red)
                    emailTextInputLayout.setHelperTextColor(errorColor)
                    emailTextInputLayout.hintTextColor=errorColor
                    isEmailValid=false
                }
            }

            override fun onUsernameCheckError() {
                val rootView: View = findViewById(android.R.id.content)
                customErrorSnackBar.showSnackbar(this@RegisterActivity,rootView,"Something went wrong")
            }

        })
    }

    private fun showCustomErrorSnackBar(){

        val rootView: View = findViewById(android.R.id.content)
        customErrorSnackBar.showSnackbar(this, rootView, "No internet connection")

    }

    private fun checkForInternetConnection() {

        lifecycleScope.launch(Dispatchers.Main) { // Launch coroutine on Main dispatcher
            networkManager.getNetworkConnectivityFlow()
                .flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher
                .collect {   // Collect flow on Main dispatcher
                    // Update UI or perform actions based on network state
                    isConnectedToInternet = it.isConnected
                    if(!isConnectedToInternet){
                        showCustomErrorSnackBar()
                    }
                }

        }

    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

}