package com.example.gethired.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import java.lang.Integer.min

class SkillAdapter(var data:List<String>, private var maxDisplayCount: Int):RecyclerView.Adapter<SkillAdapter.ViewHolder>() {

//    private val MAX_DISPLAY_COUNT = maxDisplayCount
//    private var isExpanded = false
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val name:TextView=itemView.findViewById(R.id.common_recyclerview_list_name)
        val moreText:TextView=itemView.findViewById(R.id.common_recyclerview_list_more_count)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.common_recyclerview_list__item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return min(maxDisplayCount,data.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < maxDisplayCount-1) {
            val item = data[position]
            holder.name.visibility=View.VISIBLE
            holder.name.text = item
            holder.moreText.visibility = View.GONE
        } else {
            holder.name.text = ""
            holder.name.visibility=View.GONE
            holder.moreText.visibility = View.VISIBLE
            holder.moreText.text = "+${data.size - maxDisplayCount} more"

        }
    }

    fun update(list: List<String>, maxDisplayCount: Int = 6) {
        data = list
        this.maxDisplayCount = maxDisplayCount
//        isExpanded = false
        notifyDataSetChanged()
    }
}