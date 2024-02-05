/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.ProfileDto;

/**
 * @author alok91340
 *
 */
public interface ProfileLinkService {
	
	ProfileDto addProfileLink(ProfileDto profileLinkDto, Long userProfileId);
	ProfileDto updateProfileLink(ProfileDto profileLinkDto, Long profileLinkId);
	List<ProfileDto> getAllProfileLink(Long userProfileId);
	void deleteProfileLink(Long profileLinkId);
	ProfileDto getProfileLink(Long profileLinkId);
}
