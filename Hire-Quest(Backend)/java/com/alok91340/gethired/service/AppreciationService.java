/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.AppreciationDto;

/**
 * @author alok91340
 *
 */
public interface AppreciationService {
	
	AppreciationDto addAppreciation(AppreciationDto awardDto, Long userProfileId);
	AppreciationDto updateAppreciation(AppreciationDto awardDto, Long awardId);
	List<AppreciationDto> getAllAppreciation(Long userProfileId);
	void deleteAppreciation(Long awardId);
	AppreciationDto getAppreciation(Long awardId);
}
