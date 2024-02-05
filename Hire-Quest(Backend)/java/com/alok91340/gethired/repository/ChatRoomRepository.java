/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alok91340.gethired.entities.ChatRoom;
import com.alok91340.gethired.entities.User;

/**
 * @author aloksingh
 *
 */


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	/**
	 * @param user1
	 * @param user2
	 * @return
	 */
	@Query("SELECT c FROM ChatRoom c WHERE :user1 MEMBER OF c.users AND :user2 MEMBER OF c.users AND c.isGroup = false")
    Optional<ChatRoom> findChatRoomByUsers(@Param("user1") User user1, @Param("user2") User user2);
	
//	List<ChatRoom> findAllBySenderAndIsRequest(User sender, boolean isRequest);
//
//    
//    List<ChatRoom> findAllByReceiverAndIsRequest(User receiver, boolean isRequest);
//    
//    List<ChatRoom> findAllBySenderOrReceiverAndIsRequest(User sender, User receiver, boolean isRequest);

    
    
}
