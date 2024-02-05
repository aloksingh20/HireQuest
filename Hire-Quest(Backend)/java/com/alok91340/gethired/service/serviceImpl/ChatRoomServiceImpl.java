/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.ChatRoomResponse;
import com.alok91340.gethired.dto.UserChattingInfoDto;
import com.alok91340.gethired.entities.ChatRoom;
import com.alok91340.gethired.entities.Image;
import com.alok91340.gethired.entities.Message;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.entities.UserChatRoom;
import com.alok91340.gethired.entities.UserChatRoomId;
import com.alok91340.gethired.repository.ChatRoomRepository;
import com.alok91340.gethired.repository.ImageRepository;
import com.alok91340.gethired.repository.MessageRepository;
import com.alok91340.gethired.dto.NotificationRequest;
import com.alok91340.gethired.repository.UserChatRoomRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.service.ChatRoomService;
import com.alok91340.gethired.service.NotificationService;
import com.alok91340.gethired.utils.NotificationType;

/**
 * @author aloksingh
 *
 */
@Service
public class ChatRoomServiceImpl implements ChatRoomService{
	
	@Autowired
	private UserChatRoomRepository userChatRoomRepository;
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private ImageRepository imageRepository;
	

	@Override
	public List<ChatRoomResponse> getChatRoomsAndUnseenMessageCount(Long senderId, Long receiverId) {
		
//		User sender= this.userRepository.findById(senderUsername).orElseThrow();
//		User receiver = this.userRepository.findById(receiverUsername).orElseThrow();
//		
//		List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderOrReceiverAndIsRequest(sender, receiver, false);
//
//	    List<ChatRoomDto> chatRoomDTOs = new ArrayList<>();
//
//	    for (ChatRoom chatRoom : chatRooms) {
//	        int unSeenMessageCount = messageRepository.countByRoomIdAndReceiverUsernameAndSeen(chatRoom.getId(), receiver.getUsername(), false);
//
//	        Message lastMessage = messageRepository.findTopByRoomIdOrderByTimestampDesc(chatRoom.getId());
//
//	        ChatRoomDto chatRoomDTO = new ChatRoomDto();
//	        chatRoomDTO.setId(chatRoom.getId());
//	        chatRoomDTO.setUnSeenMessageCount(unSeenMessageCount);
//	        chatRoomDTO.setIsRequest(chatRoom.isRequest());
//	        chatRoomDTO.setLastMessage(lastMessage);
//
//	        chatRoomDTOs.add(chatRoomDTO);
//	    }
//
//	    return chatRoomDTOs;
		return null;
	}
	

	@Override
	public void sendChatRequest(Long senderId, Long receiverId, Message message) {
		
		User user1= this.userRepository.findById(senderId).orElseThrow();
		User user2 = this.userRepository.findById(receiverId).orElseThrow();
		
		// Check if there's an existing chat room between user1 and user2
	    Optional<ChatRoom> existingChatRoom = chatRoomRepository.findChatRoomByUsers(user1, user2);

	    ChatRoom chatRoom;
	    ChatRoom savedChatRoom;
	    if (existingChatRoom.isPresent() && !existingChatRoom.get().isGroup()) {
	        // Use the existing chat room
	        chatRoom = existingChatRoom.get();
	        chatRoom.setDeleted(false);
	        savedChatRoom = chatRoomRepository.save(chatRoom);
	    } else {
	        // Create a new chat room
	        chatRoom = new ChatRoom();
	        chatRoom.setDeleted(false);
	        chatRoom.setGroup(false);
	        chatRoom.setRequest(true); // Set it as a request
	        chatRoom.setTimeStamp(LocalDateTime.now());
	        chatRoomRepository.save(chatRoom);

	        // Add users to the chat room
	        chatRoom.getUsers().add(user1);
	        chatRoom.getUsers().add(user2);
	        
	        // Save the chat room with users

	        savedChatRoom =chatRoomRepository.save(chatRoom);
	        
	        Message requestMessage=new Message();
	        requestMessage.setContent(message.getContent());
	        requestMessage.setDelivered(false);
	        requestMessage.setReceiverId(receiverId);
	        requestMessage.setSenderId(senderId);
	        requestMessage.setRoomId(savedChatRoom.getId() );
	        requestMessage.setSeen(false);
	        requestMessage.setTimestamp(message.getTimestamp());	    
	        
	        messageRepository.save(requestMessage);
	        
	        
	        
	    }
	    if(savedChatRoom!=null) {
	    	
		
		UserChatRoomId userChatRoomId1 = new UserChatRoomId();
		userChatRoomId1.setUserId(user1.getId()); // Set the user's ID
		userChatRoomId1.setChatRoomId(chatRoom.getId());

		
		UserChatRoomId userChatRoomId2 = new UserChatRoomId();
		userChatRoomId2.setUserId(user2.getId()); // Set the user's ID
		userChatRoomId2.setChatRoomId(chatRoom.getId());

		
		// Create UserChatRoom entries for User1 and User2
		UserChatRoom userChatRoom1 = new UserChatRoom();
		userChatRoom1.setId(userChatRoomId1);
		userChatRoom1.setChatRoom(chatRoom);
		userChatRoom1.setUser(user1);
		userChatRoom1.setRequestSender(true);
		
		UserChatRoom userChatRoom2 = new UserChatRoom();
		userChatRoom2.setId(userChatRoomId2);
		userChatRoom2.setChatRoom(chatRoom);
		userChatRoom2.setUser(user2);
		userChatRoom2.setRequestSender(false);
		
		
		userChatRoomRepository.save(userChatRoom1);
		userChatRoomRepository.save(userChatRoom2);

		NotificationRequest request= new NotificationRequest();
		request.setBody("Send you chat request");
		request.setNotificationType(NotificationType.REQUEST);
		request.setReceiverUsername(user2.getUsername());
		request.setSenderUsername(user1.getUsername());
		request.setTitle("Chat Request");
		
		this.notificationService.saveNotification(request);
		
	    }
		
		return ;
	}


