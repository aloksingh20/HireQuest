package com.alok91340.gethired.dto;

import lombok.Data;


@Data
public class HrProfileDto {

private long id;
	
	private String summary;
	
	private String linkedinProfile;
	
	private String companyName;
	
	private String companyUrl;
	
	private String jobTitle;
	
	private int yearOfExp;
	
	private boolean isVerified;
	
	private boolean isAvailable;
	
	private String jobType;
	
	
}
