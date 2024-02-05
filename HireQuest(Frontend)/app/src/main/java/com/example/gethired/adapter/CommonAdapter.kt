package com.example.gethired.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R

class CommonAdapter(var data:MutableList<String>) : RecyclerView.Adapter<CommonAdapter.SkillViewHolder>() {


    class SkillViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val skillTextView: TextView = itemView.findViewById(R.id.language_recyclerview_list_name)
        val deleteItem:ImageView=itemView.findViewById(R.id.delete_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.language_recyclerview_list_layout,parent,false)
        return SkillViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val item=data[position]
        holder.skillTextView.text=item

        holder.deleteItem.visibility = View.VISIBLE
        holder.deleteItem.setOnClickListener {
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun updateData(newData: MutableList<String>) {
        data=newData
        notifyDataSetChanged()
    }
    fun getItemText(position: Int): String {
        return data[position]
    }
}