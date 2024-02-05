package com.example.gethired

import android.content.*
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.ChatCallBack
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.Room.MessageRoomDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.ChatRoomViewModel
import com.example.gethired.ViewModel.ChatViewModel
import com.example.gethired.ViewModel.UserViewModel
import com.example.gethired.adapter.MessageAdapter
import com.example.gethired.entities.Chat
import com.example.gethired.entities.ChattingUserInfo
import com.example.gethired.entities.UserDto
import com.example.gethired.factory.ChatRoomViewModelFactory
import com.example.gethired.factory.ChatViewModelFactory
import com.example.gethired.factory.UserViewModelFactory
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.example.gethired.websocket.WebSocketManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChattingActivity : AppCompatActivity() {
    private lateinit var pageHeadingUsername: TextView

    private lateinit var adapter: MessageAdapter
    private lateinit var sendMessageBtn: ImageView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var editText: TextInputEditText
    private lateinit var isUserOnline: TextView
    private lateinit var pageHeader: LinearLayout
    private lateinit var webSocket: WebSocket
    private lateinit var userImage: ShapeableImageView
    private lateinit var backButton: ImageView

    private lateinit var chattingRecyclerView: RecyclerView
    private lateinit var sendMessageContainer: LinearLayout
    private lateinit var acceptRequestContainer: LinearLayout
    private lateinit var requestSentContainer: LinearLayout
    private lateinit var acceptRequestButton: MaterialButton
    private lateinit var deleteRequestButton: MaterialButton
    private lateinit var requestLoadingAnimation: LottieAnimationView

    private lateinit var sharedPref: SharedPreferences
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatRoomViewModel: ChatRoomViewModel
    private lateinit var userViewModel: UserViewModel

    private var messages = mutableListOf<Chat>()
    private lateinit var roomMessages: List<MessageRoomDto>
    private lateinit var tokenManager: TokenManager
    private var currentUserId = 0
    private var otherUserId = 0
    private var chatRoomId = 0
    private var receiverUser: UserDto? = null

    private var chattingUserInfo: ChattingUserInfo? = null

    private var isChattingScreenVisible = false

    private val isChatLoaded: String = "chat_loading_pref_"

    //    network connection
    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        networkManager = NetworkManager(this)           // initializing network manager
        checkForInternetConnection()

        customErrorSnackBar = CustomErrorSnackBar()

        registerReceiver(receiver, IntentFilter("NEW_MESSAGE_RECEIVED"))
        isChattingScreenVisible = true
        tokenManager = TokenManager(this@ChattingActivity)

        // Assuming you're inside the ChattingActivity
        val webSocketService = WebSocketManager.webSocketService
        if (webSocketService != null) {
            webSocket = webSocketService.getWebSocketInstance()
        }

        CommonFunction.SharedPrefsUtil.init(applicationContext)
        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        currentUserId =
            CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()!!.id.toInt()
        chatViewModel = ViewModelProvider(
            this,
            ChatViewModelFactory(tokenManager, this)
        )[ChatViewModel::class.java]
        chatRoomViewModel = ViewModelProvider(
            this,
            ChatRoomViewModelFactory(tokenManager)
        )[ChatRoomViewModel::class.java]
        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(tokenManager)
        )[UserViewModel::class.java]
//        get chatting partner/user
        // Check if URI is present
        val uri = intent.data
        if (uri != null) {
            // Handle data from URI
            val chatId = uri.lastPathSegment // Assuming chat ID is in the last path segment

            // Use chatId as needed
        } else {
            // Handle data from extras
            otherUserId = intent.getIntExtra("user_id", currentUserId)
            chatRoomId = intent.getLongExtra("chatRoomId", 0).toInt()


        }

        val isChatLoadedFromBackend = sharedPref.getBoolean(isChatLoaded + otherUserId, false)

        if (!isChatLoadedFromBackend) {
            loadMessagesFromBackend()

            val editor = sharedPref.edit()
            editor.putBoolean(isChatLoaded + otherUserId, false)
            editor.apply()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
//                val messageFromDb = chatViewModel.getMessagesFromDb(senderId.toLong(),receiverId.toLong())
//                messages.clear()
//                messages.addAll(messageFromDb.map { it.toChat() })
            }

//            fetching recent messages from backend

            val lastChatTimeStamp = LocalDateTime.parse(
                messages[messages.size - 1].timestamp.subSequence(0, 23),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            )
            loadNewMessageFromBackend(lastChatTimeStamp)
        }

