/**
 * 
 */
package com.alok91340.gethired.dto;


import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Data
public class MeetingDto {

private long id;
	
	private String user;
	private String hr;
	private String time;
	private String date;
	private String link;
	private boolean isAttended;
}
