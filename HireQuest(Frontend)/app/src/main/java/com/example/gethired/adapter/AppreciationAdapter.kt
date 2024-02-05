package com.example.gethired.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.Appreciation
import com.example.gethired.entities.Education

class AppreciationAdapter(var data:List<Appreciation>, private val isSameUser: Boolean): RecyclerView.Adapter<AppreciationAdapter.ViewHolder>() {

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }

    class ViewHolder(itemView: View, private val editIconClickListener: OnEditIconClickListener?):RecyclerView.ViewHolder(itemView) {
        val title: TextView =itemView.findViewById(R.id.appreciation_recyclerview_item_title)
        val link:ImageView=itemView.findViewById(R.id.appreciation_recyclerview_item_link_icon)
        val issuedBy:TextView=itemView.findViewById(R.id.appreciation_recyclerview_item_issuedBy)
        val date:TextView=itemView.findViewById(R.id.appreciation_recyclerview_item_duration)
        val edit_icon:ImageView=itemView.findViewById(R.id.appreciation_recyclerview_item_edit_icon)
        var url:String=""

        init {
            edit_icon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editIconClickListener?.onEditIconClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.appreciation_recyclerview_list_layout,parent,false)
        return ViewHolder(view,editIconClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=data[position]
        if(!isSameUser){
            holder.edit_icon.visibility=View.GONE
        }else{

            holder.edit_icon.visibility=View.VISIBLE
        }
        holder.title.text=item.appreciationTitle
        holder.issuedBy.text=item.issuedBy
        holder.url=holder.url
        holder.date.text=item.start+" - "+item.end
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(appreciationList: MutableList<Appreciation>) {
        data = appreciationList
        notifyDataSetChanged()
    }
}