/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Profile;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author alok91340
 *
 */
public interface ProfileRepository extends JpaRepository<Profile,Long>{

	List<Profile> findAllByUserProfile(UserProfile userProfile);

}
