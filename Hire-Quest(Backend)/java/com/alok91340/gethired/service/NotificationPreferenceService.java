/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.NotificationPreferenceDto;
/**
 * @author aloksingh
 *
 */
public interface NotificationPreferenceService {
	
	void updateNotificationPreference(String notificationType, Long userId);
	List<NotificationPreferenceDto> getAllNoificationPreference(Long userId);
	
	boolean updateMuteUnMute(Long userId, Boolean condition);

}
