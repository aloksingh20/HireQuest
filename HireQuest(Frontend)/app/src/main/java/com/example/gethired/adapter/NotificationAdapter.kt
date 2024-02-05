package com.example.gethired.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.Notification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationAdapter(private var data: List<Notification>, private val context: Context) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    private var clickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }

    class ViewHolder(itemView: View, private val editIconClickListener: OnEditIconClickListener?,private val clickListener: OnItemClickListener?):RecyclerView.ViewHolder(itemView) {

        private val editIcon: ImageView = itemView.findViewById(R.id.notification_setting_icon)
        val isOpen: ImageView = itemView.findViewById(R.id.notification_isOpen_indicator_circle)
        val notificationHeader: TextView = itemView.findViewById(R.id.notification_heading)
        val time:TextView = itemView.findViewById(R.id.notification_timing)

        init {
            editIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editIconClickListener?.onEditIconClick(position)
                }
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener?.onItemClick(position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item_list, parent, false)

        // Pass the longClickListener instance to the ViewHolder
        return ViewHolder(view, editIconClickListener,clickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item=data[position]
        holder.notificationHeader.text="${item.senderUsername} ${item.body}"
        if(item.readStatus){
            holder.isOpen.visibility=View.GONE
        }else{
            holder.isOpen.visibility=View.VISIBLE
        }


            val chatTimeStamp = LocalDateTime.parse(
                item.timestamp.substring(0, 23),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            )
            val hour = chatTimeStamp.hour
            val min = chatTimeStamp.minute
            val temp = if (hour >= 12) {

                if (min > 9) {
                    "${hour}:${min}PM"
                } else {
                    "${hour}:0${min}PM"
                }
            } else {
                if (min > 9) {
                    "${hour}:${min}AM"
                } else {
                    "${hour}:0${min}AM"
                }
            }
            holder.time.text = temp


        if(item.readStatus){
            holder.isOpen.visibility=View.GONE
        }else{
            holder.isOpen.visibility=View.VISIBLE
        }
    }

    fun updateData(notifications:List<Notification>){
        data=notifications
        notifyDataSetChanged()
    }
}