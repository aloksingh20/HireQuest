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
import com.example.gethired.entities.Education

class EducationAdapter(private var data: List<Education>,private val isSameUser: Boolean) : RecyclerView.Adapter<EducationAdapter.ViewHolder>() {

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }

    class ViewHolder(itemView: View, private val editIconClickListener: OnEditIconClickListener?) : RecyclerView.ViewHolder(itemView) {
        val degree: TextView = itemView.findViewById(R.id.education_recyclerview_list_degree)
        val institute: TextView = itemView.findViewById(R.id.education_recyclerview_list_institute_name)
        val duration: TextView = itemView.findViewById(R.id.education_recyclerview_list_duration)
        val edit_icon:ImageView=itemView.findViewById(R.id.education_recyclerview_list_edit_icon)

        init {
            // ...

            edit_icon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editIconClickListener?.onEditIconClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.education_recyclerview_list_item, parent, false)
        return ViewHolder(itemView, editIconClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        if (!isSameUser){
            holder.edit_icon.visibility=View.GONE
        }else{
            holder.edit_icon.visibility=View.VISIBLE
        }
        holder.degree.text = item.levelOfEducation
        holder.institute.text = item.instituteName

        val durationTemp = if(item.end=="present"){
            "${item.start} - present"
        }else{
            "${item.start} - ${item.end}"
        }
        holder.duration.text = durationTemp

    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(educationList: MutableList<Education>) {
        data = educationList
        notifyDataSetChanged()
    }

}
