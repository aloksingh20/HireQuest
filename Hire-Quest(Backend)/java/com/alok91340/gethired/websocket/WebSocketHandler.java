/**
 * 
 */
package com.alok91340.gethired.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author aloksingh
 *
 */
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.alok91340.gethired.entities.Message;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.dto.NotificationRequest;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.MessageRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.security.JwtTokenProvider;
import com.alok91340.gethired.service.serviceImpl.FcmNotificationService;
@Component
public class WebSocketHandler extends TextWebSocketHandler {

	@Autowired
    private  MessageRepository messageRepository;
	
	@Autowired
    private  PresenceService presenceService;
	
	@Autowired
    private  JwtTokenProvider jwtTokenProvider;
	
	@Autowired
    private  UserRepository userRepository;
	
	@Autowired
    private  FcmNotificationService fcmNotificationService;

    private final Map<Long, WebSocketSession> activeSessions = new HashMap<>();

    // Constructor

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = session.getUri().getPath().replace("/ws/", "");

        try {
            String userName = this.jwtTokenProvider.getUserNameFromToken(token);
            Optional<User> userOptional = this.userRepository.findByUsername(userName);

            if (userOptional.isPresent()) {
                Long userId = userOptional.get().getId();
                session.getAttributes().put("userId", userId);

                presenceService.addUserSession(userId, session);
                activeSessions.put(userId, session);
            } else {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = getUserIdFromSession(session);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> messageData = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Object>>(){});
        Long receiverId = Long.valueOf(String.valueOf(messageData.get("receiverId")));
        String content = (String) messageData.get("content");
        Long roomId = Long.valueOf(String.valueOf(messageData.get("roomId")));
        Message messageEntity = new Message();
        messageEntity.setRoomId(roomId);
        messageEntity.setSenderId(senderId);
        messageEntity.setReceiverId(receiverId);
        messageEntity.setContent(content);
        messageEntity.setTimestamp((String) messageData.get("timestamp"));
        messageEntity.setSeen(false);
        
        Message savedMessage = this.messageRepository.save(messageEntity);

        Set<WebSocketSession> receiverSessions = presenceService.getSessionsForUser(receiverId);

        if (receiverSessions.isEmpty()) {
            User user = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("user", receiverId));
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new ResourceNotFoundException("user", senderId));

            NotificationRequest request = new NotificationRequest();
            request.setBody(content);
            request.setTitle("message");
            request.setNotificationType("message");
            request.setReceiverUsername(user.getUsername());    
            request.setSenderUsername(sender.getUsername());
            if(!(user.getFcmToken()!=null)&&!user.getFcmToken().isEmpty()) {
            	fcmNotificationService.sendNotification(user.getFcmToken(), request);
            }
            
        } else {
            for (WebSocketSession receiverSession : receiverSessions) {
                try {
                    if (receiverSession.isOpen()) {
                        String responseMessage = "{\"id\": " + 0 +
                        		", \"roomId\": \"" +roomId + "\"" +
                                ", \"senderId\": \"" + senderId + "\"" +
                                ", \"receiverId\": \"" + receiverId + "\"" +
                                ", \"content\": \"" + content + "\"" +
                                ", \"timestamp\": \"" + messageEntity.getTimestamp() + "\"" +
                                ", \"seen\": " + false +
                                "}";

                        receiverSession.sendMessage(new TextMessage(responseMessage));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        Message savedMessage = this.messageRepository.save(messageEntity);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        activeSessions.remove(userId);
    }

    private void broadcastPresenceUpdate(String senderUsername, boolean isOnline) {
        for (WebSocketSession session : activeSessions.values()) {
            try {
                if (session.isOpen()) {
                    String message = "{\"type\": \"presenceUpdate\", \"senderId\": " + senderUsername + ", \"isOnline\": " + isOnline + "}";
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        Object userIdObject = attributes.get("userId");

        if (userIdObject instanceof String) {
            try {
                return Long.parseLong((String) userIdObject);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (userIdObject instanceof Long) {
            return (Long) userIdObject;
        } else {
            return null;
        }
    }
}
