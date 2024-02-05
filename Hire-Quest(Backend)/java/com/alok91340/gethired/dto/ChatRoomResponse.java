/**
 * 
 */
package com.alok91340.gethired.dto;

import java.time.LocalDateTime;

import com.alok91340.gethired.entities.Message;
import com.alok91340.gethired.entities.User;

import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Data
public class ChatRoomResponse {

    private Long id;
    private User receiver;
    private int unSeenMessageCount;
    private Boolean isRequest;
    private Message lastMessage;
    private LocalDateTime timeStamp;
    private Boolean isRequested;
    private String image;
}