	@Override
	public void deleteChatRoom(Long chatRoomId) {
		
		ChatRoom chatRoom= this.chatRoomRepository.findById(chatRoomId).orElseThrow();
		
		
		
	}

	

	@Override
	public List<ChatRoomResponse> findUserChattingWith(Long userId) {
		
		User user=this.userRepository.findById(userId).orElse(null);
		
//		List<User> usersChattingWith = new ArrayList<>();
	    List<ChatRoomResponse> chatList= new ArrayList<>();
	    
	    List<Long> chatRoomIds=this.userChatRoomRepository.findChatRoomIdsByUser(user);
		
	    List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomByChatRoomIdsAndIsRequestSenderAndIsNotDeletedAndNotSameUser(userId,chatRoomIds );

	    for (UserChatRoom userChatRoom : userChatRooms) {
	        // Get the other user from the UserChatRoom
	        User otherUser = userChatRoom.getChatRoom().getUsers()
	                .stream()
	                .filter(u -> !u.equals(user)) // Filter out the original user
	                .findFirst()
	                .orElse(null);

	        if (otherUser != null) {
	        	Image image = this.imageRepository.findImageByUser(otherUser);
	        	ChatRoomResponse chatRoomResponse=new ChatRoomResponse();
	        	chatRoomResponse.setId(userChatRoom.getChatRoom().getId());
	        	chatRoomResponse.setReceiver(otherUser);
	        	
	        	Pageable pageable = PageRequest.of(0, 1, Sort.by("timestamp").descending());
	        	Message latestMessage = this.messageRepository.findLatestMessage(userChatRoom.getChatRoom().getId());

	        	chatRoomResponse.setIsRequest(userChatRoom.getChatRoom().isRequest());
	        	if(latestMessage!=null) {
	        		chatRoomResponse.setLastMessage(latestMessage);
	        		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	                // Parse the string to LocalDateTime
	                LocalDateTime localDateTime = LocalDateTime.parse(latestMessage.getTimestamp().substring(0, 23), formatter);

	        		chatRoomResponse.setTimeStamp(localDateTime);
	        	}
	        	else {
	        		chatRoomResponse.setTimeStamp(userChatRoom.getChatRoom().getTimeStamp());
	        		
	        	}
	        	chatRoomResponse.setImage(encodeImageToString(image.getData()));
	        			
	            chatList.add(chatRoomResponse);
	        }
	    }
	    
	   
		return chatList;
	}

	@Override
	public void acceptChatRequest(Long chatRoomId) {
		// Find the chat room (assuming you have the chat room ID)
		
		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
		
		UserChatRoom userChatRoom=this.userChatRoomRepository.findByChatRoomAndIsRequestSender(chatRoom, true);
	
		

		if (chatRoom != null && chatRoom.isRequest()) {
		    // Set it as an active chat
		    chatRoom.setRequest(false);
		    chatRoomRepository.save(chatRoom);

		    // Update UserChatRoom entries if needed (e.g., mark as active)
		}
		
	}

	@Override
	public void rejectChatRequest(Long chatRoomId) {
		// Find the chat room (assuming you have the chat room ID)
		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);

		if (chatRoom != null && chatRoom.isRequest()) {
		    // Delete the chat room and associated entries
			
			chatRoom.setDeleted(true);
			chatRoomRepository.save(chatRoom);
			
//		    // Delete UserChatRoom entries if needed
//		    List<UserChatRoom> userChatRooms=this.userChatRoomRepository.findByChatRoom(chatRoom);
//		    for(UserChatRoom  userChatRoom:userChatRooms) {
//		    	
//		    	this.userChatRoomRepository.delete(userChatRoom);
//		    }
		}

		
	}


	@Override
	public UserChattingInfoDto findUserChattingInfo(Long senderId, Long receiverId) {
		User user1=this.userRepository.findById(senderId).orElse(null);
		User user2=this.userRepository.findById(receiverId).orElse(null);
		Optional<ChatRoom> chatRoom = this.chatRoomRepository.findChatRoomByUsers(user1, user2);
		UserChattingInfoDto userChattingInfoDto = new UserChattingInfoDto();
		if(chatRoom.isPresent()) {
			
			UserChatRoom userChatRoom = this.userChatRoomRepository.findByChatRoomAndUser(chatRoom.get(), user2);
			Image image = this.imageRepository.findImageByUser(userChatRoom.getUser());
			userChattingInfoDto.setRoomId(chatRoom.get().getId());
			userChattingInfoDto.setIsRequested(chatRoom.get().isRequest());
			userChattingInfoDto.setUsername(userChatRoom.getUser().getName());
			userChattingInfoDto.setIsSender(userChatRoom.isRequestSender());
			userChattingInfoDto.setImage(encodeImageToString(image.getData()));
			
			
		}
		return userChattingInfoDto;
	}
	
	String encodeImageToString(byte[] imageData) {
		return Base64.getEncoder().encodeToString(imageData);
	}

}
