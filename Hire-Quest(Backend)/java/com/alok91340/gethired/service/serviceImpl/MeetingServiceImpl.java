/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.MeetingDto;
import com.alok91340.gethired.entities.Meeting;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.MeetingRepository;
import com.alok91340.gethired.service.MeetingService;

/**
 * @author aloksingh
 *
 */
@Service
public class MeetingServiceImpl implements MeetingService{

	@Autowired
	private MeetingRepository meetingRepository;
	
	
	@Override
	public MeetingDto addMeeting( MeetingDto meetingDto) {
		Meeting meeting=new Meeting();
		System.out.println(meetingDto.toString());
		mapToEntity(meetingDto,meeting);
		
		
		String[] time=meetingDto.getTime().split(":");
		int hour = Integer.parseInt(time[0]);
		String[] temp=time[1].split(" ");
		int min=Integer.parseInt(temp[0]);
		if(temp[1]=="AM") {
			hour=Integer.parseInt(time[0]);
		}else {
			hour=Integer.parseInt(time[0])+12;
		}
		System.out.println("nnn "+hour);

		
		String[] date=meetingDto.getDate().split("-");
		int year=Integer.parseInt(date[2]);
		int month=Integer.parseInt(date[1]);
		int day=Integer.parseInt(date[0]);
		
		LocalDate dateToCheck = LocalDate.of(year, month, day);
        LocalTime timeToCheck = LocalTime.of(hour, min);

        if (dateToCheck.isBefore(LocalDate.now()) ||
                (dateToCheck.isEqual(LocalDate.now()) && timeToCheck.isBefore(LocalTime.now()))) {
            return null;
        }
		
		
		Meeting currentMeeting1=this.meetingRepository.currentMeeting(meeting.getEmployeeName(),  dateToCheck, timeToCheck);
		Meeting currentMeeting2=this.meetingRepository.currentMeeting(meeting.getHrPersonName(),  dateToCheck, timeToCheck);

		Meeting savedMeeting=null;
		if(currentMeeting1==null&&currentMeeting2==null) {
			savedMeeting=this.meetingRepository.save(meeting);
			return mapToDto(savedMeeting);
		}
		return null;
	}

	@Override
	public void deleteMeeting(Long meetingId) {
		Meeting meeting=this.meetingRepository.findById(meetingId).orElseThrow(()-> new ResourceNotFoundException("meeting",meetingId));
		this.meetingRepository.delete(meeting);
		return;
	}

	@Override
	public MeetingDto updateMeeting(Long meetingId, MeetingDto meetingDto) {
		Meeting meeting=this.meetingRepository.findById(meetingId).orElseThrow(()-> new ResourceNotFoundException("meeting",meetingId));
		mapToEntity(meetingDto,meeting);
		Meeting savedMeeting=this.meetingRepository.save(meeting);
		return mapToDto(savedMeeting);
	}
	
	private void mapToEntity(MeetingDto meetingDto, Meeting meeting) {

		String[] time=meetingDto.getTime().split(":");
		int hour = Integer.parseInt(time[0]);
		System.out.println("mmm1 "+hour);

		String[] temp=time[1].split(" ");
		int min=Integer.parseInt(temp[0]);
		if(temp[1]=="AM") {
			hour=Integer.parseInt(time[0]);
		}else {
			hour=Integer.parseInt(time[0])+12;
		}
		System.out.println("mmm2 "+hour);

		
		String[] date=meetingDto.getDate().split("-");
		int year=Integer.parseInt(date[2]);
		int month=Integer.parseInt(date[1]);
		int day=Integer.parseInt(date[0]);
		
		meeting.setMeetingTime(LocalTime.of(hour, min));
		meeting.setMeetingLink(meetingDto.getLink());
		meeting.setMeetingDate(LocalDate.of(year, month, day));
		meeting.setEmployeeName(meetingDto.getUser());
		meeting.setHrPersonName(meetingDto.getHr());
		meeting.setHasTakenPlace(false);
	}
	
	private MeetingDto mapToDto( Meeting meeting) {
		
		LocalTime currentTime = meeting.getMeetingTime();

        // Extract Hour, Minute, and Second from LocalTime
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();
        int second = currentTime.getSecond();
        
        LocalDate currentDate = meeting.getMeetingDate();

        // Extract Year, Month, and Day from LocalDate
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        
        
        String h=(hour<9)?"0"+hour:""+hour;
        String m=(minute<9)?"0"+minute:""+minute;
        
		
		MeetingDto meetingDto =new MeetingDto();
		meetingDto.setId(meeting.getId());
		meetingDto.setTime(h+":"+m);
		meetingDto.setLink(meeting.getMeetingLink());
		meetingDto.setDate(day+"-"+month+"-"+year);
		meetingDto.setUser(meeting.getEmployeeName());
		meetingDto.setHr(meeting.getHrPersonName());
		meetingDto.setAttended(meeting.isHasTakenPlace());
		return meetingDto;
	}

	@Override
	public List<MeetingDto> upcommingMeetings(String user) {
        
		List<Meeting> meetings =  this.meetingRepository.upcomingMeetings(user, LocalDate.now(), LocalTime.now());
		
		List<MeetingDto> meetingDtos=meetings.stream().map(meeting-> mapToDto(meeting)).collect(Collectors.toList());
		return meetingDtos;
	}

	@Override
	public List<MeetingDto> pastMeetings(String user) {
     
		List<Meeting> meetings = this.meetingRepository.pastMeetings(user,LocalDate.now(), LocalTime.now());
		
		List<MeetingDto> meetingDtos=meetings.stream().map(meeting-> mapToDto(meeting)).collect(Collectors.toList());
		return meetingDtos;
	}

}
