/**
 * 
 */
package com.alok91340.gethired.entities;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.HashSet;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author aloksingh
 *
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class ChatRoom {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    
	@ManyToMany
    @JoinTable(
        name = "chat_room_users",
        joinColumns = @JoinColumn(name = "chat_room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();
    
    private boolean isGroup=false;
    
    private boolean isRequest=true;
    
    private boolean isDeleted=true;
    
    private LocalDateTime timeStamp;
    

}
