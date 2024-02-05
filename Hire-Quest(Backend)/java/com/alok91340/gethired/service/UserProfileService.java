/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.UserProfileDto;
import com.alok91340.gethired.dto.UserProfileResponse;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author alok91340
 *
 */
public interface UserProfileService {
	
	UserProfileDto getUserProfile(Long userId);
	
	List<UserProfileResponse> getUserProfiles(String search,int pageNo, int pageSize, String sortBy, String sortDir);
	
	List<UserProfileResponse> getUserProfiles(String search,String role, int pageNo, int pageSize, String sortBy, String sortDir);
	

	
	UserProfileDto updateUserProfile(UserProfileDto userProfileDto, Long userProfileId);
	
	
	
}
