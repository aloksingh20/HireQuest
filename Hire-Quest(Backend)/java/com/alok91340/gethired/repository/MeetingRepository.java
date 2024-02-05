/**
 * 
 */
package com.alok91340.gethired.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alok91340.gethired.entities.Meeting;

/**
 * @author aloksingh
 *
 */
public interface MeetingRepository extends JpaRepository<Meeting,Long>{

	@Query("SELECT m FROM Meeting m WHERE " +
		       "(m.employeeName = :user OR m.hrPersonName = :user) AND " +
		       "(m.meetingDate > :date OR (m.meetingDate = :date AND m.meetingTime >= :time))")
		List<Meeting> upcomingMeetings(
		        @Param("user") String user,
		        @Param("date") LocalDate date,
		        @Param("time") LocalTime time
		);

	
	@Query("SELECT m FROM Meeting m WHERE (m.employeeName = :user OR m.hrPersonName = :user) AND " +
	        "(m.meetingDate < :date OR (m.meetingDate = :date AND m.meetingTime < :time))")
	List<Meeting> pastMeetings(String user, LocalDate date, LocalTime time);
	
	@Query("SELECT m FROM Meeting m WHERE (m.employeeName = :user OR m.hrPersonName = :user) AND m.meetingDate = :date AND m.meetingTime = :time")
    Meeting currentMeeting(String user, LocalDate date, LocalTime time);
	

}
