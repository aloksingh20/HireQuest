/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.dto.ExperienceDto;
import com.alok91340.gethired.service.ExperienceService;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class ExperienceController {

	@Autowired
	private ExperienceService experienceService;
	
	@PostMapping("/{userProfileId}/add-experience")
	public ResponseEntity<ExperienceDto> addExperience(@PathVariable Long userProfileId, @RequestBody ExperienceDto experienceDto){
		ExperienceDto result=this.experienceService.addExperience(experienceDto, userProfileId);
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/{userProfileId}/get-experiences")
	public ResponseEntity<List<ExperienceDto>> getAllExperience(@PathVariable Long userProfileId){
		List<ExperienceDto> experienceDtoList=this.experienceService.getAllExperience(userProfileId);
		return new ResponseEntity<>(experienceDtoList,HttpStatus.OK);
	}
	
	@DeleteMapping("/{experienceId}/delete-experience")
	public ResponseEntity<String> deleteExperience(@PathVariable Long experienceId){
		this.experienceService.deleteExperience(experienceId);
		return ResponseEntity.ok("Deleted");
	}
	
	@PutMapping("/{experienceId}/update-experience")
	public ResponseEntity<ExperienceDto> updateExperience(@PathVariable Long experienceId, @RequestBody ExperienceDto experienceDto){
		ExperienceDto result=this.experienceService.updateExperience(experienceDto, experienceId);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
}
