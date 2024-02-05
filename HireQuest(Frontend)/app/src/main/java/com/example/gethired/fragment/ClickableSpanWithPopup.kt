package com.example.gethired.fragment

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.gethired.R

class ClickableSpanWithPopup(
    private val popupLayoutResId: Int,
    private val fragmentManager: FragmentManager
) : ClickableSpan() {

    override fun onClick(widget: View) {
//        when (popupLayoutResId) {
//            R.layout.user_agreement_popup -> {
//                // Handle action for popup layout one
//                val popupFragment = PopupBottomSheetFragment(popupLayoutResId)
//
//                popupFragment.show(fragmentManager, popupFragment.tag)
//            }
//            R.layout.privacy_and_policy_popup -> {
//                // Handle action for popup layout two
//                val popupFragment = AnotherPopupBottomSheetFragment(popupLayoutResId)
//                popupFragment.show(fragmentManager, popupFragment.tag)
//            }
//            // Add more cases for other popup layouts if needed
//            else -> {
//                // Handle default action or show an error message
//            }
        }
    }

//    override fun updateDrawState(ds: TextPaint) {
//        super.updateDrawState(ds)
//        ds.isUnderlineText = false
//    }
//}
