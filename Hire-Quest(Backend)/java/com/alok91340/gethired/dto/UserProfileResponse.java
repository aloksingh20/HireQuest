/**
 * 
 */
package com.alok91340.gethired.dto;

import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Data
public class UserProfileResponse {
	
	private Long id;
	private String username;
	private String name;
	private String email;
	private String headline;
	private String birthdate;
	private String currentOccupation;
	private boolean status;
	private String phone;
	private int isRecuriter;
	private String gender;
	
	private String about;
	
	private String imageData;


}
