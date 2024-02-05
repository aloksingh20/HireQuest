/**
 * 
 */
package com.alok91340.gethired.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.entities.UserProfile;


/**
 * @author alok91340
 *
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	
	

	@Query("SELECT DISTINCT u FROM UserProfile u WHERE LOWER(u.user.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.user.username) LIKE LOWER(CONCAT('%', :query, '%'))")
	Page<UserProfile> searchUserProfiles(String query, Pageable pageable);

//	Page<UserProfile> searchUserProfiles(String query,String language, Pageable pageable);
    
	
	@Query("SELECT DISTINCT u FROM UserProfile u WHERE LOWER(u.user.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.user.username) LIKE LOWER(CONCAT('%', :query, '%')) AND (LOWER(u.user.headline) LIKE LOWER(CONCAT('%', :role, '%')) OR LOWER(u.user.currentOccupation) LIKE LOWER(CONCAT('%', :role, '%')))")
	Page<UserProfile> searchUserProfiles(String query, String role, Pageable pageable);

	
//	@Query("SELECT DISTINCT u FROM UserProfile u WHERE LOWER(u.user.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.user.username) LIKE LOWER(CONCAT('%', :query, '%')) AND u.user.role = :role AND u.user.status = :status")
//	Page<UserProfile> searchUserProfiles(String query, String role, String status, Pageable pageable);

	
    UserProfile findUserProfileByUser(User user);

}