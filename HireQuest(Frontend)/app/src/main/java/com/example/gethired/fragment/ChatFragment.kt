package com.example.gethired.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.ChattingActivity
import com.example.gethired.CommonFunction
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.R
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.ChatRoomViewModel
import com.example.gethired.ViewModel.ChatViewModel
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.adapter.ChatAdapter
import com.example.gethired.entities.ChatRoom
import com.example.gethired.entities.User
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.ChatRoomViewModelFactory
import com.example.gethired.factory.ChatViewModelFactory
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class ChatFragment : Fragment() {

    private var chatRoomList: MutableList<ChatRoom> = mutableListOf()
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatRoomAdapter: ChatAdapter
    private lateinit var noChatFound: LinearLayout
    private lateinit var loadingChatList: LottieAnimationView


    private lateinit var chatViewModel: ChatViewModel
    private lateinit var tokenManager: TokenManager

    private lateinit var chatRoomViewModel: ChatRoomViewModel

    private var currentUser: UserDto?=null

    //    network connection
    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet:Boolean=true


    private lateinit var rootView:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_chat, container, false)

        networkManager = NetworkManager(requireContext())           // initializing network manager
        checkForInternetConnection()

        customErrorSnackBar= CustomErrorSnackBar()

        tokenManager= TokenManager(requireContext())

        CommonFunction.SharedPrefsUtil.init(requireContext())
        currentUser=CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()
        chatRoomViewModel = ViewModelProvider(this, ChatRoomViewModelFactory(tokenManager))[ChatRoomViewModel::class.java]
//        chatViewModel = ViewModelProvider(this,ChatViewModelFactory(tokenManager,requireContext()))[ChatViewModel::class.java]


        chatRecyclerView=rootView.findViewById(R.id.chat_recyclerView)
        noChatFound=rootView.findViewById(R.id.chat_not_found)
        loadingChatList=rootView.findViewById(R.id.loadingChat)

        loadingChatList.playAnimation()
        loadingChatList.visibility=View.VISIBLE

        chatRoomAdapter= ChatAdapter(chatRoomList,requireContext())
        chatRecyclerView.adapter=chatRoomAdapter
        chatRecyclerView.layoutManager=LinearLayoutManager(requireContext())

        chatRoomViewModel.loading.observe(viewLifecycleOwner){

        }
        chatRoomViewModel.error.observe(viewLifecycleOwner){

        }

        chatRoomViewModel.getAllChatUser(currentUser!!.id)
//        if(isConnectedToInternet){
            chatRoomViewModel.chatUserList.observe(viewLifecycleOwner){ chatRooms->
                loadingChatList.visibility=View.GONE

                if (chatRooms.isNullOrEmpty()){
                    noChatFound.visibility=View.VISIBLE
                    chatRecyclerView.visibility=View.GONE
                }else{
                    noChatFound.visibility=View.GONE
                    chatRecyclerView.visibility=View.VISIBLE
                    chatRoomList.clear()
                    chatRoomList.addAll(chatRooms)
                }

            }
//        }else{
//            loadingChatList.visibility=View.GONE
//            noChatFound.visibility=View.GONE
//            showCustomErrorSnackBar()
//        }
        chatRoomAdapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {

            override fun onItemClick(position: Int) {
                val intent=Intent(requireContext(),ChattingActivity::class.java)
                intent.putExtra("user_id",chatRoomList[position].receiver.id.toInt())
                intent.putExtra("receiver",chatRoomList[position].receiver)
                intent.putExtra("chatRoomId",chatRoomList[position].id)
                startActivity(intent)
            }
        })
        return rootView
    }

    private fun checkForInternetConnection() {
        lifecycleScope.launch(Dispatchers.Main) { // Launch coroutine on Main dispatcher
            networkManager.getNetworkConnectivityFlow()
                .flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher
                .collect {   // Collect flow on Main dispatcher
                    // Update UI or perform actions based on network state
                    isConnectedToInternet = it.isConnected
                    if(!isConnectedToInternet){
                        showCustomErrorSnackBar()
                    }
                }
        }
    }
    private fun showCustomErrorSnackBar(){
        customErrorSnackBar.showSnackbar(requireContext(),rootView.findViewById(android.R.id.content) , "No internet connection")
    }

    override fun onResume() {
        super.onResume()
        chatRoomViewModel.getAllChatUser(currentUser!!.id)
    }

}