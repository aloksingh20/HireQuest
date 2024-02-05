/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.RegisterDto;
import com.alok91340.gethired.dto.UserDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

/**
 * @author alok91340
 *
 */

public interface UserService {
	
	UserDto createUser(RegisterDto registerDto);
	
	UserDto getUser(Long userId);
	
	List<UserDto> getAllUser(int pageNo, int pageSize, String sortBy, String sortDir);
	
	UserDto updateUser(Long userId, UserDto userDto);
	
	String deleteUser(Long userId);
	
	UserDto updatePassword(String email,String password);
	
	UserDto createUserWithGoogleSignIn(Payload payload);
	
	
}
