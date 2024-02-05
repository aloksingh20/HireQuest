package com.example.gethired.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.Profile

class ProfileAdapter(private var data:List<Profile>, private val isSameUser: Boolean):RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }
    class ViewHolder(itemView: View,private val editIconClickListener: OnEditIconClickListener?):RecyclerView.ViewHolder(itemView) {

        val name=itemView.findViewById<TextView>(R.id.profile_recyclerview_item_title)
        val link=itemView.findViewById<ImageView>(R.id.profile_recyclerview_item_url)
        val edit_icon=itemView.findViewById<ImageView>(R.id.profile_recyclerview_item_edit_icon)
        val date=itemView.findViewById<TextView>(R.id.profile_recyclerview_item_description)
        var urlLink: String = ""
        init {

            link.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlLink))
                itemView.context.startActivity(intent)
            }
            edit_icon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editIconClickListener?.onEditIconClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_recyclerview_item_layout, parent, false)
        return ViewHolder(itemView,editIconClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if(!isSameUser){
            holder.edit_icon.visibility=View.GONE
        }else{

            holder.edit_icon.visibility=View.VISIBLE
        }
        holder.name.text=item.handleName
        holder.urlLink=item.profileUrl
    }

    fun updateData(profile:List<Profile>){
        data=profile
        notifyDataSetChanged()
    }
}