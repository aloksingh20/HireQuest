/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.service.BookmarkedProfileService;

/**
 * @author aloksingh
 *
 */
//@RestController
//@RequestMapping("api/hireQuest")
public class BookmarkedProfileController {
//	
//	@Autowired
//	private BookmarkedProfileService bookmarkedService;
//	
//	@PutMapping("/{hrId}/add-bookmark-profile")
//	public ResponseEntity<Boolean> addBookmarkedProfile(@PathVariable Long hrId, @RequestParam("userProfileId") Long userProfileId){
//		
//		Boolean isSaved=this.bookmarkedService.addProfile(hrId, userProfileId);
//		return ResponseEntity.ok(isSaved);
//	}	
//	
//	@DeleteMapping("/{hrId}/remove-bookmark-profile")
//	public ResponseEntity<Boolean> removeBookmarkedProfile(@PathVariable Long hrId, @RequestParam("userProfileId") Long userProfileId){
//		
//		Boolean isRemoved=this.bookmarkedService.removeProfile(hrId, userProfileId);
//		return ResponseEntity.ok(isRemoved);
//		
//	}
//	@GetMapping("/get-bookmark-profile")
//	public ResponseEntity<List<UserProfile>> getAllBookMarkedProfile(@RequestParam("hrId") Long hrId){
//		List<UserProfile> bookmarkedProfiles = this.bookmarkedService.getAllBokkmarkedProfile(hrId);
//		return ResponseEntity.ok(bookmarkedProfiles);
//	}
	
}
