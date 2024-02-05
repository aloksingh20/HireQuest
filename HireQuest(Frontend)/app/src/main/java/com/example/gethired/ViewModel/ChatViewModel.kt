package com.example.gethired.ViewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gethired.Callback.ChatCallBack
import com.example.gethired.Repository.ChatRepository
import com.example.gethired.Room.MessageRoomDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.entities.Chat
import com.example.gethired.entities.ChattingUserInfo
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDateTime

class ChatViewModel(
    tokenManager: TokenManager,
    context: Context
) : ViewModel() {
    private val chatRepository=ChatRepository(tokenManager,context)

    private var _chatUserInfo = MutableLiveData<ChattingUserInfo?> (null)
    val chatUserInfo :LiveData<ChattingUserInfo?> = _chatUserInfo


    suspend fun getAllChat(senderId: Long, receiverId: Long, timeStamp: String,callback: ChatCallBack) {
        chatRepository.getAllChat(senderId, receiverId,timeStamp, object : ChatCallBack {
            override fun onChatResponse(chats: List<Chat>) {
                callback.onChatResponse(chats)
            }

            override fun onChatError() {
                callback.onChatError()
            }
        })
    }

    fun updateMessage(senderId: Long,receiverId: Long,chat: Chat): MutableLiveData<Chat> {
        return chatRepository.updateMessage(senderId,receiverId,chat)
    }
//
//    suspend fun getMessagesFromDb(senderUsername: Long, receiverUsername: Long): List<MessageRoomDto> {
//        return chatRepository.getMessagesFromDb(senderUsername, receiverUsername)
//    }
//
//    suspend fun insertAllMessagesToRoom(roomMessages: List<MessageRoomDto>) {
//        chatRepository.insertAllMessagesToRoom(roomMessages)
//    }
//
//    suspend fun insertMessageToRoom(messageRoomDto: MessageRoomDto) {
//        chatRepository.insertMessageToRoom(messageRoomDto)
//    }
//
//    suspend fun deleteMessageFromRoom(messageRoomDto: MessageRoomDto) {
//        chatRepository.deleteMessageFromRoom(messageRoomDto)
//    }

    fun getUserInfo(senderId: Long,receiverId: Long){
        viewModelScope.launch {
            try {
                val response= chatRepository.getUserInfo(senderId, receiverId)

                _chatUserInfo.value=response

            }
            catch (e :Exception){
                Log.d("error",e.localizedMessage)
            }
        }
    }
}
