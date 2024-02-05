package com.example.gethired.adapter.dynamicSpan

import androidx.recyclerview.widget.GridLayoutManager
import com.example.gethired.entities.Item

class DynamicSpanSizeLookup(private val items: List<Item>) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return when {
            items[position].text.length <= 12 -> 1
            items[position].text.length <= 20 -> 2
            else -> 3
        }
    }
}