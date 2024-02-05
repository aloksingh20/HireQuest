package com.example.gethired.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R

class FilterAdapter(val data: List<String> ,private val context: Context) : RecyclerView.Adapter<FilterAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }



    class MyViewHolder(itemView: View,mListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.filter_list_layout_text)

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.filter_list_layout, parent, false)
//        view.layoutParams = ViewGroup.LayoutParams(width, height)
        return MyViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item
    }

    override fun getItemCount(): Int = data.size
}