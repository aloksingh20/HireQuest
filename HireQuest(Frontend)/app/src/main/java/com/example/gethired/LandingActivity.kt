package com.example.gethired

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.gethired.Callback.LoginCallback
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.Token.LoginResponse
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.RegisterLoginViewModel
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.adapter.ImagePagerAdapter
import com.example.gethired.entities.GoogleSignInClass
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Random

class LandingActivity : AppCompatActivity() {

    private lateinit var logoImageView: ImageView
    private lateinit var viewPager: ViewPager
    private lateinit var loginButton: Button
    private lateinit var continueWithGoogleButton: Button
    private lateinit var signUpButton: Button
    private  lateinit var cardView:MaterialCardView
    private lateinit var tabLayout: TabLayout

    private lateinit  var sharedPref: SharedPreferences

    private lateinit var userViewModel: UserViewModel
    private lateinit var registerLoginViewModel: RegisterLoginViewModel
    private lateinit var tokenManager: TokenManager
    private var fcmToken: String?=""

    private val RC_SIGN_IN = 9001

    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet:Boolean=true


    private lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        networkManager = NetworkManager(this)           // initializing network manager
        checkForInternetConnection()



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("892359217503-edbgb1qmljgcqbeoova03n36lf01dcmv.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)







// Register intent filter for deep link
        val intentFilter = IntentFilter(Intent.ACTION_VIEW)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        intentFilter.addCategory(Intent.CATEGORY_BROWSABLE)
        intentFilter.addDataScheme("https")
        intentFilter.addDataAuthority("app.goo.gl", 8080.toString())
        registerReceiver(deepLinkReceiver, intentFilter)

//      initializing token-manager
        tokenManager= TokenManager(this@LandingActivity)

        userViewModel = ViewModelProvider(this,UserViewModelFactory(tokenManager))[UserViewModel::class.java]
        registerLoginViewModel= ViewModelProvider(this)[RegisterLoginViewModel::class.java]
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                // Token retrieval succeeded
                fcmToken = token ?: ""
                // Now you can proceed with your registration logic here
            }
            .addOnFailureListener { exception ->
                // Token retrieval failed
                Log.e("FCM", "Fetching FCM registration token failed", exception)
                // Handle the error if token retrieval fails
            }





        tabLayout = findViewById(R.id.tabLayout)
        logoImageView = findViewById(R.id.logoImageView)
        viewPager = findViewById(R.id.viewPager)
        loginButton = findViewById(R.id.loginButton)
        continueWithGoogleButton = findViewById(R.id.continueWithGoogleButton)
        signUpButton = findViewById(R.id.signUpButton)
        cardView=findViewById(R.id.container)

        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

        logoTransition()


        loginButton.setOnClickListener {
            val intent= Intent(this@LandingActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val intent= Intent(this@LandingActivity,RegisterActivity::class.java)
            startActivity(intent)
        }

        continueWithGoogleButton.setOnClickListener {
//            signInWithGoogle()
        }




        val savedUsername = sharedPref.getString("username", "")
        val savedPassword = sharedPref.getString("password", "")


        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            // Perform automatic login
            if (checkLoginCredentials(savedUsername, savedPassword)) {
                // Login successful
                val intent= Intent(this@LandingActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Invalid saved credentials, prompt for manual login
//                Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show()
            }
        }


        //      initializing customErrorSnackBar
        customErrorSnackBar= CustomErrorSnackBar()


    }

//    internet checking function
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
    private fun showCustomErrorSnackBar(){

        val rootView: View = findViewById(android.R.id.content)
        customErrorSnackBar.showSnackbar(this, rootView, "No internet connection")

    }

