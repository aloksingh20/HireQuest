/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.NotificationDto;
import com.alok91340.gethired.dto.NotificationRequest;
import com.alok91340.gethired.entities.Notification;
import com.alok91340.gethired.entities.NotificationPreference;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.repository.NotificationPreferenceRepository;
import com.alok91340.gethired.repository.NotificationRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.service.NotificationService;

/**
 * @author aloksingh
 *
 */

@Service
public class NotificationServiceImpl implements NotificationService{
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private NotificationPreferenceRepository notificationPreferenceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private final FcmNotificationService fcmNotificationService;
	
	@Autowired
	public NotificationServiceImpl(FcmNotificationService fcmNotificationService) {
		this.fcmNotificationService = fcmNotificationService;
	}

	@Override
	public NotificationDto saveNotification(NotificationRequest request) {
		Notification notification= new Notification();
		notification.setBody(request.getBody());
		notification.setNotificationType(request.getNotificationType());
		notification.setReadStatus(false);
		notification.setSenderUsername(request.getSenderUsername());
		notification.setReceiverUsername(request.getReceiverUsername());
		notification.setTitle(request.getTitle());
		notification.setTimestamp(LocalDateTime.now());
		Notification savedNotification=this.notificationRepository.save(notification);
		
		
		User user = userRepository.findUserByUsername(request.getReceiverUsername());
		
		NotificationPreference np=this.notificationPreferenceRepository.findNotificationPreferenceByNotificationTypeAndUserId(request.getNotificationType(), user.getId());
		
		if(!np.getMuted() && user.getFcmToken()!=null&& !user.getFcmToken().isEmpty()) {
			fcmNotificationService.sendNotification(user.getFcmToken(), request);
		}
		
		return mapToDto(savedNotification);
	}

	@Override
	public void deleteNotification(Long notificationId) {
		Notification notification= this.notificationRepository.findById(notificationId).orElseThrow();
		this.notificationRepository.delete(notification);
		
	}

	@Override
	public List<NotificationDto> getAllNotification(String receiverUsername) {
		List<Notification> notifications=this.notificationRepository.getNotificationAccordingToReceiverId(receiverUsername);
		List<NotificationDto> notificationDtos=notifications.stream().map(notification->mapToDto(notification)).collect(Collectors.toList());
		return notificationDtos;
	}
	
	NotificationDto mapToDto(Notification notification) {
		
		NotificationDto notificationDto= new NotificationDto();
		notificationDto.setId(notification.getId());
		notificationDto.setBody(notification.getBody());
		notificationDto.setTitle(notification.getTitle());
		notificationDto.setNotificationType(notification.getNotificationType());
		notificationDto.setReadStatus(notification.isReadStatus());
		notificationDto.setReceiverUserName(notification.getReceiverUsername());
		notificationDto.setSenderUsername(notification.getSenderUsername());
		notificationDto.setTimestamp(notification.getTimestamp());
		
		return notificationDto;
	}

	@Override
	public NotificationDto updateNotification(Long notificationId) {
		Notification notification= this.notificationRepository.findById(notificationId).orElseThrow();
		notification.setReadStatus(true);
		Notification updatedNotification=this.notificationRepository.save(notification);
		return mapToDto(updatedNotification);
	}

}
