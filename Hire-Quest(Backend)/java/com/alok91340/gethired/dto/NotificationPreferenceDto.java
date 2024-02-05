/**
 * 
 */
package com.alok91340.gethired.dto;

import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Data
public class NotificationPreferenceDto {

private Long id;
	
	private Long userId;
	
	private String notificationType;
	
	private Boolean muted;

}
