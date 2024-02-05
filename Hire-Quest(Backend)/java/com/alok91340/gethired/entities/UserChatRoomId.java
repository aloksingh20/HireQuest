/**
 * 
 */
package com.alok91340.gethired.entities;


import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Embeddable
@Data
public class UserChatRoomId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "chatroom_id")
    private Long chatRoomId;

}
