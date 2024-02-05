package com.example.gethired.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.R
import com.example.gethired.entities.Pdf

class ResumeAdapter(private var data: List<Pdf>, private val isSameUser: Boolean):RecyclerView.Adapter<ResumeAdapter.ViewHolder>() {

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }
    interface OnDownloadIconClickListener {
        fun onDownloadIconClick(position: Int)
    }

    private var downloadIconClickListener: OnDownloadIconClickListener? = null

    fun setOnDownloadIconClickListener(listener: OnDownloadIconClickListener) {
        downloadIconClickListener = listener
    }

    class ViewHolder(itemView: View, private val editIconClickListener: OnEditIconClickListener?,private val downloadIconClickListener: OnDownloadIconClickListener?):RecyclerView.ViewHolder(itemView) {

        val deleteIcon :ImageView=itemView.findViewById(R.id.resume_recyclerview_list_item_delete_icon)
        val fileName:TextView = itemView.findViewById(R.id.resume_recyclerview_list_item_file_name)
        val fileSize:TextView = itemView.findViewById(R.id.resume_recyclerview_list_item_file_size)
        val timeStamp:TextView = itemView.findViewById(R.id.resume_recyclerview_list_item_timeStamp)
        val downloadIcon:ImageView=itemView.findViewById(R.id.resume_recyclerview_list_item_download_icon)
        val downloadProgressBar:LottieAnimationView=itemView.findViewById(R.id.resume_recyclerview_list_item_download_loading_bar)

        init {

            deleteIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editIconClickListener?.onEditIconClick(position)
                }
            }
            downloadIcon.setOnClickListener {
                val position=adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    downloadIconClickListener?.onDownloadIconClick(position)
                    downloadProgressBar.visibility=View.VISIBLE
                    downloadIcon.visibility=View.GONE
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_recyclerview_list_item, parent, false)
        return ViewHolder(itemView, editIconClickListener,downloadIconClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.fileName.text=item.fileName
        holder.fileSize.text=item.fileSize
        holder.timeStamp.text=item.timeStamp
        if(!isSameUser){
            holder.deleteIcon.visibility=View.GONE
            holder.downloadIcon.visibility=View.VISIBLE
            holder.downloadProgressBar.visibility=View.GONE

        }else{
            holder.deleteIcon.visibility=View.VISIBLE
            holder.downloadIcon.visibility=View.GONE
            holder.downloadProgressBar.visibility=View.GONE

        }
    }

    fun updateData( newData:List<Pdf>){



        data=newData
        notifyDataSetChanged()
        Log.d("notify-resume","${data.size}")

    }
}