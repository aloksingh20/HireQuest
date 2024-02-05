/**
 * 
 */
package com.alok91340.gethired.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Notification {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;
    private String senderUsername;
    private String receiverUsername;
    private String notificationType;
    private boolean readStatus;
    private LocalDateTime timestamp;
    
}
