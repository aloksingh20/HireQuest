/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.EducationDto;

/**
 * @author alok91340
 *
 */
public interface EducationService {
	
	EducationDto addEducation(EducationDto educationDto, Long userProfileId);
	EducationDto updateEducation(EducationDto educationDto, Long educationId);
	List<EducationDto> getAllEducation(Long userprofileId);
	void deleteEducation(Long educationId);
	EducationDto getEducation(Long educationId);
}
