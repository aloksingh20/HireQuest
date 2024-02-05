/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.ChatUser;
import com.alok91340.gethired.entities.ChatRoom;
import com.alok91340.gethired.entities.Message;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.repository.ChatRoomRepository;
import com.alok91340.gethired.repository.MessageRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.service.ChatService;

/**
 * @author aloksingh
 *
 */
@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private UserRepository userRepository;


	@Override
	public Message saveMessage(Message message) {
        Message savedMessage=messageRepository.save(message);
        return savedMessage;
    }

	@Override
	public List<Message> getMessages(Long senderId, Long receiverId,String timeStamp) {
	    // Assuming you have a MessageRepository
	    List<Message> messages = messageRepository.fetchAllMessage(senderId, receiverId, timeStamp);
	    return messages;
	}

	


}
