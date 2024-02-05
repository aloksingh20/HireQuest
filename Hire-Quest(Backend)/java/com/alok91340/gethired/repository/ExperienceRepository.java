/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Experience;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author alok91340
 *
 */
public interface ExperienceRepository extends JpaRepository<Experience,Long>{

	List<Experience> findAllByUserProfile(UserProfile userProfile);
}
