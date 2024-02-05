package com.example.gethired

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.RegisterUserCallback
import com.example.gethired.Callback.UsernameAvailabilityCallback
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.entities.RegisterDto
import com.example.gethired.entities.RegistrationResponse
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.example.gethired.utils.FcmToken
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class UserInfoActivity : AppCompatActivity() {

    lateinit var joinBtn:MaterialButton
    lateinit var usernameInputTextLayout: TextInputLayout
    lateinit var usernameEditText: TextInputEditText
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    lateinit var password:TextInputEditText
    lateinit var passwordTextInputLayout:TextInputLayout
    private lateinit var confirmPassword:TextInputEditText
    lateinit var confirmPasswordTextInputLayout: TextInputLayout
    lateinit var loadingAnimation:LottieAnimationView

    lateinit var  errorColor: ColorStateList
    lateinit var  validColor: ColorStateList
    lateinit var available: ColorStateList

    var isUserNameValid=false
    var isPasswordValid = false

    private lateinit var userViewModel: UserViewModel
    private lateinit var tokenManager: TokenManager

    private lateinit var fcmToken: String

//    network
    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)


//        getting fcm-token
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                // Token retrieval succeeded
                fcmToken=token
                // Now you can proceed with your registration logic here
            }
            .addOnFailureListener { exception ->
                // Token retrieval failed
                Log.e("FCM", "Fetching FCM registration token failed", exception)
                // Handle the error if token retrieval fails
            }

        tokenManager= TokenManager(this)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(tokenManager))[UserViewModel::class.java]

        networkManager = NetworkManager(this@UserInfoActivity)           // initializing network manager

        checkForInternetConnection()

        //      initializing customErrorSnackBar
        customErrorSnackBar= CustomErrorSnackBar()

//        fetching input field

        joinBtn=findViewById(R.id.joinBtn)
        usernameEditText=findViewById(R.id.join_username)
        usernameInputTextLayout=findViewById(R.id.join_username_heading)
        firstNameEditText=findViewById(R.id.join_first_name)
        lastNameEditText=findViewById(R.id.join_last_name)
        password=findViewById(R.id.join_password)
        passwordTextInputLayout=findViewById(R.id.join_password_heading)
        confirmPassword=findViewById(R.id.join_confirm_password)
        confirmPasswordTextInputLayout=findViewById(R.id.join_confirm_password_heading)
        loadingAnimation=findViewById(R.id.loading_animation)

        errorColor= ContextCompat.getColorStateList(this,R.color.red)!!
        validColor= ContextCompat.getColorStateList(this,R.color.text)!!
        available= ContextCompat.getColorStateList(this,R.color.isAvailable)!!



        usernameEditText.addTextChangedListener(usernameTextWatcher)
        password.addTextChangedListener(passwordTextWatcher)
        confirmPassword.addTextChangedListener(confirmPasswordTextWatcher)

        joinBtn.setOnClickListener {
            loadingAnimation.visibility= View.VISIBLE
            joinBtn.visibility=View.GONE
            val name=firstNameEditText.text.toString()+" "+lastNameEditText.text.toString()
            if(isUserNameValid&& name.isNotEmpty()&&isPasswordValid&&password.text.toString()==confirmPassword.text.toString()){

                val newIntent = intent
                val email=newIntent.getStringExtra("email")
                val user= RegisterDto(email.toString(),name,usernameEditText.text.toString(),password.text.toString())
                if(isConnectedToInternet){
                    userViewModel.createUser(user, object : RegisterUserCallback {
                        override fun onUserRegistrationResponse(registerUserResponse: RegistrationResponse) {
                            val intent = Intent(this@UserInfoActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("username",usernameEditText.text.toString())
                            startActivity(intent)
                            finish()

                            Toast.makeText(this@UserInfoActivity,"User Registered",Toast.LENGTH_SHORT).show()
                        }

                        override fun onUserRegistrationError(errorCode:Int) {
                            loadingAnimation.visibility= View.GONE
                            joinBtn.visibility=View.VISIBLE
                            Toast.makeText(this@UserInfoActivity,"Registration failed",Toast.LENGTH_SHORT).show()
                        }
                    })
                }else{
                    showCustomErrorSnackBar()
                }
            }else{
                loadingAnimation.visibility= View.GONE
                joinBtn.visibility=View.VISIBLE
                Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val usernameTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val username=s.toString()
            if(username.isEmpty()){
                isUserNameValid=false
                usernameInputTextLayout.helperText="Username can not be Empty"
                usernameInputTextLayout.setHelperTextColor(errorColor)
                usernameInputTextLayout.hintTextColor=errorColor
                usernameInputTextLayout.boxStrokeColor= ContextCompat.getColor(this@UserInfoActivity, R.color.red)
            }else {
                if(isConnectedToInternet){
                    userViewModel.checkUserName(username,object : UsernameAvailabilityCallback {
                        override fun onUsernameAvailable(isAvailable: Boolean) {

                            if(!isAvailable){
                                usernameInputTextLayout.helperText="Username is available"
                                usernameInputTextLayout.setHelperTextColor(available)
                                usernameInputTextLayout.hintTextColor=available
                                usernameInputTextLayout.boxStrokeColor= ContextCompat.getColor(this@UserInfoActivity, R.color.isAvailable)
                                isUserNameValid=true
                            }else{
                                isUserNameValid=false
                                usernameInputTextLayout.helperText="Username is already taken"
                                usernameInputTextLayout.setHelperTextColor(errorColor)
                                usernameInputTextLayout.hintTextColor=errorColor
                                usernameInputTextLayout.boxStrokeColor= ContextCompat.getColor(this@UserInfoActivity, R.color.red)
                            }
                        }

                        override fun onUsernameCheckError() {
                            isUserNameValid=false
                        }

                    })
                }else{
                    showCustomErrorSnackBar()
                }
            }

        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    private val passwordTextWatcher =object :TextWatcher {
        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val password = s.toString()

            if (password.isEmpty()) {
                isPasswordValid=false
                passwordTextInputLayout.helperText="Password is Empty"
                passwordTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@UserInfoActivity, R.color.red)
            } else if(password.length<6){
                isPasswordValid=false
                passwordTextInputLayout.helperText="length 6 or more"
                passwordTextInputLayout.setHelperTextColor(errorColor)
                passwordTextInputLayout.hintTextColor = errorColor
                passwordTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@UserInfoActivity, R.color.red)
            }
            else{
                isPasswordValid=true
                passwordTextInputLayout.helperText=null
                passwordTextInputLayout.setHelperTextColor(validColor)
                passwordTextInputLayout.hintTextColor = available
                passwordTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@UserInfoActivity, R.color.isAvailable)
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

            if (confirmPassword != password.text.toString()) {
                confirmPasswordTextInputLayout.helperText="Password does not matches"
                confirmPasswordTextInputLayout.hintTextColor=errorColor
                confirmPasswordTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@UserInfoActivity, R.color.red)
            }
            else{
                confirmPasswordTextInputLayout.helperText="Password matches"
                confirmPasswordTextInputLayout.setHelperTextColor(available)
                confirmPasswordTextInputLayout.hintTextColor = available
                confirmPasswordTextInputLayout.boxStrokeColor=ContextCompat.getColor(this@UserInfoActivity, R.color.isAvailable)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun showCustomErrorSnackBar(){

        customErrorSnackBar.showSnackbar(this@UserInfoActivity, findViewById(android.R.id.content), "No internet connection")

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

}