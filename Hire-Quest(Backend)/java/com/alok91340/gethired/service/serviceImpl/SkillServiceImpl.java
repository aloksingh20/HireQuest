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
import com.alok91340.gethired.service.SkillService;

/**
 * @author alok91340
 *
 */
@Service
public class SkillServiceImpl implements SkillService{
	
	@Autowired
	private UserProfileRepository userProfileRepository;

	@Override
	public List<String> addSkill(Long userProfileId, List<String> skills) {
		
		UserProfile userProfile=userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		userProfile.setSkills(skills);
		UserProfile updatedUserProfile=this.userProfileRepository.save(userProfile);
		
		return updatedUserProfile.getSkills();
	}
	
	
	@Override
	public List<String> getAllSkill(Long userProfileId){
		UserProfile userProfile=userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		
		return userProfile.getSkills();
	}
	

}
