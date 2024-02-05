/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.LanguageService;

/**
 * @author alok91340
 *
 */
@Service
public class LanguageServiceImpl implements LanguageService{
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	

	@Override
	public List<String> addLanguage(List<String> languages, Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		userProfile.setLanguages(languages);
		UserProfile updatedUserProfile=this.userProfileRepository.save(userProfile);
		return updatedUserProfile.getLanguages();
		
	}
	
	@Override
	public List<String> getAllLanguage(Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("User-Profile",userProfileId));
		List<String> languages=userProfile.getLanguages();
		return languages;
	}
	
	

}
