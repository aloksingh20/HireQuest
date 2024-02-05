/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Certificate;
import com.alok91340.gethired.entities.UserProfile;

/**
 * @author alok91340
 *
 */
public interface CertificateRepository extends JpaRepository<Certificate,Long>{

	List<Certificate> findAllByUserProfile(UserProfile userProfile);
}
