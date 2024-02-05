package com.example.gethired.Repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gethired.Callback.ChatCallBack
import com.example.gethired.Room.AppDatabase
import com.example.gethired.Room.MessageRoomDto
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.RetrofitClient
import com.example.gethired.api.retrofitInterface.ApiService
import com.example.gethired.entities.Chat
import com.example.gethired.entities.ChattingUserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatRepository(tokenManager: TokenManager, context: Context) {
    private val retrofitAPI: ApiService = RetrofitClient(tokenManager).getApiService()
//    private val messageCache: MutableMap<Pair<Long, Long>, List<MessageRoomDto>> = mutableMapOf()
    private val roomDatabase: AppDatabase = AppDatabase.getDatabase(context)

    suspend fun getAllChat(senderId: Long, receiverId: Long,timeStamp: String, callback: ChatCallBack) {

            val call: Call<List<Chat>> = retrofitAPI.getChat(senderId, receiverId,timeStamp)
            call.enqueue(object :Callback<List<Chat>>{
                override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val chats = response.body()!!
                        callback.onChatResponse(chats)

                        // Convert and cache messages
                        val roomMessages = chats.map { it.toMessageRoomDto() }
//                messageCache[cacheKey] = roomMessages

                        // Insert messages to Room database
                        CoroutineScope(Dispatchers.Main).launch {
                            insertAllMessagesToRoom( roomMessages)
                        }
                    } else {
                        callback.onChatError()
                    }
                }

                override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
                    callback.onChatError()
                }

            })



    }

    suspend fun getMessagesFromDb(senderId: Long, receiverId: Long, ): Nothing? {

        //            roomDatabase.messageRoomDao().getMessagesBetween(senderId, receiverId)
//        Log.d("room size", messages.size.toString())
        return null as Nothing?
    }

    suspend fun insertAllMessagesToRoom( roomMessages: List<MessageRoomDto>) {
        withContext(Dispatchers.IO) {
//            roomDatabase.messageRoomDao().insertAllMessage(roomMessages)
        }
    }

    suspend fun insertMessageToRoom(messageRoomDto: MessageRoomDto) {
        withContext(Dispatchers.IO) {
//            roomDatabase.messageRoomDao().insertMessage(messageRoomDto)

            // Update messageCache if it exists
//            val cacheKey = Pair(senderId, receiverId)
//            messageCache[cacheKey]?.let { cachedMessages ->
//                messageCache[cacheKey] = cachedMessages + messageRoomDto
//            }
        }
    }


    suspend fun deleteMessageFromRoom(messageRoomDto: MessageRoomDto) {
        withContext(Dispatchers.IO) {
//            roomDatabase.messageRoomDao().delete(messageRoomDto)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Chat.toMessageRoomDto(): MessageRoomDto {
        val chatTimeStamp = LocalDateTime.parse(this.timestamp.substring(0, 23), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        return MessageRoomDto(
            id = this.id,
            roomId = this.roomId,
            senderId = this.senderId,
            receiverId = this.receiverId,
            content = this.content,
            timeStamp = chatTimeStamp,
            seen = this.seen
        )
    }

    fun updateMessage(senderId: Long, receiverId: Long, chat: Chat): MutableLiveData<Chat> {
        val updatedChat = MutableLiveData<Chat> ()

        val call : Call<Chat> = retrofitAPI.updateChatMessage(senderId,receiverId,chat)

        call.enqueue(object :Callback<Chat>{
            override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                if(response.isSuccessful){
                    updatedChat.value=response.body()
                }
            }

            override fun onFailure(call: Call<Chat>, t: Throwable) {

            }

        })
        return updatedChat
    }


   suspend fun getUserInfo(senderId: Long,receiverId: Long):ChattingUserInfo?{
        return try {
            withContext(Dispatchers.IO){
                val response = retrofitAPI.getChattingUserInfo(senderId, receiverId).awaitResponse()
                if(response.isSuccessful&&response.body()!=null){
                    response.body()
                }else{
                    null
                }
            }
        }
        catch (e: Exception) {
            null // Handle errors here
        }

    }

}
