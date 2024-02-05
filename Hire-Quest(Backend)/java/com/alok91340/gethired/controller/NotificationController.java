/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;

/**
 * @author alok91340
 *
 */
//NotificationController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.dto.NotificationDto;
import com.alok91340.gethired.dto.NotificationRequest;
import com.alok91340.gethired.service.NotificationService;

@RestController
@RequestMapping("api/hireQuest")
public class NotificationController {
	

	@Autowired
	private NotificationService notificationService;

	
	@PostMapping("/send-notification")
	public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
		// Call the notificationService to send the notification
		
		
		NotificationDto notification=this.notificationService.saveNotification(request);
		
//		String notificationId = fcmNotificationService.sendNotification(user.getFcmToken(), request);
     
		return ResponseEntity.ok("Notification sent successfully");
	}
	
	@PutMapping("/{notificationId}/update-notification")
	public ResponseEntity<NotificationDto> updateNotification(@PathVariable Long notificationId){
		NotificationDto notification= this.notificationService.updateNotification(notificationId);
		return new ResponseEntity<>(notification,HttpStatus.OK);
	}
	
	@DeleteMapping("/{notificationId}/delete-notification")
	public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId){
		this.notificationService.deleteNotification(notificationId);
		return new ResponseEntity<>("deleted",HttpStatus.OK);
	}
	
	
	@GetMapping("/get-notifications")
	public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam("receiverUsername") String receiverUsername){
		List<NotificationDto> notifications=this.notificationService.getAllNotification(receiverUsername);
		return new ResponseEntity<>(notifications, HttpStatus.OK);
	}
	
	
	
}
