/**
 * 
 */
package com.alok91340.gethired.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author aloksingh
 *
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChatRoom {
	
	@EmbeddedId
    private UserChatRoomId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("chatRoomId")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private boolean isDeleted = false;
    
    private boolean isRequestSender;


}
