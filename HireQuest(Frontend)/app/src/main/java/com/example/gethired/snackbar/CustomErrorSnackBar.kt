package com.example.gethired.snackbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.example.gethired.R
import com.google.android.material.snackbar.Snackbar

class CustomErrorSnackBar {
        fun showSnackbar(context: Context, view: android.view.View, message: String) {
            val customSnackbar = Snackbar.make(view, "", Snackbar.ANIMATION_MODE_SLIDE)
            val snackbarLayout = customSnackbar.view as Snackbar.SnackbarLayout

            // Inflate custom layout
            val inflater = LayoutInflater.from(context)
            val customLayout = inflater.inflate(R.layout.custom_error_snackbar, null)

            // Customize your layout components
//            val icon = customLayout.findViewById<ImageView>(R.id.customSnackbarIcon)
//            icon.setImageResource(R.drawable.ic_custom_icon)

            val messageTextView = customLayout.findViewById<TextView>(R.id.custom_error_snackbar_message)
            val dismissButton = customLayout.findViewById<TextView>(R.id.custom_error_snackbar_dismiss)
            messageTextView.text = message

            // Add custom layout to Snackbar
            snackbarLayout.addView(customLayout, 0)


            // Show the Snackbar
            customSnackbar.show()

            dismissButton.setOnClickListener {
                fadeOutDismiss(customSnackbar)
            }
        }
    private fun fadeOutDismiss(snackbar: Snackbar) {
        // Get the Snackbar view
        val snackbarView = snackbar.view

        // Start the fade-out animation using the animate() method
        snackbarView.animate()
            .alpha(0f) // Set the final alpha value
            .setDuration(300L) // Set the duration of the fade animation
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // Dismiss the Snackbar when the animation is complete
                    snackbar.dismiss()
                }
            })
    }

}
