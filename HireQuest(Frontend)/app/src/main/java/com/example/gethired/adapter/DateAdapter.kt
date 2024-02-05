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

class DateAdapter(private val context: Context, private var months: List<String>) : RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    private var selectedItem = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text:TextView=itemView.findViewById(R.id.date_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_recyclerview_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val month = months[position]

        holder.text.text = month

        if (position == selectedItem) {
            // Customize the selected item
            holder.itemView.backgroundTintList= ContextCompat.getColorStateList(context,R.color.base_color)
            holder.itemView.background=ContextCompat.getDrawable(context,R.drawable.rounded_background_day)
            holder.itemView.alpha=1f
            holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        } else {
            // Reset other items to normal state
            holder.itemView.backgroundTintList=ContextCompat.getColorStateList(context,R.color.date_background_notSelected)
            holder.itemView.background=ContextCompat.getDrawable(context,R.drawable.rounded_background_day)
            holder.itemView.alpha=0.6f
            holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }

    }

    override fun getItemCount(): Int {
        return months.size
    }

    fun setSelectedItem(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }

    fun setItems(daysInMonth: List<String>) {
        months =daysInMonth
        notifyDataSetChanged()
    }
}

