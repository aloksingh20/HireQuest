/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.dto.NotificationPreferenceDto;
import com.alok91340.gethired.service.NotificationPreferenceService;

/**
 * @author aloksingh
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class NotificationPreferenceController {

	@Autowired
	private NotificationPreferenceService notificationPreferenceService;
	
	@PutMapping("/muteAllNotification")
	private ResponseEntity<Boolean> muteAllNotification(@RequestParam("userId") Long userId){
		Boolean isMuted=this.notificationPreferenceService.updateMuteUnMute(userId,true);
		return ResponseEntity.ok(isMuted);
	}
	
	@PutMapping("/unMuteAllNotification")
	private ResponseEntity<Boolean> unMuteAllNotification(@RequestParam("userId") Long userId){
		Boolean isunMuted=this.notificationPreferenceService.updateMuteUnMute(userId,false);
		return ResponseEntity.ok(isunMuted);
	}
	
	@PostMapping("/{notificationType}/mute")
	private ResponseEntity<Boolean> updateNotificationPreference(@PathVariable String notificationType,@RequestParam("userId") Long userId ){
		
		this.notificationPreferenceService.updateNotificationPreference(notificationType, userId);
		
		return ResponseEntity.ok(true);
		
	}
	
	@GetMapping("/notificationPreferences")
	private ResponseEntity<List<NotificationPreferenceDto>> allNotificationPreference(@RequestParam("userId") Long userId){
		List<NotificationPreferenceDto> notificationPreferenceDtos=this.notificationPreferenceService.getAllNoificationPreference(userId);
		return ResponseEntity.ok(notificationPreferenceDtos);
		
	}
}
