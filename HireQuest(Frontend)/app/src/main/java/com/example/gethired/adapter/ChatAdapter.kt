package com.example.gethired.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.Certificate
import com.example.gethired.entities.ChatRoom
import com.google.android.material.imageview.ShapeableImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatAdapter(
    private var data: List<ChatRoom>,
    private val context: Context
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    private var longClickListener: OnItemLongClickListener? = null
    private var clickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        longClickListener = listener
    }

    class ViewHolder(
        itemView: View,
        private val longClickListener: OnItemLongClickListener?,
        private val clickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(itemView) {

        val name: TextView =itemView.findViewById(R.id.user_name)
        val profileImg: ShapeableImageView =itemView.findViewById(R.id.user_profile_img)
        val recentMessage: TextView =itemView.findViewById(R.id.user_recent_message)
        val unseenMessageCount: TextView =itemView.findViewById(R.id.unseen_message_count)
        val time: TextView =itemView.findViewById(R.id.recent_message_time)

        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    longClickListener?.onItemLongClick(itemView, position)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
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
            .inflate(R.layout.chat_item_list, parent, false)

        return ViewHolder(view, longClickListener, clickListener)
    }

    override fun getItemCount(): Int {
        return data.size // Use the actual size of your data
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.name.text=item.receiver.name
        holder.recentMessage.text=item.lastMessage.content
        if(item.unseenMessageCount>0){
            holder.unseenMessageCount.visibility=View.VISIBLE
            holder.unseenMessageCount.text=item.unseenMessageCount.toString()
        }else{
            holder.unseenMessageCount.visibility=View.GONE
        }

        val imageBytes = java.util.Base64.getDecoder().decode(item.image)

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.profileImg.setImageBitmap(bitmap)

        if(item.timeStamp.length!=23){

        }else {
            val chatTimeStamp = LocalDateTime.parse(
                item.timeStamp.substring(0, 23),
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
        }

    }

    fun update(list:List<ChatRoom>){
        data=list
        notifyDataSetChanged()
    }
}
