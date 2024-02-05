
package com.example.gethired

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.LoginCallback
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.Token.LoginResponse
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.RegisterLoginViewModel
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.entities.LoginDto
import com.example.gethired.entities.User
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.example.gethired.utils.FcmToken
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import io.jsonwebtoken.Claims
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val PREF_FILE_NAME = "login_preference"
class LoginActivity : AppCompatActivity() {
    private lateinit var usernameOrEmail:TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var signInBtn:MaterialButton
    private lateinit var forgotPassword:TextView
    private lateinit var loadingBar: LottieAnimationView
    private lateinit var helpTv:TextView
    private lateinit var joinBtn:TextView

    private lateinit var userViewModel:UserViewModel
    private lateinit var registerLoginViewModel: RegisterLoginViewModel
    //    shared preferences
    private lateinit  var sharedPref: SharedPreferences

    private lateinit var rootView: View
//    private var snackbar: Snackbar? = null
    private var fcmToken: String?=""


    private lateinit var tokenManager: TokenManager

    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tokenManager= TokenManager(this@LoginActivity)
        networkManager = NetworkManager(this@LoginActivity)           // initializing network manager

        checkForInternetConnection()

        //      initializing customErrorSnackBar
        customErrorSnackBar= CustomErrorSnackBar()

        userViewModel = ViewModelProvider(this, UserViewModelFactory(tokenManager))[UserViewModel::class.java]
        registerLoginViewModel=ViewModelProvider(this)[RegisterLoginViewModel::class.java]
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

        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)


        val usernameFromRegister=intent.getStringExtra("username")
        rootView= findViewById(android.R.id.content)

        loadingBar=findViewById(R.id.loading_animation)
        forgotPassword=findViewById(R.id.login_forgotPasswordTv)
        usernameOrEmail=findViewById(R.id.login_usernameEdt)
        password=findViewById(R.id.login_passwordEdt)
        signInBtn=findViewById(R.id.login_btn)

        joinBtn=findViewById(R.id.joinTextView)
        usernameOrEmail.setText(usernameFromRegister)


        // Check if user is already logged in
        val savedUsername = sharedPref.getString("username", "")
        val savedPassword = sharedPref.getString("password", "")


        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            // Perform automatic login
            if (checkLoginCredentials(savedUsername, savedPassword)) {
                // Login successful
                val intent= Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
                // Proceed to the next screen or perform necessary actions
            } else {
                // Invalid saved credentials, prompt for manual login
//                Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show()
            }
        }

        joinBtn.setOnClickListener {
            val intent=Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        forgotPassword.setOnClickListener {
            val intent=Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

// Inside your onCreate function or wherever you want to make the network call
        signInBtn.setOnClickListener {
            if(isConnectedToInternet){
                loadingBar.visibility = View.VISIBLE
                loadingBar.playAnimation()

                val username = usernameOrEmail.text.toString()
                val password = password.text.toString()

                registerLoginViewModel.loginUser(LoginDto(username, password, fcmToken!!,""), object : LoginCallback {
                    override fun onResponseLogin(loginResponse: LoginResponse) {
                        tokenManager.saveToken(loginResponse.accessToken)
                        val token = tokenManager.getToken()
                        userViewModel.getUser(token!!, object : UpdateUserCallback {
                            override fun onUserUpdated(updatedUserDto: UserDto) {
                                loadingBar.visibility = View.GONE
                                signInBtn.visibility = View.VISIBLE
                                saveLoginCredentials(updatedUserDto)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }

                            override fun onUpdateUserError() {
                                tokenManager.clearToken()
                                Toast.makeText(this@LoginActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                                loadingBar.visibility = View.GONE
                                signInBtn.visibility = View.VISIBLE
                            }
                        })
                    }

                    override fun onErrorLogin() {
                        loadingBar.visibility = View.GONE
                        signInBtn.visibility = View.VISIBLE
                        Toast.makeText(this@LoginActivity, "Error in login", Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                showCustomErrorSnackBar()
            }
        }

    }

    private fun navigateToUserProfile(userIdToView: String) {
        userViewModel.getUserInfo(userIdToView,object :UpdateUserCallback{
            override fun onUserUpdated(updatedUserDto: UserDto) {
                val intent=Intent(this@LoginActivity,ProfileActivity::class.java)
                intent.putExtra("user", updatedUserDto)
                startActivity(intent)
            }

            override fun onUpdateUserError() {
                Toast.makeText(this@LoginActivity,"Something Went Wrong",Toast.LENGTH_SHORT).show()

            }

        })
    }


    private fun checkLoginCredentials(username: String, password: String): Boolean {
        val saveUsername = sharedPref.getString("username", "") ?: ""
        val savePassword = sharedPref.getString("password", "") ?: ""

        return username == saveUsername && password == savePassword
    }

    private fun saveLoginCredentials(userDto:UserDto) {
        val gson = Gson()
        val userJson = gson.toJson(userDto)
        val editor = sharedPref.edit()
        editor.putString("user_details", userJson)
        editor.putString("username", usernameOrEmail.text.toString())
        editor.putString("password", password.text.toString()) // For additional usage, e.g., displaying username
        editor.apply()
    }

    private fun showCustomErrorSnackBar(){
        customErrorSnackBar.showSnackbar(this@LoginActivity, findViewById(android.R.id.content), "No internet connection")
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