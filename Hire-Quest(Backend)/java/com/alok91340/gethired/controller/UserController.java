/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.utils.Constant;
import com.alok91340.gethired.utils.GoogleIdTokenVerifierUtil;
import com.alok91340.gethired.utils.isAuthenticatedAsAdminOrUser;
import com.alok91340.gethired.websocket.PresenceService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import jakarta.servlet.http.HttpSession;

import com.alok91340.gethired.dto.GoogleSignInDto;
import com.alok91340.gethired.dto.LoginDto;
import com.alok91340.gethired.dto.RegisterDto;
import com.alok91340.gethired.dto.RegistrationResponse;
import com.alok91340.gethired.dto.UserDto;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.security.JwtAuthResponse;
import com.alok91340.gethired.security.JwtTokenProvider;
import com.alok91340.gethired.service.UserService;
import com.alok91340.gethired.service.serviceImpl.PresenceServiceImpl;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class UserController {
	
	private final UserService userService;
    private final AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PresenceServiceImpl presenceService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    HttpSession session;
    
    

    public UserController(UserService userService, AuthenticationManager authenticationManager
                         ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
	
    @Autowired
    private UserRepository userRepo;
//	get user by id
    @isAuthenticatedAsAdminOrUser
	@GetMapping("/{userId}/get-user")
	public ResponseEntity<?> getUser(@AuthenticationPrincipal Authentication authentication,@PathVariable Long userId){
		
//			UserDto userDto= userService.getUser(userId);
		UserDto user= this.userService.getUser(userId);
		
			return new ResponseEntity<>(user,HttpStatus.OK);
		
	}
	
//	get all users
	@GetMapping("/get-users")
	public ResponseEntity<List<UserDto>> getUsers(
			@RequestParam(value = "pageNo", defaultValue = Constant.DEFAULT_PAGE_NUMBER) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = Constant.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = Constant.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = Constant.DEFAULT_SORT_DIRECTION) String sortDir
			){
		
		return ResponseEntity.ok(userService.getAllUser(pageNo,pageSize,sortBy,sortDir));
	}
	
//	create user
	@PostMapping("/create-user")
	public ResponseEntity<RegistrationResponse> createUser(@RequestBody RegisterDto registerDto){

		RegistrationResponse response=new RegistrationResponse();
		// check for userName exists in DB
        if (userRepo.existsByUsername(registerDto.getUsername())) {
        	response.setMessage("Username already exists");
            return new ResponseEntity<>(response,
                    HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByEmail(registerDto.getEmail())) {
        	response.setMessage("Email already exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
		UserDto result=userService.createUser(registerDto);
		response.setMessage("User is successfully registered");
		return new ResponseEntity<>(response,HttpStatus.OK);
		
	}
	
	@PostMapping("/google-sign-in")
	public ResponseEntity<JwtAuthResponse> signInWithGoogle(@RequestBody GoogleSignInDto googleSignInDto){
		
		User user=this.userRepo.findUserByEmail(googleSignInDto.getGoogleSignInId());
		System.out.println("  `````````````````````````````     "    +user);	 
		if(user==null) {
			
			Payload payload=GoogleIdTokenVerifierUtil.verifyAndExtractEmail(googleSignInDto.getGoogleSignInId());
			
			if(payload!=null) {
				System.out.println("  `````````````````````````````     "    +payload);	            
				UserDto userDto= this.userService.createUserWithGoogleSignIn(payload);
	            
			}
			return null;
			
			
		}else {
			
			return null;
		}
		
	}
	
//	authenticate
	@PostMapping(value="/user-login", produces = "application/json")
    public ResponseEntity<JwtAuthResponse> loginUser(@RequestBody LoginDto loginDto) throws Exception {

		if(!loginDto.getGoogleIdToken().isEmpty()) {
			
//			String userEmailFromGoogle=GoogleIdTokenVerifierUtil.verifyAndExtractEmail(loginDto.getGoogleIdToken());
			
			User optionalUser = userRepo.findUserByEmail(loginDto.getGoogleIdToken());
            User user;
//            if (optionalUser.isPresent()) {
//                user = optionalUser.get();
//            } else {
//                // Create a new user if not found
//                user = new User();
//                user.setUsername(userEmailFromGoogle);
//                // Set other user attributes as needed
//                userRepo.save(user);
//            }
            user=optionalUser;

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = tokenProvider.generateToken(userDetails);
            user.setFcmToken(loginDto.getFcmToken());
            user.setStatus(true);
            this.userRepo.save(user);
            presenceService.setUserOnline(Long.toString(user.getId()));
            return ResponseEntity.ok(new JwtAuthResponse(token));
            
		}
		else {
		
        authenticate(loginDto.getUsername(),

                loginDto.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginDto.getUsername());

        final String token = tokenProvider.generateToken(userDetails);
        
        User user=this.userRepo.findByUsername(loginDto.getUsername()).orElseThrow(()-> new ResourceNotFoundException("user",(long)0));
        user.setFcmToken(loginDto.getFcmToken());
        user.setStatus(true);
        this.userRepo.save(user);
        presenceService.setUserOnline(Long.toString(user.getId()));
        return ResponseEntity.ok(new JwtAuthResponse(token));
		}
    }

	
	
//	update user
	@PutMapping("/{userId}/update-user")
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,@PathVariable Long userId){
		UserDto updatedUserDto= userService.updateUser(userId, userDto);
		return ResponseEntity.ok(updatedUserDto);
	}
	
//	delete user
	@DeleteMapping("/{userId}/delete-user")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId){
		userService.deleteUser(userId);
		return ResponseEntity.ok("Deleted user with id:"+userId);
	}
	
	
	@GetMapping("/{username}/check-username")
	public ResponseEntity<Boolean> checkUsername(@PathVariable String username){
		boolean isAvailable=this.userRepo.existsByUsername(username);
		return new ResponseEntity<>(isAvailable,HttpStatus.OK);
	}
	
	@GetMapping("/get-userBy-username")
	private ResponseEntity<UserDto> getUser(@RequestParam("username") String username){
		User user=this.userRepo.findUserByUsername(username);
		UserDto userDto=this.userService.getUser(user.getId());
		return ResponseEntity.ok(userDto);
		
	}
	
	@GetMapping("/get-userBy-userId")
	private ResponseEntity<UserDto> getUser(@RequestParam("userId") Long userId){
		User user=this.userRepo.findById(userId).orElseThrow();
		UserDto userDto=this.userService.getUser(user.getId());
		return ResponseEntity.ok(userDto);
		
	}
	@GetMapping("/{email}/check-email")
	public ResponseEntity<Boolean> checkEmail(@PathVariable String email){
		boolean isAvailable=this.userRepo.existsByEmail(email);
		return new ResponseEntity<>(isAvailable,HttpStatus.OK);
	}
	
	@GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userRepo.searchUsers(query);
        return ResponseEntity.ok(users);
    }
	
	@GetMapping("/{token}/get-user-info")
	public ResponseEntity<UserDto> getUsername(@PathVariable String token){
		String username= this.tokenProvider.getUserNameFromToken(token);
		User user=this.userRepo.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("user",(long)0));
		UserDto userDto=this.mapToDto(user);
		return new ResponseEntity<>(userDto,HttpStatus.OK);
	}
	
	@PutMapping("/{email}/{password}/change-password")
	public ResponseEntity<UserDto> changePassword(@PathVariable String email,@PathVariable String password){
		UserDto userDto=this.userService.updatePassword(email, email);
		return new ResponseEntity<>(userDto,HttpStatus.OK);
	}
	
	
	@PostMapping("/{userId}/userPresence-offline")
	public ResponseEntity<String> offlineUserPresence(@PathVariable Long userId){
		presenceService.setUserOffline(Long.toString(userId));
		
		return ResponseEntity.ok("offline");
	}
	
	
	@PutMapping("/user-logout")
	public ResponseEntity<Boolean> logoutUser(@RequestParam(value="username") String username){
		
		
		User user = this.userRepo.findByUsername(username).orElseThrow();
		user.setStatus(false);
		user.setFcmToken(null);
		this.userRepo.save(user);
		
		
		return ResponseEntity.ok(true);
		
		
	}
	
	@GetMapping("{username}/get-fcm-token")
	public ResponseEntity<Boolean> getFcmToken(@PathVariable String username){
		Optional<User> user=this.userRepo.findByUsername(username);
		if(user.get().getFcmToken() != null) {
			return ResponseEntity.ok(true);
		}else {
			return ResponseEntity.ok(false);
		}
	}
	
	@PutMapping("/update-fcm-token")
	public ResponseEntity<Boolean> updateFcmToken(@RequestParam("username") String username, @RequestBody String token){
		Optional<User> user=this.userRepo.findByUsername(username);
		if(user.get().getFcmToken() != null) {
			
			user.get().setFcmToken(token);
			this.userRepo.save(user.get());
			
			return ResponseEntity.ok(true);
		}else {
			return ResponseEntity.ok(false);
		}
	}
	
	
public UserDto mapToDto(User user) {
		
	UserDto userDto= new UserDto();
	userDto.setId(user.getId());
	userDto.setName(user.getName());
	userDto.setEmail(user.getEmail());
	userDto.setUsername(user.getUsername());
	userDto.setBirthdate(user.getBirthdate());
	userDto.setHeadline(user.getHeadline());
	userDto.setStatus(user.isStatus());
	userDto.setPhone(user.getPhone());
	userDto.setGender(user.getGender());
	userDto.setIsRecuriter(user.getIsRecuriter());
	userDto.setCurrentOccupation(user.getCurrentOccupation());
		return userDto;
	}

private void authenticate(String username, String password) throws

Exception {

    Objects.requireNonNull(username);

    Objects.requireNonNull(password);

    try {

        authenticationManager.authenticate(new

        UsernamePasswordAuthenticationToken(username, password));
        System.out.println("hello");
    } catch (DisabledException e) {

        throw new Exception("USER_DISABLED", e);

    } catch (BadCredentialsException e) {

        throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
	
}
