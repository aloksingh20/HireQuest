package com.example.gethired.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor

class FontInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result = chain.proceed(chain.request())

        if (result.view is TextView) {
            val textView = result.view as TextView
            textView.typeface = Typeface.createFromAsset(textView.context.assets, "Nunito-VariableFont_wght.ttf")
        }

        if (result.view is TextInputEditText) {
            val editText = result.view as TextInputEditText
            editText.typeface = Typeface.createFromAsset(editText.context.assets, "Nunito-VariableFont_wght.ttf")
        }

        return result
    }
}
