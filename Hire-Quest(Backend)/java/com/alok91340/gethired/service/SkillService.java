/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;


/**
 * @author alok91340
 *
 */
public interface SkillService {
	List<String> addSkill(Long userProfileId,List<String> skills);
	List<String> getAllSkill(Long userProfileId);
}
