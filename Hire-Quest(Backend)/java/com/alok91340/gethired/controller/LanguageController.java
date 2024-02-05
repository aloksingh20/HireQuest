/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alok91340.gethired.service.LanguageService;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class LanguageController {
	
	@Autowired
	private LanguageService languageService;
	
	@PostMapping("/{userProfileId}/add-languages")
	public ResponseEntity<List<String>> addLanguage(@PathVariable Long userProfileId, @RequestBody List<String> languages){
		List<String> result=this.languageService.addLanguage(languages, userProfileId);
		return ResponseEntity.ok(result);
	}
	
	
	@GetMapping("/{userProfileId}/get-languages")
	public ResponseEntity<List<String>> getAllLanguage(@PathVariable Long userProfileId){
		List<String> languageDtos=this.languageService.getAllLanguage(userProfileId);
		return new ResponseEntity<>(languageDtos,HttpStatus.OK);
	}

}
