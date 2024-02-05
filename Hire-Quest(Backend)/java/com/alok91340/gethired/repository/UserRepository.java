/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alok91340.gethired.entities.User;

/**
 * @author alok91340
 *
 */
public interface UserRepository extends JpaRepository<User,Long> {
	
	User findUserByUsername(String username);
	User findUserByEmail(String email);
	Optional<User> findByUsername(String username);
	Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE %:query% OR LOWER(u.email) LIKE %:query% OR LOWER(u.username) LIKE %:query%")
    List<User> searchUsers(String query);
	/**
	 * @param userId
	 * @return
	 */
//	Optional<User> findById(Long userId);
}
