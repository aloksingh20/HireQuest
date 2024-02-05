/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.NotificationDto;
import com.alok91340.gethired.dto.NotificationRequest;

/**
 * @author aloksingh
 *
 */
public interface NotificationService {
	
	NotificationDto saveNotification(NotificationRequest request);
	
	NotificationDto updateNotification(Long notificationId);
	
	void deleteNotification(Long notificationId);
	
	List<NotificationDto> getAllNotification(String receiverUsername);

}
