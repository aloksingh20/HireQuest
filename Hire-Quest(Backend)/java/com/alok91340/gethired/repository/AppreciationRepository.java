/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Appreciation;
import com.alok91340.gethired.entities.Certificate;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author alok91340
 *
 */
public interface AppreciationRepository extends JpaRepository<Appreciation,Long>{

	List<Appreciation> findAllByUserProfile(UserProfile userProfile);
}
