package com.example.gethired.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.gethired.R

class PopupBottomSheetFragment(private val popupLayoutResId: Int) : BottomSheetDialogFragment() {

    lateinit var agreementPointsTextView:TextView
    lateinit var agreementWelcomeText:TextView
    lateinit var agreementBottomText:TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(popupLayoutResId, container, false)

        agreementWelcomeText=view.findViewById(R.id.userAgreementWelcomeText)
        agreementPointsTextView=view.findViewById(R.id.userAgreementPoints)
        agreementBottomText=view.findViewById(R.id.userAgreementBottomText)

        userAgreementWelcomeSpannable()
        userAgreementPointsSpannable()
        userAgreementBottomSpannable()

        return view
    }

    private fun userAgreementBottomSpannable() {
        val welcomeText=agreementBottomText.text
        val spannable=SpannableString(welcomeText)
        val startIndex=welcomeText.indexOf("HireQuest")
        val endIndex=startIndex+"HireQuest".length

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(),R.color.base_color)),
            startIndex,endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        agreementBottomText.text=spannable
    }

    private fun userAgreementWelcomeSpannable() {
        val welcomeText=agreementWelcomeText.text
        val spannable=SpannableString(welcomeText)
        val startIndex=welcomeText.indexOf("HireQuest")
        val endIndex=startIndex+"HireQuest".length

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(),R.color.base_color)),
            startIndex,endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        agreementWelcomeText.text=spannable
    }

    private fun userAgreementPointsSpannable() {
        val agreementText = getString(R.string.user_agreement_points)

        val spannable = SpannableString(agreementText)



        val highlightedWords = arrayOf(
            "1. Purpose and Acceptable Use:", "2. Account Responsibility:", "3. Prohibited Content:",
            "4. Professional Conduct:", "5. Liability and Conduct:" , "HireQuest"
        )
        val highlightColors = arrayOf(
            ContextCompat.getColor(requireContext(),R.color.heading),ContextCompat.getColor(requireContext(),R.color.heading),ContextCompat.getColor(requireContext(),R.color.heading),ContextCompat.getColor(requireContext(),R.color.heading),ContextCompat.getColor(requireContext(),R.color.heading),ContextCompat.getColor(requireContext(),R.color.base_color)
        )

        for (i in highlightedWords.indices) {
            val startIndex = agreementText.indexOf(highlightedWords[i])
            if (startIndex != -1) {
                val endIndex = startIndex + highlightedWords[i].length

                spannable.setSpan(
                    ForegroundColorSpan(highlightColors[i]),
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                if (highlightedWords[i] != "HireQuest") {
//                    spannable.setSpan(
//                        RelativeSizeSpan(highlightTextSizeFactor),
//                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        // Find and format all occurrences of "HireQuest"
        val hireQuest = "HireQuest"
        var hireQuestIndex = agreementText.indexOf(hireQuest)
        while (hireQuestIndex != -1) {
            spannable.setSpan(
                ForegroundColorSpan(highlightColors[5]),
                hireQuestIndex, hireQuestIndex + hireQuest.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            hireQuestIndex = agreementText.indexOf(hireQuest, hireQuestIndex + hireQuest.length)
        }

        agreementPointsTextView.text = spannable
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.peekHeight = (2f / 3f * resources.displayMetrics.heightPixels).toInt()
        behavior.isHideable = true
    }
}
