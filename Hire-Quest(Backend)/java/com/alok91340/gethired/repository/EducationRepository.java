/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Education;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author alok91340
 *
 */
public interface EducationRepository extends JpaRepository<Education,Long>{

	List<Education> findAllByUserProfile(UserProfile userProfile);
}
