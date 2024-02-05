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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.dto.ChatRoomResponse;
import com.alok91340.gethired.dto.UserChattingInfoDto;
import com.alok91340.gethired.entities.ChatRoom;
import com.alok91340.gethired.entities.Message;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.entities.UserChatRoom;
import com.alok91340.gethired.repository.ChatRoomRepository;
import com.alok91340.gethired.repository.UserChatRoomRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.service.ChatRoomService;
import com.alok91340.gethired.service.ChatService;

/**
 * @author aloksingh
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class ChatRoomController {
	
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	@Autowired 
	private ChatRoomService chatRoomService;
	
	@Autowired
	private UserChatRoomRepository userChatRoomRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@PostMapping("/{senderId}/send-chat-request")
	public ResponseEntity<Boolean> createChatRoom(@PathVariable Long senderId, @RequestParam Long receiverId,@RequestBody Message message){
		
		this.chatRoomService.sendChatRequest(senderId, receiverId ,message);
		
		return ResponseEntity.ok(true);
		
	}
	
	@PutMapping("/accept-chatRequest")
	public ResponseEntity<String> acceptChatRequest(@RequestParam("chatRoomId") Long chatRoomId){
		this.chatRoomService.acceptChatRequest(chatRoomId);
		return ResponseEntity.ok("accepted");
		
	}
	
	@DeleteMapping("/delete-chatRequest")
	public ResponseEntity<String> deleteChatRequest(@RequestParam("chatRoomId") Long chatRoomId){
		this.chatRoomService.deleteChatRoom(chatRoomId);
		return ResponseEntity.ok("deleted");
		
	}
	
	@GetMapping("/{userId}/get-pendingChatRequest")
	public ResponseEntity<List<User>> getPendingChatRequestsForUser(@PathVariable Long userId) {
		
		User user=this.userRepository.findById(userId).orElseThrow();
		
		List<Long> chatRoom=this.userChatRoomRepository.findChatRoomIdsByUser(user);
		
		
		List<User> userChatRooms=userChatRoomRepository.findUserChatRoomsByChatroomIdsAndIsRequestSenderAndNotSameUser(userId,chatRoom);
		return ResponseEntity.ok(userChatRooms);
	}
	
	@GetMapping("/{userId}/get-chatting-list")
	public ResponseEntity<List<ChatRoomResponse>> getChattingList(@PathVariable Long userId){
		List<ChatRoomResponse> chatList=this.chatRoomService.findUserChattingWith(userId);
		return ResponseEntity.ok(chatList);
	}
	
	@GetMapping("{senderId}/{receiverId}/get-user-info")
	public ResponseEntity<UserChattingInfoDto> getUserInfo(@PathVariable Long senderId, @PathVariable Long receiverId){
		
		UserChattingInfoDto userInfo=this.chatRoomService.findUserChattingInfo(senderId, receiverId);
		return ResponseEntity.ok(userInfo);
	}


}
