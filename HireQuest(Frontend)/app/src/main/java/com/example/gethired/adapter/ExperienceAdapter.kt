import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.CommonFunction
import com.example.gethired.R
import com.example.gethired.entities.Experience

class ExperienceAdapter(private var data: List<Experience>,private val isSameUser: Boolean) :
    RecyclerView.Adapter<ExperienceAdapter.ViewHolder>() {

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }

    class ViewHolder(itemView: View, private val editIconClickListener: OnEditIconClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.experience_recyclerView_list_title)
        val organisation = itemView.findViewById<TextView>(R.id.experience_recyclerView_list_company)
        val duration = itemView.findViewById<TextView>(R.id.experience_recyclerView_list_duration)
        val description=itemView.findViewById<TextView>(R.id.experience_recyclerView_list_description)
        val edit_icon=itemView.findViewById<ImageView>(R.id.experience_recyclerView_list_edit_icon)
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
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.experience_recyclerview_list_layout, parent, false)
        return ViewHolder(itemView, editIconClickListener)
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
        holder.title.text = item.title
        holder.organisation.text =item.organisation

        if(item.description.isNotEmpty()){
            holder.description.visibility=View.VISIBLE

            holder.description.text=item.description
        }else{
            holder.description.visibility=View.GONE
        }


        val durationTemp = if(item.end=="present"){
            "${item.start} - present"
        }else{
            "${item.start} - ${item.end}"
        }
        holder.duration.text = durationTemp

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(experience: List<Experience>){
        data=experience
        notifyDataSetChanged()
    }
}
