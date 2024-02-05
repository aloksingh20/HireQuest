import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.CommonFunction
import com.example.gethired.R
import com.example.gethired.entities.Project

class ProjectAdapter(private var data: List<Project>,private val isSameUser: Boolean) :
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    interface OnEditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    private var editIconClickListener: OnEditIconClickListener? = null

    fun setOnEditIconClickListener(listener: OnEditIconClickListener) {
        editIconClickListener = listener
    }

    @SuppressLint("SuspiciousIndentation")
    class ViewHolder(itemView: View, private val editIconClickListener: OnEditIconClickListener?) :
        RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.project_recyclerview_item_title)
        val url = itemView.findViewById<ImageView>(R.id.project_recyclerview_item_url)
        val duration = itemView.findViewById<TextView>(R.id.project_recyclerview_item_duration)
        val description = itemView.findViewById<TextView>(R.id.project_recyclerview_item_description)
        val edit_icon=itemView.findViewById<ImageView>(R.id.project_edit_icon)
        var urlLink: String = ""

        init {
            url.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlLink))
                itemView.context.startActivity(intent)
            }

            // Set the long-click listener for the item view
            edit_icon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editIconClickListener?.onEditIconClick(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_recyclerview_list_layout, parent, false)
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
        holder.urlLink = item.projectUrl

        holder.duration.text =
            (item.start) + " - " + (item.end)
        if (holder.urlLink.isEmpty()) {
            holder.url.visibility = View.GONE
        } else {
            holder.url.visibility = View.VISIBLE
        }

        if(item.description.isNotEmpty()){
            holder.description.visibility=View.VISIBLE
            holder.description.text=item.description
        }else{
            holder.description.visibility=View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(projectList:List<Project>){
        data=projectList
        notifyDataSetChanged()
    }
}
