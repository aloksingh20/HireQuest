/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author aloksingh
 *
 */

@Service
public class PresenceServiceImpl {
	
	 @Autowired(required=true)
	  private RedisTemplate<String, String> redisTemplate;
	 
	 public void setUserOnline(String userId) {
	        redisTemplate.opsForValue().set(userId, "online");
	    }

	    public void setUserOffline(String userId) {
	        redisTemplate.opsForValue().set(userId, "offline");
	    }

	    public String getUserPresence(String userId) {
	        return redisTemplate.opsForValue().get(userId);
	    }
	    
	    
	    @PreDestroy
	    public void cleanUp() {
	        // This method will be called when the Spring context is shutting down
	        // Close the Redis connection here
	        // Make sure 'redisTemplate' is properly injected in this class
	        if(redisTemplate != null) {
	            redisTemplate.getConnectionFactory().getConnection().close();
	        }
	    }

}