    private fun logoTransition(){
        val logoAnimator = ObjectAnimator.ofFloat(logoImageView, "translationY", 600f)
        val headingAnimationSet = AnimatorSet()
        headingAnimationSet.playTogether(logoAnimator)
        headingAnimationSet.duration = 0
        headingAnimationSet.start()
        Handler().postDelayed({ animateLayoutTransition() }, Random().nextInt(1001) + 2000.toLong())

    }
    private fun animateLayoutTransition() {
        // Move the heading (app name and logo) to the top
        val logoAnimator = ObjectAnimator.ofFloat(logoImageView, "translationY", 600f, 0f)
        val cardFadeInAnimator = ObjectAnimator.ofFloat(cardView, "alpha", 1f)
        cardFadeInAnimator.duration = 1000

        val headingAnimationSet = AnimatorSet()
        headingAnimationSet.playTogether(logoAnimator, cardFadeInAnimator)
        headingAnimationSet.duration = 500
        headingAnimationSet.start()

        // Create translation animations for the elements inside the card view
        val viewPagerAnimator = ObjectAnimator.ofFloat(viewPager, "translationY", 300f, 0f)
        val loginButtonAnimator = ObjectAnimator.ofFloat(loginButton, "translationY", 200f, 0f)
        val continueWithGoogleButtonAnimator = ObjectAnimator.ofFloat(continueWithGoogleButton, "translationY", 200f, 0f)
        val signUpButtonAnimator = ObjectAnimator.ofFloat(signUpButton, "translationY", 200f, 0f)

        // Create alpha animations for the elements inside the card view
        val viewPagerAlphaAnimator = ObjectAnimator.ofFloat(viewPager, "alpha", 0f, 1f)
        val loginButtonAlphaAnimator = ObjectAnimator.ofFloat(loginButton, "alpha", 0f, 1f)
        val continueWithGoogleButtonAlphaAnimator = ObjectAnimator.ofFloat(continueWithGoogleButton, "alpha", 0f, 1f)
        val signUpButtonAlphaAnimator = ObjectAnimator.ofFloat(signUpButton, "alpha", 0f, 1f)

        // Combine all animations into one AnimatorSet
        val combinedAnimatorSet = AnimatorSet()
        combinedAnimatorSet.playTogether(
            viewPagerAnimator, loginButtonAnimator,
            continueWithGoogleButtonAnimator, signUpButtonAnimator,
            viewPagerAlphaAnimator, loginButtonAlphaAnimator,
            continueWithGoogleButtonAlphaAnimator, signUpButtonAlphaAnimator
        )


        // Start the combined animation
        combinedAnimatorSet.start()
        viewPager.adapter=ImagePagerAdapter(this)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun checkLoginCredentials(username: String, password: String): Boolean {
        val saveUsername = sharedPref.getString("username", "") ?: ""
        val savePassword = sharedPref.getString("password", "") ?: ""

        return username == saveUsername && password == savePassword
    }
    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
//                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                val loginDto= account.id?.let { GoogleSignInClass(fcmToken.toString(), it) }

                if (loginDto != null) {
                    registerLoginViewModel.loginUserUsingGoogle(loginDto,object :LoginCallback{
                        override fun onResponseLogin(loginResponse: LoginResponse) {
                            tokenManager.saveToken(loginResponse.accessToken)
                            val token=tokenManager.getToken()
                            userViewModel.getUser(token!!, object : UpdateUserCallback {
                                override fun onUserUpdated(updatedUserDto: UserDto) {
                                    saveLoginCredentials(updatedUserDto)
                                    startActivity(Intent(this@LandingActivity,MainActivity::class.java))
                                    finish()
                                }

                                override fun onUpdateUserError() {
                                    tokenManager.clearToken()
                                    logout()
                                    Toast.makeText(this@LandingActivity,"Something Went Wrong",Toast.LENGTH_SHORT).show()

                                }

                            })
                        }

                        override fun onErrorLogin() {
                            logout()
                            Toast.makeText(this@LandingActivity,"Please create account first", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
                // Signed in successfully, handle the account
                // You can use account.email, account.displayName, etc.
            } catch (e: ApiException) {
                logout()
                // Sign in failed, handle the error

            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this@LandingActivity,LoginActivity::class.java)

        startActivity(intent)
        finish()
    }

    private fun navigateToUserProfile(userId: String?) {
        userViewModel.getUserInfo(userId!!,object :UpdateUserCallback{
            override fun onUserUpdated(updatedUserDto: UserDto) {

                val intent=Intent(this@LandingActivity,ProfileActivity::class.java)
                intent.putExtra("user", updatedUserDto)
                startActivity(intent)

            }

            override fun onUpdateUserError() {
                Toast.makeText(this@LandingActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun isLoggedIn(): Boolean {
        val user = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
        return user!!.username.isNotEmpty()
    }

    private fun savePendingUserId(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("pendingUserId", userId).apply()
    }

    private fun saveLoginCredentials(userDto:UserDto) {
        val gson = Gson()
        val userJson = gson.toJson(userDto)
        val editor = sharedPref.edit()
        editor.putString("user_details", userJson)
        editor.putString("username", userDto.username)
        editor.putString("password", "")
        editor.apply()
    }

    private val deepLinkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val userId = intent.getStringExtra("userId")
            if (userId != null) {
                // Open user profile
                if (isLoggedIn()) {
                    // User is logged in, navigate to view user profile

                    navigateToUserProfile(userId)

                } else {
                    // User is not logged in, prompt to log in
                    savePendingUserId(this@LandingActivity,userId)
                    navigateToLogin()

                }
            } else {
                // Handle error
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(deepLinkReceiver)
    }

    private fun logout() {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            mGoogleSignInClient.signOut().addOnCompleteListener(this
            ) { Toast.makeText(this@LandingActivity, "Signed Out", Toast.LENGTH_LONG).show() }


        }
    }

}
