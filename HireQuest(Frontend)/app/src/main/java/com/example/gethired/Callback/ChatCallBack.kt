package com.example.gethired.Callback

import android.os.Message
import com.example.gethired.entities.Chat

interface ChatCallBack {
    fun onChatResponse(chats:List<Chat>)
    fun onChatError()
}