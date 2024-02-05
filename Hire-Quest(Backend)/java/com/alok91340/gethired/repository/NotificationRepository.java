/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alok91340.gethired.entities.Notification;

/**
 * @author aloksingh
 *
 */
public interface NotificationRepository extends JpaRepository<Notification,Long>{
	
	@Query("select n from Notification n where n.receiverUsername = :receiverUsername ORDER BY n.timestamp DESC")
    List<Notification> getNotificationAccordingToReceiverId(@Param("receiverUsername") String receiverUsername);

}
