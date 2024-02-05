/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Pdf;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author aloksingh
 *
 */
public interface PdfRepository extends JpaRepository<Pdf, Long>{

	List<Pdf> findAllByUserProfile(UserProfile userProfile);

}
