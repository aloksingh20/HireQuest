package com.example.gethired.adapter.dynamicSpan

import androidx.recyclerview.widget.GridLayoutManager
import com.example.gethired.entities.Item

class DynamicSpanSizeLookupSkill(private val items: List<String>) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return when {
            items[position].length <= 15 -> 1
            items[position].length <= 20 -> 2
            else -> 3
        }
    }
}