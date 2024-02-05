/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import com.alok91340.gethired.dto.NotificationRequest;
import com.alok91340.gethired.repository.NotificationPreferenceRepository;
import com.alok91340.gethired.repository.NotificationRepository;
/**
 * @author alok91340
 *
 */
//NotificationService.java
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FcmNotificationService {
	
	

 public String sendNotification(String fcmToken, NotificationRequest request) {
     Notification notification = Notification.builder()
             .setTitle(request.getTitle())
             .setBody(request.getBody())
             .build();

     Message message = Message.builder()
             .setNotification(notification)
             .putData("notification_type", request.getNotificationType())
             .putData("sender", request.getSenderUsername())
             .setToken(fcmToken)
             .build();

     try {
         String messageId=FirebaseMessaging.getInstance().send(message);
         return messageId;
     } catch (Exception e) {
         // Handle the exception
         e.printStackTrace();
         return null;
     }
 }
}
