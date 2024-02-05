package com.example.gethired.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.PREF_FILE_NAME
import com.example.gethired.R
import com.example.gethired.entities.Meeting
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.lang.Math.abs
import java.time.LocalDate
import java.time.LocalTime

class MeetingAdapter(private var data: List<Meeting>, private val context: Context,private val sharedPreferences: SharedPreferences, private val isHr:Int?) :
    RecyclerView.Adapter<MeetingAdapter.ViewHolder>()  {


    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    private var longClickListener: OnItemLongClickListener? = null

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        longClickListener = listener
    }


    interface OnScheduleMeetingClickListener{
        fun onScheduleMeetingClick(view:View, position: Int)
    }
    private var onScheduleMeetingClickListener: OnScheduleMeetingClickListener?=null

    fun setOnScheduleMeetingClickListener(listener: OnScheduleMeetingClickListener){
        onScheduleMeetingClickListener = listener
    }


    class ViewHolder (itemView: View,private val longClickListener: OnItemLongClickListener?, onScheduleMeetingClickListener: OnScheduleMeetingClickListener?):RecyclerView.ViewHolder(itemView){
         val date=itemView.findViewById<TextView>(R.id.meeting_date)
         val time=itemView.findViewById<TextView>(R.id.meeting_time)

         val joinBtn=itemView.findViewById<FloatingActionButton>(R.id.meeting_join_button)
         var url=""
         var userProfileImage = itemView.findViewById<ImageView>(R.id.meetingWith_user_profile_image)
         var userName = itemView.findViewById<TextView>(R.id.meetingWith_user_name)
         var timeLeft = itemView.findViewById<TextView>(R.id.meeting_time_left)

        init {

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    longClickListener?.onItemLongClick(itemView, position)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }

            userProfileImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onScheduleMeetingClickListener?.onScheduleMeetingClick(itemView, position)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meeting_list_layout, parent, false)

        return ViewHolder(view,longClickListener,onScheduleMeetingClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=data[position]

        holder.date.text=item.date
        holder.time.text=item.time
        holder.url=item.link
        if(isHr==1){
            holder.userName.text=item.user
        }else{
            holder.userName.text=item.hr
        }

        if(item.isAttended){
            holder.timeLeft.text="attended"
            holder.joinBtn.setBackgroundColor(context.resources.getColor(R.color.completed_background))
        }
        else{
            val date=LocalDate.now()
            val arr=item.date.split("-")
            val year = arr[2].toInt()
            val month = arr[1].toInt()
            val day = arr[0].toInt()
            val itemDate=LocalDate.of(year,month,day)

            val currentTime = LocalTime.now()
            val time = item.time.split(":")
            val hour = time[0].toInt()
            val min = time[1].toInt()
            val itemTime=LocalTime.of(hour,min)

            if(date>itemDate){
                holder.timeLeft.text="missed"
                holder.joinBtn.visibility=View.GONE

//                holder.joinBtn.rippleColor=null
//                holder.joinBtn.text="Missed"
//                holder.joinBtn.icon=null
//                holder.joinBtn.setTextColor(context.resources.getColor(R.color.not_joined))
//                holder.joinBtn.setBackgroundColor(context.resources.getColor(R.color.not_joined_background))
            }
            else if(date==itemDate&&currentTime>itemTime){
                holder.timeLeft.text="missed"
                holder.joinBtn.visibility=View.GONE

//                holder.joinBtn.rippleColor=null
//                holder.joinBtn.text="Missed"
//                holder.joinBtn.icon=null
//                holder.joinBtn.setTextColor(context.resources.getColor(R.color.not_joined))
//                holder.joinBtn.setBackgroundColor(context.resources.getColor(R.color.not_joined_background))

            }
                else
            {
                holder.joinBtn.visibility=View.VISIBLE

                holder.timeLeft.text="starts in ${(itemDate.dayOfYear-date.dayOfYear)*24+itemTime.hour-currentTime.hour}h ${abs(itemTime.minute - currentTime.minute)}m"
//                holder.joinBtn.text="Click Here"
//                holder.joinBtn.setIconResource(R.drawable.icon_url)
//                holder.joinBtn.setIconTintResource(R.color.base_color)
//                holder.joinBtn.setTextColor(context.resources.getColor(R.color.base_color))
                holder.joinBtn.setBackgroundColor(context.resources.getColor(R.color.join_background_color))
            }
        }
//        val isReminderScheduled = sharedPreferences.getBoolean("${ item.id }-${usename}",false)
//        if(isReminderScheduled){
//            holder.reminderBtn.setImageResource(R.drawable.icon_off_notification)
//        }else{
//            holder.reminderBtn.setImageResource(R.drawable.icon_notification)
//        }
        if(holder.url.isNotEmpty()){
            holder.joinBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(holder.url))
                startActivity(holder.itemView.context,intent,null)
            }
        }

    }

    fun update(meetingList: List<Meeting>) {
        data=meetingList
        notifyDataSetChanged()
    }

}