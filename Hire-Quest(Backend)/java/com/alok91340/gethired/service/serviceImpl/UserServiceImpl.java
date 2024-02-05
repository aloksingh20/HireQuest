/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.RegisterDto;
import com.alok91340.gethired.dto.UserDto;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.NotificationPreferenceRepository;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.entities.NotificationPreference;

/**
 * @author alok91340
 *
 */
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private NotificationPreferenceRepository notificationPreferenceRepository;
	
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
	
	@Override
	public UserDto createUser(RegisterDto registerDto) {
	
		User user=new User();
		mapToEntity(user,registerDto);
		User savedUser=userRepo.save(user);
		
		createNotificationPreference(savedUser.getId());
		
		UserProfile userProfile=new UserProfile();
		userProfile.setUser(savedUser);
		userProfile.setCreatedAt(LocalDateTime.now());
		this.userProfileRepository.save(userProfile);
		
		return mapToDto(savedUser);
	}
	
	public UserDto createUserWithGoogleSignIn(Payload payload) {
		
		String email = payload.getEmail();
        String name = (String) payload.get("name");
        String userName=email.split("@")[0];
        
        User newUser= new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setUsername(userName);
        
        String hashedPassword = bCryptPasswordEncoder.encode(userName);
        newUser.setPassword(hashedPassword);
        newUser.setCreatedAt(LocalDateTime.now());
        User savedUser=userRepo.save(newUser);
		createNotificationPreference(savedUser.getId());
		UserProfile userProfile=new UserProfile();
		userProfile.setUser(savedUser);
		userProfile.setCreatedAt(LocalDateTime.now());
		this.userProfileRepository.save(userProfile);
		
		return mapToDto(savedUser);
		
	}

	

	@Override
	public UserDto getUser(Long userId)throws ResourceNotFoundException {
		User user=userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user not available",(long)0));
		
		return mapToDto(user);
	}

	@Override
	public List<UserDto> getAllUser(int pageNo, int pageSize, String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> users = userRepo.findAll(pageable);
        
        List<User> userList= users.getContent();
        List<UserDto> userDtoList = userList.stream()
                .map(user -> mapToDto(user))
                .collect(Collectors.toList());
		return userDtoList;
	}


	@Override
	public UserDto updateUser(Long userId, UserDto userDto) {
	    User user = userRepo.findById(userId)
	                        .orElseThrow(() -> new ResourceNotFoundException("user", (long)0));
	    
	    user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setUsername(userDto.getUsername());
		user.setCreatedAt(LocalDateTime.now());
		user.setCreatedBy(userDto.getUsername());
		user.setBirthdate(userDto.getBirthdate());
		user.setHeadline(userDto.getHeadline());
		user.setStatus(userDto.isStatus());
		user.setPhone(userDto.getPhone());
		user.setIsRecuriter(userDto.getIsRecuriter());
		user.setGender(userDto.getGender());
		user.setCurrentOccupation(userDto.getCurrentOccupation());

	    user.setUpdatedAt(LocalDateTime.now());
	    user.setUpdatedBy(userDto.getUsername());

	    User savedUser = userRepo.save(user);
	    UserDto savedUserDto = mapToDto(savedUser);
	    return savedUserDto;
	}


	@Override
	public String deleteUser(Long userId) {
		User user= this.userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("user",(long)0));
		this.userRepo.delete(user);
		return null;
	}
	
	/**
	 * @param user
	 * @param userDto
	 */
	public User mapToEntity(User user,RegisterDto registerDto) {
		user.setName(registerDto.getName());
		user.setEmail(registerDto.getEmail());
		String hashedPassword = bCryptPasswordEncoder.encode(registerDto.getPassword());
		user.setPassword(hashedPassword);
		user.setUsername(registerDto.getUsername());
		user.setCreatedAt(LocalDateTime.now());
		user.setCreatedBy(registerDto.getUsername());
		
		return user;
		
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

	@Override
	public UserDto updatePassword(String email,String password) {
		User user=this.userRepo.findUserByEmail(email);
		String hashedPassword = bCryptPasswordEncoder.encode(password);
		user.setPassword(hashedPassword);
		User updatedUser=this.userRepo.save(user);
		
		return mapToDto(updatedUser);
	}
	
	/**
	 * 
	 */
	private void createNotificationPreference(Long userId) {
		
		
		String[] type={"message","chat-request","profile-visit","meeting-schedule","general"};
		
		for(String str:type) {
			NotificationPreference notificationPreference=new NotificationPreference();
			
			notificationPreference.setMuted(false);
			notificationPreference.setUserId(userId);
			notificationPreference.setNotificationType(str);
			
			this.notificationPreferenceRepository.save(notificationPreference);
			
		
		}
		
		
		
	}
}
