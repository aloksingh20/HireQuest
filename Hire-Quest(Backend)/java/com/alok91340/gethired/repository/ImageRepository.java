/**
 * 
 */
package com.alok91340.gethired.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Image;
import com.alok91340.gethired.entities.User;

/**
 * @author alok91340
 *
 */
public interface ImageRepository extends JpaRepository<Image,Long>{
	
	Image findImageByUser(User user);
}
