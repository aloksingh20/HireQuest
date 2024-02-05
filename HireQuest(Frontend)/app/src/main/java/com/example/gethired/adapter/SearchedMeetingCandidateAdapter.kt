package com.example.gethired.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.UserProfile
import java.util.*


class SearchedMeetingCandidateAdapter(private var data:List<UserProfile>):RecyclerView.Adapter<SearchedMeetingCandidateAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class ViewHolder(itemView: View, mListener: OnItemClickListener?):RecyclerView.ViewHolder(itemView) {

        val name:TextView=itemView.findViewById(R.id.meeting_candidate_search_profile_user_name)
        val img:ImageView=itemView.findViewById(R.id.meeting_candidate_search_profile_user_img)

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.meeting_candidate_search_profile,parent,false)
        return ViewHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=data[position]
        holder.name.text=item.name
//        val imageData: ByteArray = Base64.decode(item.user.image.data, Base64.DEFAULT)
//        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
//        holder.img.setImageBitmap(bitmap)

    }
}