package com.example.gethired.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R

class SearchSkillAdapter(var data:List<String>) : RecyclerView.Adapter<SearchSkillAdapter.SkillViewHolder>() {

    var onSkillItemClickListener: OnSkillItemClickListener? = null

    interface OnSkillItemClickListener {
        fun onSkillItemClick(skill: String)
    }


    class SkillViewHolder(itemView: View, private val onSkillItemClickListener: OnSkillItemClickListener):RecyclerView.ViewHolder(itemView) {
        val skillTextView: TextView = itemView.findViewById(R.id.skill_recyclerview_item_text)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val skill = skillTextView.text.toString()
                    onSkillItemClickListener.onSkillItemClick(skill)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.skill_recyclerview_item,parent,false)
        return SkillViewHolder(view,onSkillItemClickListener!!)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val item=data[position]
        holder.skillTextView.text=item
    }
    fun updateData(newData: List<String>) {
        data=newData
        notifyDataSetChanged()
    }

}