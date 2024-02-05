/**
 * 
 */
package com.alok91340.gethired.dto;

import lombok.Data;

/**
 * @author alok91340
 *
 */
//NotificationRequest.java
@Data
public class NotificationRequest {
	
	private String senderUsername;
	private String title;
	private String body;
	private String receiverUsername;
	private String notificationType;
	
}
