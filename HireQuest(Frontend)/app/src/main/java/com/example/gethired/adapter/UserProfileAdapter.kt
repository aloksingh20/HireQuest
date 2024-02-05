package com.example.gethired.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gethired.R
import com.example.gethired.entities.UserProfile
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import java.util.*

class UserProfileAdapter(
    private var userProfileList: MutableList<UserProfile>,
    private val isRecruiter: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Define view types for items and loading indicator
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    // Flag to track loading state
    private var isLoading = false

    private lateinit var onLoadMoreListener: OnLoadMoreListener
    // Interface for load more callback
    interface OnLoadMoreListener {
        fun onLoadMore()
    }
    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        this.onLoadMoreListener = listener
    }

    // Interface for item click callback
    interface OnItemClickListener {
        fun onItemClick(position: Int, userProfile: UserProfile)
    }

    // Variable to hold item click listener
    private var mListener: OnItemClickListener? = null

    // Function to set item click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    // ViewHolder class for item data
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context: Context = itemView.context
        val userProfileImg = itemView.findViewById<ShapeableImageView>(R.id.search_user_profile_picture)
        val name = itemView.findViewById<TextView>(R.id.search_user_name)
        val headline = itemView.findViewById<TextView>(R.id.search_user_headline)
        val currentOccupation = itemView.findViewById<TextView>(R.id.search_user_current_occupation)
        val sendMessage = itemView.findViewById<MaterialButton>(R.id.search_user_send_message)
        val bookMark = itemView.findViewById<ImageView>(R.id.search_user_bookmark)

        init {
            // Set click listener for each item
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition, userProfileList[adapterPosition])
            }
        }
    }

    // ViewHolder class for loading indicator
    inner class LoadingViewHolder(itemView: View,private val parentRecyclerView: RecyclerView) : RecyclerView.ViewHolder(itemView) {

        init {
            // Attach scroll listener to the parent RecyclerView
            (parentRecyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // Get the layout manager
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                    // Get the last visible position
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    // Check if loading is not already in progress and if we've reached the end of the list
                    if (isLoading && lastVisiblePosition >= userProfileList.size-1) {
                        // Notify the load more listener to fetch more data
                        onLoadMoreListener.onLoadMore()
                    }
                }
            })
        }
    }

    // Override `onCreateViewHolder` to create the appropriate ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                // Create a ViewHolder for an item
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_fragment_recyclerview_item_list, parent, false)
                ViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                // Create a ViewHolder for the loading indicator
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_recyalerview_shimmer_layout, parent, false)
                LoadingViewHolder(view,parent as RecyclerView)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    // Override `getItemViewType` to determine the view type for each item
    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == userProfileList.size) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return userProfileList.size + if (isLoading) 1 else 0
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val userProfile = userProfileList[position]

            holder.currentOccupation.text = userProfile.currentOccupation
            holder.headline.text = userProfile.headline
            holder.name.text = userProfile.name

            if(!isRecruiter){
                holder.sendMessage.visibility=View.GONE
                holder.bookMark.visibility=View.GONE
            }else{
                holder.sendMessage.visibility=View.VISIBLE
                holder.bookMark.visibility=View.VISIBLE
            }
            val imageBytes = Base64.getDecoder().decode(userProfile.imageData)

            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.userProfileImg.setImageBitmap(bitmap)

        }
    }

    fun updateList(newProfileList: List<UserProfile>, isNewSearch:Boolean) {
        if(isNewSearch){
            userProfileList.clear()
            userProfileList.addAll(newProfileList)
        }else{
            userProfileList.addAll(newProfileList)
        }

        notifyDataSetChanged()
    }

    fun showLoading() {
        isLoading = true
        notifyDataSetChanged()
    }
    fun getIsLoading(): Boolean {
        return isLoading
    }
    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }
}