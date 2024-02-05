/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.dto.UserProfileDto;
import com.alok91340.gethired.dto.UserProfileResponse;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.service.UserProfileService;
import com.alok91340.gethired.utils.Constant;

/**
 * @author alok91340
 *
 */

@RestController
@RequestMapping("api/hireQuest")
public class UserProfileController {
	static String path="http://localhost:8080/api/gethired";
	
	@Autowired
	private UserProfileService userProfileService;
    private ObjectMapper objectMapper = new ObjectMapper(); // Create the ObjectMapper instance

	
//	get all users
	@GetMapping("/userProfiles")
	public ResponseEntity<List<String>> getUsers(
			@RequestParam(value = "query") String search,
			@RequestParam(value="role", required=false) String role,
			@RequestParam(value = "pageNo", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = Constant.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = Constant.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = Constant.DEFAULT_SORT_DIRECTION) String sortDir
			){
		
		
		List<UserProfileResponse> userProfiles=null;
		if(role==null||role.length()==0) {
			userProfiles = userProfileService.getUserProfiles(search,pageNo,pageSize,sortBy,sortDir);
		}else {
			userProfiles = this.userProfileService.getUserProfiles(search, role, pageNo, pageSize, sortBy, sortDir);
		}
		
		List<String> jsonResponse=null;
		jsonResponse = userProfiles.stream().map(userProfile->{
			try {
				return objectMapper.writeValueAsString(userProfile);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}).collect(Collectors.toList());
		return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);

		
	}
	
//	update user
	@PutMapping("/{userProfileId}/update-user-profile")
	public ResponseEntity<UserProfileDto> getUser(@RequestBody UserProfileDto userProfileDto,@PathVariable Long userProfileId){
		UserProfileDto result=this.userProfileService.updateUserProfile(userProfileDto, userProfileId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{userId}/get-user-profile")
	public ResponseEntity<UserProfileDto> getUserProfileDto(@PathVariable("userId") Long userId){
		UserProfileDto result=this.userProfileService.getUserProfile(userId);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	
}
