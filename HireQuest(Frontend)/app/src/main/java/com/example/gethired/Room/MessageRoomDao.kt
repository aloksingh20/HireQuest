package com.example.gethired.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageRoomDao {

//    @Query("SELECT * FROM message_table WHERE (senderId = :senderId AND receiverId = :receiverId) OR (senderId= :receiverId AND receiverId = :senderId) ORDER BY timeStamp ASC")
//    suspend fun getMessagesBetween(senderId: Long, receiverId: Long): List<MessageRoomDto>
//
//    @Insert
//    suspend fun insertAllMessage(roomMessages: List<MessageRoomDto>)
//
//    @Insert
//    suspend fun insertMessage(messageRoomDto: MessageRoomDto)
//
//    @Delete
//    suspend fun delete(messageRoomDto: MessageRoomDto)

}
