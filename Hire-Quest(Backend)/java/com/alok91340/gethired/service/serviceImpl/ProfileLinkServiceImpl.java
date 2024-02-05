/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.ProfileDto;
import com.alok91340.gethired.entities.Profile;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.ProfileRepository;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.ProfileLinkService;

/**
 * @author alok91340
 *
 */
@Service
public class ProfileLinkServiceImpl implements ProfileLinkService{
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private ProfileRepository profileLinkRespository;

	@Override
	public ProfileDto addProfileLink(ProfileDto profileLinkDto, Long userProfileId) {
		
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		Profile profileLink= new Profile();
		mapToEntity(profileLinkDto,profileLink);
		profileLink.setUserProfile(userProfile);
		Profile savedProfileLink=this.profileLinkRespository.save(profileLink);
		return mapToDto(savedProfileLink);
	}

	@Override
	public ProfileDto updateProfileLink(ProfileDto profileLinkDto, Long profileLinkId) {
		Profile profileLink= this.profileLinkRespository.findById(profileLinkId).orElseThrow(()-> new ResourceNotFoundException("profile-link",profileLinkId));
		mapToEntity(profileLinkDto,profileLink);
		Profile savedProfileLink=this.profileLinkRespository.save(profileLink);
		return mapToDto(savedProfileLink);
	}

	@Override
	public 	ProfileDto getProfileLink(Long profileLinkId) {
		Profile profileLink= this.profileLinkRespository.findById(profileLinkId).orElseThrow(()-> new ResourceNotFoundException("profile-link",profileLinkId));
		return mapToDto(profileLink);
	}
	@Override
	public List<ProfileDto> getAllProfileLink(Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("User-Profile",userProfileId));
		List<Profile> profileLinks=this.profileLinkRespository.findAllByUserProfile(userProfile);
		List<ProfileDto> profileLinkDtos=profileLinks.stream().map(profileLink->mapToDto(profileLink)).collect(Collectors.toList());
		
		return profileLinkDtos;
	}

	@Override
	public void deleteProfileLink(Long profileLinkId) {
		this.profileLinkRespository.deleteById(profileLinkId);
		
	}
	private Profile mapToEntity(ProfileDto profileLinkDto,Profile profileLink) {
		profileLink.setProfileName(profileLinkDto.getHandleName());
		profileLink.setProfileUrl(profileLinkDto.getProfileUrl());
		return profileLink;
	}
	private ProfileDto mapToDto(Profile profileLink) {
		
		ProfileDto profileLinkDto= new ProfileDto();
		profileLinkDto.setId(profileLink.getId());
		profileLinkDto.setHandleName(profileLink.getProfileName());
		profileLinkDto.setProfileUrl(profileLink.getProfileUrl());
		return profileLinkDto;
	}

}
