package com.example.gethired.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.Chat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MessageAdapter(
    val messages: List<Chat>,
    private val currentUser: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val timer = Timer()
    private val updateTimestampsTask = object : TimerTask() {
        override fun run() {
            Handler(Looper.getMainLooper()).post {
                notifyDataSetChanged()
            }
        }
    }

    init {
        timer.scheduleAtFixedRate(updateTimestampsTask, 60 * 1000, 60 * 1000)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        timer.cancel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.SENDER -> MessageViewHolder(inflater.inflate(R.layout.sender_message_item, parent, false))
            else -> MessageViewHolder(inflater.inflate(R.layout.receiver_message_item, parent, false))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val chatTimeStamp = LocalDateTime.parse(message.timestamp.subSequence(0, 23), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))


        when (holder) {
            is MessageViewHolder -> {
                if(position==0){
                    holder.dateLabel.visibility=View.VISIBLE
                    holder.dateLabel.text=getMessageLabel(message.timestamp)
                }else if(isNewDay(message,messages[position-1]) ){
                    holder.dateLabel.visibility=View.VISIBLE
                    holder.dateLabel.text=getMessageLabel(message.timestamp)
                }else{
                    holder.dateLabel.visibility=View.GONE
                }
                holder.messageContent.text = message.content
                holder.messageTime.text = getTimeAgo(chatTimeStamp)
            }
        }
    }


    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {

        return if (messages[position].senderId.toString() == currentUser) {
            MessageType.SENDER
        } else {
            MessageType.RECEIVER
        }
    }



    private fun getTimeAgo(date: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(date, now)

        return when {
            duration.toMinutes() < 2 -> "just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            else -> date.format(DateTimeFormatter.ofPattern("hh:mm a"))
        }
    }

    private fun isNewDay(message: Chat, previousMessage: Chat): Boolean {
        return message.timestamp.subSequence(0, 10) != previousMessage.timestamp.subSequence(0, 10)
    }

    private fun getMessageLabel(timestamp: String): String {
        val chatTimeStamp = LocalDateTime.parse(timestamp.substring(0, 23), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        val now = LocalDateTime.now()
        val yesterday = now.minusDays(1)



        return when (chatTimeStamp.dayOfMonth) {
            now.dayOfMonth -> "Today"
            yesterday.dayOfMonth -> "Yesterday"
            else -> {

                val date = chatTimeStamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-")

                "${months[date[1].toInt()-1]} ${date[2]}, ${date[0]}"
            }
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageContent: TextView = itemView.findViewById(R.id.message_text)
        val messageTime: TextView = itemView.findViewById(R.id.message_timing)
        val dateLabel: TextView = itemView.findViewById(R.id.date_header)
    }

    private fun previousMessage(position: Int): Chat {
        return if (position > 0) messages[position - 1] else throw IndexOutOfBoundsException("No previous message")
    }

    private fun getNextMessagePos(currentPos: Int): Int {
        for (i in currentPos + 1 until messages.size) {
            if (isNewDay(messages[i], messages[i - 1])) {
                return i
            }
        }
        return -1
    }
    object MessageType {
        const val SENDER = 0
        const val RECEIVER = 1
    }

    private val months= listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
}
