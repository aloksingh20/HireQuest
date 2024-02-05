package com.example.gethired.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R

class TimeAdapter(private val context: Context, private val time: List<String>) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.date_item)
    }

    private var selectedItem = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_recyclerview_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val month = time[position]

        holder.text.text = month

        if (position == selectedItem) {
            // Customize the selected item
            holder.itemView.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.base_color)
            holder.itemView.background =
                ContextCompat.getDrawable(context, R.drawable.rounded_background_day)
            holder.itemView.alpha = 1f
            holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        } else {
            // Reset other items to normal state
            holder.itemView.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.date_background_notSelected)
            holder.itemView.background =
                ContextCompat.getDrawable(context, R.drawable.rounded_background_day)
            holder.itemView.alpha = 0.6f
            holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }

    }

    override fun getItemCount(): Int {
        return time.size
    }

    fun setSelectedItem(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }
}