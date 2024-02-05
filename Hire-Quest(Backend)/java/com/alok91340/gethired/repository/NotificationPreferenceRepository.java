/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alok91340.gethired.entities.NotificationPreference;

/**
 * @author aloksingh
 *
 */
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference,Long>{

	NotificationPreference findNotificationPreferenceByNotificationTypeAndUserId(String NotificationType, Long userId);
	
	@Query("Select n from NotificationPreference n where n.userId =:userId")
	List<NotificationPreference> findAllNotificationPreference(Long userId);
	
	NotificationPreference findNNotificationPreferenceByNotificationType(String NotificationType);
	
}
