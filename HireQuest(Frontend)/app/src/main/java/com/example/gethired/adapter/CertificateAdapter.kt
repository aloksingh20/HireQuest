package com.example.gethired.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.CommonFunction
import com.example.gethired.R
import com.example.gethired.entities.Certificate

class CertificateAdapter(private var data: List<Certificate>,private val isSameUser: Boolean) :
    RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }

    class ViewHolder(itemView: View, private val editIconClickListener:OnEditIconClickListener?) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.certificate_recyclerview_item_title)
        val issuedBy = itemView.findViewById<TextView>(R.id.certificate_recyclerview_item_issuedBy)
        val duration = itemView.findViewById<TextView>(R.id.certificate_recyclerview_item_duration)
        val link=itemView.findViewById<ImageView>(R.id.certificate_recyclerview_item_link_icon)
        val edit_icon=itemView.findViewById<ImageView>(R.id.certificate_recyclerview_item_edit_icon)
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.certificate_recyclerview_list_layout, parent, false)

        // Pass the longClickListener instance to the ViewHolder
        return ViewHolder(view, editIconClickListener)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if(!isSameUser){
            holder.edit_icon.visibility=View.GONE
        }else{

            holder.edit_icon.visibility=View.VISIBLE
        }
        holder.title.text = item.certificateTitle
        holder.issuedBy.text = item.issuedBy
        holder.duration.text = (item.start) + " - " + (item.end)

        holder.url=item.certificateUrl
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(certificate:List<Certificate>){
        data=certificate
        notifyDataSetChanged()
    }
}
