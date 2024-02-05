package com.example.gethired

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.UpdateUserCallback
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.entities.User
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.fragment.UserProfileFragment


class ProfileActivity : AppCompatActivity() {

    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var userProfileFragment: FrameLayout
//    private lateinit var userViewModel: UserViewModel
//    private lateinit var tokenManager: TokenManager
    private  var currentUser:UserDto?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        currentUser=CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
        val searchedUser = intent.getSerializableExtra("user") as? UserDto
        val searchedUserId = intent.getLongExtra("userId",currentUser!!.id)


        loadingAnimation=findViewById(R.id.loading_animation)
        userProfileFragment=findViewById(R.id.user_profile_fragment)

        loadingAnimation.visibility= View.GONE
        userProfileFragment.visibility=View.VISIBLE


        val bundle = Bundle()
        bundle.putSerializable("userInfo", searchedUser)
        bundle.putLong("userId",searchedUserId)
        val fragment = UserProfileFragment()
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.user_profile_fragment, fragment)
        fragmentTransaction.commit()



        }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to the previous activity
        finish()
    }

    private fun convertUserToUserDto(user: User): UserDto {
        return UserDto(
            id = user.id,
            birthdate = user.birthdate,
            currentOccupation = user.currentOccupation,
            email = user.email,
            headline = user.headline,
            name = user.name,
            phone = user.phone,
            status = user.status,
            username = user.username,
            isRecuriter = user.isRecuriter, // Set this value based on your logic
            gender = null // Set this value based on your logic
        )
    }
    // Function to store the pending user ID
//    private fun moveToUserProfileFragment(userDto: UserDto) {
//
//            val bundle = Bundle()
//            bundle.putSerializable("userInfo", userDto)
//            val fragment = UserProfileFragment()
//            fragment.arguments = bundle
//            val fragmentManager = supportFragmentManager
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.user_profile_fragment, fragment)
//            fragmentTransaction.commit()
//
//    }



}