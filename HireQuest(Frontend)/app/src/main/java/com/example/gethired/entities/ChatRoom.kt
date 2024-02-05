package com.example.gethired.entities

data class ChatRoom (
   val id: Long,
   val receiver: User,
   val timeStamp: String,
   val unseenMessageCount: Long,
   val lastMessage: Chat,
   val isRequest:Boolean,
   val image:String
)