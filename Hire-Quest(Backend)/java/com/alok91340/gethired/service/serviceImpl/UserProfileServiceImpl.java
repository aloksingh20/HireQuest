/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.UserProfileDto;
import com.alok91340.gethired.dto.UserProfileResponse;
import com.alok91340.gethired.entities.Image;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.UserProfileService;
import com.alok91340.gethired.repository.*;

/**
 * @author alok91340
 *
 */
@Service
public class UserProfileServiceImpl implements UserProfileService{
	
	
	
	@Autowired
	private UserProfileRepository userProfileRepo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ImageRepository imageRepository;
	
			

	@Override
	public UserProfileDto getUserProfile(Long userId) {
		User user=this.userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user",(long)0));
		UserProfile userProfile=userProfileRepo.findUserProfileByUser(user);
		UserProfileDto userProfileDto= new UserProfileDto();
		userProfileDto.setId(userProfile.getId());
		userProfileDto.setAbout(userProfile.getAbout());
		
		return userProfileDto;
	}

	@Override
	public List<UserProfileResponse> getUserProfiles(String search,int pageNo, int pageSize, String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		if (!search.isEmpty()) {
	        // If a search term is provided, use it for filtering
	        Page<UserProfile> userProfiles=userProfileRepo.searchUserProfiles(search, pageable);
	        List<UserProfile> userProfileResult= userProfiles.getContent();
	        List<UserProfileResponse> result= userProfileResult.stream().map(userProfile->mapToUserProfileResponse(userProfile)).collect(Collectors.toList());
	        
	        return result;
	    } else {
	        // Otherwise, fetch user profiles without filtering
	        Page<UserProfile> userProfiles = userProfileRepo.findAll(pageable);
	        List<UserProfile> userProfileResult= userProfiles.getContent();
	        List<UserProfileResponse> result= userProfileResult.stream().map(userProfile->mapToUserProfileResponse(userProfile)).collect(Collectors.toList());

	        return result;
	    }
	}
	
	UserProfileDto mapToDto(UserProfile userProfile) {
		UserProfileDto userProfileDto= new UserProfileDto();
		userProfileDto.setId(userProfile.getId());
		userProfileDto.setAbout(userProfile.getAbout());
		return userProfileDto;
	}
	
	UserProfile mapToEntity(UserProfileDto userProfileDto) {
		UserProfile userProfile= new UserProfile();
		userProfile.setAbout(userProfileDto.getAbout());
		return userProfile;
		
		
		
	}


	@Override
	public UserProfileDto updateUserProfile(UserProfileDto userProfileDto, Long userProfileId) {
		UserProfile userProfile=this.userProfileRepo.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		userProfile.setAbout(userProfileDto.getAbout());
		userProfile.setUpdatedAt(LocalDateTime.now());
		userProfile.setUpdatedBy(userProfile.getUser().getEmail());
		UserProfile savedUserProfile=this.userProfileRepo.save(userProfile);
		return mapToDto(savedUserProfile);
	}

	@Override
	public List<UserProfileResponse> getUserProfiles(String search, String role, int pageNo, int pageSize, String sortBy,
			String sortDir) {
		
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		if (!search.isEmpty()) {
	        // If a search term is provided, use it for filtering
	        Page<UserProfile> userProfiles=userProfileRepo.searchUserProfiles(search,role, pageable);
	        List<UserProfile> userProfileResult= userProfiles.getContent();
	        List<UserProfileResponse> result= userProfileResult.stream().map(userProfile->mapToUserProfileResponse(userProfile)).collect(Collectors.toList());
	        
	        return result;
	        
		} else {
	        // Otherwise, fetch user profiles without filtering
	        Page<UserProfile> userProfiles = userProfileRepo.findAll(pageable);
	        List<UserProfile> userProfileResult= userProfiles.getContent();
	        List<UserProfileResponse> result= userProfileResult.stream().map(userProfile->mapToUserProfileResponse(userProfile)).collect(Collectors.toList());

	        return result;

	    }
	}

	/**
	 * @param userProfile
	 * @return
	 */
	private UserProfileResponse mapToUserProfileResponse(UserProfile userProfile) {
		
		Image image = this.imageRepository.findImageByUser(userProfile.getUser())	;	
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		userProfileResponse.setId(userProfile.getUser().getId());
		userProfileResponse.setName(userProfile.getUser().getName());
		userProfileResponse.setUsername(userProfile.getUser().getUsername());
		userProfileResponse.setEmail(userProfile.getUser().getEmail());
		userProfileResponse.setGender(userProfile.getUser().getGender());
		userProfileResponse.setBirthdate(userProfile.getUser().getBirthdate());
		userProfileResponse.setCurrentOccupation(userProfile.getUser().getCurrentOccupation());
		userProfileResponse.setHeadline(userProfile.getUser().getHeadline());
		userProfileResponse.setIsRecuriter(userProfile.getUser().getIsRecuriter());
		userProfileResponse.setPhone(userProfile.getUser().getPhone());
		userProfileResponse.setStatus(false);
		userProfileResponse.setId(userProfile.getUser().getId());
		userProfileResponse.setAbout(userProfile.getAbout());
//		String image=userProfile.getUser().getImage();
		userProfileResponse.setImageData((image!=null)? encodeImage(image.getData()):"");

	
		return userProfileResponse;
		
	}
	String encodeImage(byte[] imageBytes) {
	    return Base64.getEncoder().encodeToString(imageBytes);
	}


}
