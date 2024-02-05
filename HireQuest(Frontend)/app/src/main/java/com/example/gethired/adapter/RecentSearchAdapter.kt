package com.example.gethired.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.RecentSearch
import com.example.gethired.entities.UserProfile

class RecentSearchAdapter(private var data:List<RecentSearch>):RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    interface OnDeleteIconClickListener {
        fun onDeleteIconClick(position: Int)
    }

    private var deleteIconClickListener: OnDeleteIconClickListener? = null

    fun setOnDeleteIconClickListener(listener: OnDeleteIconClickListener) {
        deleteIconClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }
    class ViewHolder(itemView:View, deleteIconClickListener:OnDeleteIconClickListener?, mListener:OnItemClickListener?):RecyclerView.ViewHolder(itemView) {

        val text:TextView=itemView.findViewById(R.id.recent_search_text)
        val delete:ImageView=itemView.findViewById(R.id.recent_search_delete_icon)

        init {
            delete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    deleteIconClickListener?.onDeleteIconClick(position)

                }
            }
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recent_search_recyclerview_list_item,parent,false)
        return ViewHolder(view,deleteIconClickListener,mListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=data[position]
        holder.text.text=item.searchedText
    }

    fun updateList(list:List<RecentSearch>){
        data=list
        notifyDataSetChanged()
    }


}