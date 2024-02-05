/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.entities.UserProfile;

/**
 * @author aloksingh
 *
 */
public interface BookmarkedProfileService {

	Boolean addProfile(Long hrId, Long userProfileId);
	Boolean removeProfile(Long hrId, Long userProfileId);
	public List<UserProfile> getAllBokkmarkedProfile(Long hrId);
}
