/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alok91340.gethired.entities.ChatRoom;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.entities.UserChatRoom;
import com.alok91340.gethired.entities.UserChatRoomId;

/**
 * @author aloksingh
 *
 */
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, UserChatRoomId> {

    UserChatRoom findByChatRoomAndIsRequestSender(ChatRoom chatRoom, boolean isRequestSender);
    
    UserChatRoom findByChatRoomAndUser(ChatRoom chatRoom, User user);

    @Query("SELECT ucr FROM UserChatRoom ucr " +
           "WHERE ucr.chatRoom.id IN :chatRoomId " +
           "AND ucr.isDeleted = false " +
           "AND ucr.user.id <> :userId")
    List<UserChatRoom> findUserChatRoomByChatRoomIdsAndIsRequestSenderAndIsNotDeletedAndNotSameUser(
            @Param("userId") Long userId,
            @Param("chatRoomId") List<Long> chatRoomId);

    List<UserChatRoom> findByUserAndIsDeletedAndIsRequestSender(User user, boolean isDeleted, boolean isRequestSender);

    List<UserChatRoom> findByChatRoom(ChatRoom chatRoom);

    @Query("SELECT ucr.user FROM UserChatRoom ucr " +
           "WHERE ucr.chatRoom.id IN :chatRoomId " +
           "AND ucr.isRequestSender = true " +
           "AND ucr.user.id <> :userId")
    List<User> findUserChatRoomsByChatroomIdsAndIsRequestSenderAndNotSameUser(
            @Param("userId") Long userId,
            @Param("chatRoomId") List<Long> chatRoomId);

    @Query("SELECT ucr.chatRoom.id " +
           "FROM UserChatRoom ucr " +
           "WHERE ucr.user = :user " +
           "AND ucr.chatRoom.isDeleted = false")
    List<Long> findChatRoomIdsByUser(@Param("user") User user);

}
