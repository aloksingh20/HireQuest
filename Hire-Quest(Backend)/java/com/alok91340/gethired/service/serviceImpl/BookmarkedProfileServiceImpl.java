/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.repository.BookmarkedProfileRepository;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.BookmarkedProfileService;
import com.alok91340.gethired.entities.BookmarkedProfile;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author aloksingh
 *
 */
//@Service
public class BookmarkedProfileServiceImpl {
//implements BookmarkedProfileService{
//	
//	@Autowired
//	private BookmarkedProfileRepository bookmarkedProfileRepository;
//	
//	@Autowired
//	private UserProfileRepository userProfileRepository ;
//	
//
//	@Override
//	public Boolean addProfile(Long hrId, Long userProfileId) {
//		Optional<UserProfile> userProfile = this.userProfileRepository.findById(userProfileId);
//		if(userProfile.isEmpty()) {
//			return false;
//		}
//		else {
//			BookmarkedProfile bookmarkedProfile=this.bookmarkedProfileRepository.findByHrId(hrId);
//			if(bookmarkedProfile!=null) {
//				
//				bookmarkedProfile.getUsers().put(userProfileId,userProfile.get());
//				BookmarkedProfile savedProfile=this.bookmarkedProfileRepository.save(bookmarkedProfile);
//				return savedProfile!=null;
//				
//			}else {
//				
//				BookmarkedProfile newBookmarkedProfile= new BookmarkedProfile();
//				newBookmarkedProfile.setHrId(hrId);
//				newBookmarkedProfile.getUsers().put(userProfileId,userProfile.get());
//				BookmarkedProfile savedProfile=this.bookmarkedProfileRepository.save(newBookmarkedProfile);
//				return savedProfile!=null;
//			}
//		}
//	}
//
//	@Override
//	public Boolean removeProfile(Long hrId, Long userProfileId) {
//		BookmarkedProfile bookmarkedProfile=this.bookmarkedProfileRepository.findByHrId(hrId);
//		bookmarkedProfile.getUsers().remove(userProfileId);
//		BookmarkedProfile updatedProfile = this.bookmarkedProfileRepository.save(bookmarkedProfile);
//		return updatedProfile!=null;
//		
//	}
//
//	@Override
//	public List<UserProfile> getAllBokkmarkedProfile(Long hrId) {
//		
//		BookmarkedProfile bookmarkedProfile=this.bookmarkedProfileRepository.findByHrId(hrId);
//		return bookmarkedProfile.getUserProfiles();
//		
//	}

}
