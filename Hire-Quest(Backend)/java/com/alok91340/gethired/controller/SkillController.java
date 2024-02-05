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

import com.alok91340.gethired.service.SkillService;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class SkillController {
	
	@Autowired
	private SkillService skillService;
	
	@PostMapping("/{userProfileId}/add-skills")
	public ResponseEntity<List<String>> addSkill(@RequestBody List<String> skills, @PathVariable Long userProfileId){
		
		List<String> result=skillService.addSkill(userProfileId, skills);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/{userProfileId}/get-skills")
	public ResponseEntity<List<String>> getAllSkill(@PathVariable Long userProfileId){
		
		List<String> result=this.skillService.getAllSkill(userProfileId);
		return new ResponseEntity<>(result,HttpStatus.OK);
		
	}
	

}
