/**
 * 
 */
package com.alok91340.gethired.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Data
public class NotificationDto {
	
	private Long id;
    private String title;
    private String body;
    private String senderUsername;
    private String receiverUserName;
    private String notificationType;
    private boolean readStatus;
    private LocalDateTime timestamp;

}
