/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.MeetingDto;

/**
 * @author aloksingh
 *
 */
public interface MeetingService {
	
	MeetingDto addMeeting(MeetingDto meetingDto);
	void deleteMeeting(Long meetingId);
	MeetingDto updateMeeting(Long meetingId,MeetingDto meetingDto);
	List<MeetingDto> upcommingMeetings(String user);
	List<MeetingDto> pastMeetings(String user);
	
}
