/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;


/**
 * @author alok91340
 *
 */
public interface LanguageService {
	
	List<String> addLanguage(List<String> languages, Long userProfileId);
	List<String> getAllLanguage(Long userProfileId);
}
