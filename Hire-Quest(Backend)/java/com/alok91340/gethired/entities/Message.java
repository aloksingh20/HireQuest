/**
 * 
 */
package com.alok91340.gethired.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Message {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "room_id")
    private Long roomId;

    private Long senderId;

    private Long receiverId;

    private String content;
    private String timestamp;
    private boolean seen;
    private boolean isDelivered;

}
