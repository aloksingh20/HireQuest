package com.example.gethired.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.gethired.R

class ImagePagerAdapter(private val context: Context) : PagerAdapter() {

    private val imageIds = arrayOf(
        R.drawable.slider1, // Replace with your image resource IDs
        R.drawable.slider2,
        R.drawable.slider3
    )

    override fun getCount(): Int {
        return imageIds.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.setImageResource(imageIds[position])
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