//        activity field initialization
        pageHeadingUsername = findViewById(R.id.page_heading_user_name)
        backButton = findViewById(R.id.back_button)
        pageHeader = findViewById(R.id.page_heading)
        userImage = findViewById(R.id.page_heading_user_profile_img)
        textInputLayout = findViewById(R.id.send_message_Layout)
        editText = findViewById(R.id.send_message_Edittext)
        sendMessageBtn = findViewById(R.id.send_message_Btn)
        isUserOnline = findViewById(R.id.page_heading_isUserOnline)
        chattingRecyclerView = findViewById(R.id.chatting_content_recyclerview)
        sendMessageContainer = findViewById(R.id.page_bottomContainer_message)
        requestSentContainer = findViewById(R.id.page_bottomContainer_send_request)
        acceptRequestContainer = findViewById(R.id.page_bottomContainer_accept_request)
        acceptRequestButton = findViewById(R.id.message_request_accept_button)
        deleteRequestButton = findViewById(R.id.message_request_reject_button)
        requestLoadingAnimation = findViewById(R.id.message_request_loading_animation)

        chatViewModel.getUserInfo(currentUserId.toLong(), otherUserId.toLong())

        chatViewModel.chatUserInfo.observe(this) {
            chattingUserInfo = it
            if (chattingUserInfo != null) {
                setUserDetails()
            }
        }
        setUserDetails()
        adapter = MessageAdapter(messages, currentUserId.toString())
        chattingRecyclerView.layoutManager = LinearLayoutManager(this)
        chattingRecyclerView.adapter = adapter

        backButton.setOnClickListener {
            onBackPressed()
        }
        // Set up send button click listener
        sendMessageBtn.setOnClickListener {
            val messageContent = editText.text.toString()
            if (messageContent.isNotEmpty()) {

                sendMessage(messageContent)
            }
        }

        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayout.hint = ""
            } else {
                textInputLayout.hint = "Write a message"
            }
        }

        acceptRequestButton.setOnClickListener {
            requestLoadingAnimation.visibility = View.GONE
            if (chatRoomId != 0) {
                chatRoomViewModel.acceptChatRequest(chatRoomId.toLong())
                chatRoomViewModel.loading.observe(this@ChattingActivity) { isLoading ->
                    if (isLoading) {
                        requestLoadingAnimation.visibility = View.VISIBLE
                        acceptRequestContainer.visibility = View.GONE
                    }
                }
                chatRoomViewModel.error.observe(this@ChattingActivity) { error ->
                    requestLoadingAnimation.visibility = View.GONE
                    showCustomErrorSnackBar()
                }
                chatRoomViewModel.isRequestAccepted.observe(this@ChattingActivity) { isAccepted ->
                    if (isAccepted) {
                        requestLoadingAnimation.visibility = View.GONE
                        sendMessageContainer.visibility = View.VISIBLE
                        acceptRequestContainer.visibility = View.GONE
                        requestSentContainer.visibility = View.GONE

                    }
                }


            }

        }
        deleteRequestButton.setOnClickListener {
            requestLoadingAnimation.visibility = View.VISIBLE
            if (chatRoomId != 0) {
                chatRoomViewModel.deleteChatRequest(chatRoomId.toLong())
                chatRoomViewModel.isRequestDeleted.observe(this@ChattingActivity) { isDeleted ->
                    if (isDeleted) {
                        requestLoadingAnimation.visibility = View.GONE
                        acceptRequestContainer.visibility = View.GONE
                        sendMessageContainer.visibility = View.GONE
                        requestSentContainer.visibility = View.GONE
                    }
                }
                chatRoomViewModel.error.observe(this@ChattingActivity) { error ->
                    requestLoadingAnimation.visibility = View.GONE
                    showCustomErrorSnackBar()
                }
            }
        }

        pageHeader.setOnClickListener {

            moveToUsersProfile()
        }
    }

    private fun setUserDetails() {
        if (chattingUserInfo != null) {

            val imageBytes = java.util.Base64.getDecoder().decode(chattingUserInfo!!.image)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            userImage.setImageBitmap(bitmap)

            pageHeadingUsername.text = chattingUserInfo!!.username
            if (chattingUserInfo!!.isRequested) {

                sendMessageContainer.visibility = View.GONE

                if (chattingUserInfo!!.isSender) {
                    acceptRequestContainer.visibility = View.GONE
                    requestSentContainer.visibility = View.VISIBLE
                } else {

                    acceptRequestContainer.visibility = View.VISIBLE
                    requestSentContainer.visibility = View.GONE
                }

            } else {
                sendMessageContainer.visibility = View.VISIBLE
                acceptRequestContainer.visibility = View.GONE
                requestSentContainer.visibility = View.GONE
            }
        }
    }

    private fun moveToUsersProfile() {

        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("userId", otherUserId.toLong())
        startActivity(intent)

    }


    private fun loadNewMessageFromBackend(timeStamp: LocalDateTime) {
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Define your desired format

        val formattedDateTime: String = timeStamp.format(formatter)
        CoroutineScope(Dispatchers.Main).launch {
            chatViewModel.getAllChat(
                currentUserId.toLong(),
                otherUserId.toLong(),
                formattedDateTime,
                object : ChatCallBack {
                    override fun onChatResponse(chats: List<Chat>) {
                        messages.clear()
                        messages.addAll(chats)
                        adapter.notifyDataSetChanged()
                        chattingRecyclerView.scrollToPosition(messages.size - 1)
                    }

                    override fun onChatError() {

                    }

                })
        }
    }

    private fun loadMessagesFromBackend() {
        val dateTime = LocalDateTime.of(2000, 4, 20, 0, 0)
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Define your desired format

        val formattedDateTime: String = dateTime.format(formatter)
        CoroutineScope(Dispatchers.Main).launch {
            chatViewModel.getAllChat(currentUserId.toLong(), otherUserId.toLong(),
                formattedDateTime, object : ChatCallBack {
                    override fun onChatResponse(chats: List<Chat>) {
                        messages.addAll(chats)
                        adapter.notifyDataSetChanged()
                        chattingRecyclerView.scrollToPosition(messages.size - 1)
                    }

                    override fun onChatError() {
                        messages.clear()
                    }

                })
        }
    }

    private fun sendMessage(content: String) {

        val messageData = Chat(
            0,
            chatRoomId.toLong(),
            currentUserId.toLong(),
            otherUserId.toLong(),
            content,
            LocalDateTime.now().toString(),
            false
        )
        val messageJson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()
            .toJson(messageData)
        if (isConnectedToInternet) {
            if (webSocket.send(messageJson)) {
                // Add the message to your UI
                messages.add(messageData)
                adapter.notifyItemInserted(messages.size - 1)
                chattingRecyclerView.scrollToPosition(messages.size - 1)

                // Clear the message input field
                editText.text?.clear()

//            inserting message to db
                CoroutineScope(Dispatchers.Main).launch {
//                chatViewModel.insertMessageToRoom(messageData.toMessageRoomDto())
                }

            } else {
                Toast.makeText(this, "Unable to send message!", Toast.LENGTH_SHORT).show()
            }
        } else {
            showCustomErrorSnackBar()
        }

    }

    fun MessageRoomDto.toChat(): Chat {
        return Chat(
            // Map the properties from MessageRoomDto to Chat
            id = this.id,
            roomId = this.roomId,
            senderId = this.senderId,
            receiverId = this.receiverId,
            content = this.content!!,
            timestamp = this.timeStamp.toString(),
            seen = this.seen
        )
    }

    private fun Chat.toMessageRoomDto(): MessageRoomDto {
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
//        val dateTime = LocalDateTime.parse(messageData.get("timeStamp") as String, formatter)

        val chatTimeStamp = LocalDateTime.parse(
            this.timestamp,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        )
        return MessageRoomDto(
            // Map the properties from Chat to MessageRoomDto

            id = this.id,
            roomId = this.roomId,
            senderId = this.senderId,
            receiverId = this.receiverId,
            content = this.content,
            timeStamp = chatTimeStamp,
            seen = this.seen
        )
    }

    override fun onResume() {
        super.onResume()
        isVisible = true

    }

    override fun onPause() {
        super.onPause()
        isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isVisible = false
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (isConnectedToInternet) {

                if (intent?.action == "NEW_MESSAGE_RECEIVED") {
                    val messageJson = intent.getStringExtra("message")

                    // Convert the JSON message to a Chat object
                    val messageData = Gson().fromJson(messageJson, Chat::class.java)

                    // Create a Chat object
                    val message = Chat(
                        id = 0,
                        roomId = messageData.roomId,
                        senderId = messageData.senderId,
                        receiverId = messageData.receiverId,
                        content = messageData.content,
                        timestamp = messageData.timestamp,
                        seen = false
                    )

                    // Add the message to the list
                    messages.add(messageData)

                    // Notify the adapter that a new message has been added
                    adapter.notifyItemInserted(messages.size - 1)

                    // Scroll to the newly added message
                    chattingRecyclerView.scrollToPosition(messages.size - 1)

                    // If the chatting screen is not visible, send a notification
                } else {
                    showCustomErrorSnackBar()
                }
            }
        }
    }

    companion object {
        var isVisible = false
    }

    private fun checkForInternetConnection() {
        lifecycleScope.launch(Dispatchers.Main) { // Launch coroutine on Main dispatcher
            networkManager.getNetworkConnectivityFlow()
                .flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher
                .collect {   // Collect flow on Main dispatcher
                    // Update UI or perform actions based on network state
                    isConnectedToInternet = it.isConnected
                    if (!isConnectedToInternet) {
                        showCustomErrorSnackBar()
                    }
                }
        }
    }

    private fun showCustomErrorSnackBar() {
        val rootView: View = findViewById(android.R.id.content)
        customErrorSnackBar.showSnackbar(this, rootView, "No internet connection")
    }

}