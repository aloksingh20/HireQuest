/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.ExperienceDto;

/**
 * @author alok91340
 *
 */
public interface ExperienceService {
	
	ExperienceDto addExperience(ExperienceDto experienceDto, Long userProfileId);
	ExperienceDto updateExperience(ExperienceDto experiencezdto, Long experienceId);
	List<ExperienceDto> getAllExperience(Long userProfileId);
	void deleteExperience(Long experienceId);
	ExperienceDto getExperience(Long experienceId);
}
