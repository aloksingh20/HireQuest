/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.NotificationPreferenceDto;
import com.alok91340.gethired.entities.NotificationPreference;
import com.alok91340.gethired.repository.NotificationPreferenceRepository;
import com.alok91340.gethired.service.NotificationPreferenceService;

/**
 * @author aloksingh
 *
 */
@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService{
	
	@Autowired
	private NotificationPreferenceRepository notificationPreferenceRepository;

	@Override
	public void updateNotificationPreference(String notificationType, Long userId) {
		NotificationPreference notificationPreference = this.notificationPreferenceRepository.findNotificationPreferenceByNotificationTypeAndUserId(notificationType, userId);
		notificationPreference.setMuted(!notificationPreference.getMuted());
		this.notificationPreferenceRepository.save(notificationPreference);
		
	}

	@Override
	public List<NotificationPreferenceDto> getAllNoificationPreference(Long userId) {
		List<NotificationPreference> notificationPreferences=this.notificationPreferenceRepository.findAllNotificationPreference(userId);
		
		List<NotificationPreferenceDto> notificationPreferenceDtos= notificationPreferences.stream().map(notificationPreference -> mapToDto(notificationPreference)).collect(Collectors.toList());
		
		
		
		return notificationPreferenceDtos;
	}
	
	NotificationPreferenceDto mapToDto(NotificationPreference notificationPreference) {
		
		NotificationPreferenceDto notificationPreferenceDto = new NotificationPreferenceDto();
		
		notificationPreferenceDto.setId(notificationPreference.getId());
		notificationPreferenceDto.setMuted(notificationPreference.getMuted());
		notificationPreferenceDto.setUserId(notificationPreference.getUserId());
		notificationPreferenceDto.setNotificationType(notificationPreference.getNotificationType());
		
		return notificationPreferenceDto;
	}

	@Override
	public boolean updateMuteUnMute(Long userId, Boolean condition) {
		List<NotificationPreference> notificationPreferences=this.notificationPreferenceRepository.findAllNotificationPreference(userId);
		
		for(NotificationPreference notificationPreference:notificationPreferences) {
			notificationPreference.setMuted(condition);
			this.notificationPreferenceRepository.save(notificationPreference);
		}
		return true;
	}

}
