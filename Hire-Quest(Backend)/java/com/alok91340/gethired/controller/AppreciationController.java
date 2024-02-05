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

import com.alok91340.gethired.dto.AppreciationDto;
import com.alok91340.gethired.service.AppreciationService;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class AppreciationController {
	
	@Autowired
	private AppreciationService appreciationService;
	
	
	@PostMapping("/{userProfileId}/add-appreciation")
	public ResponseEntity<AppreciationDto> addAward(@PathVariable Long userProfileId, @RequestBody AppreciationDto awardDto){
		
		AppreciationDto result=this.appreciationService.addAppreciation(awardDto, userProfileId);
		return ResponseEntity.ok(result);
	}
	
	
	@GetMapping("/{userProfileId}/get-appreciations")
	public ResponseEntity<List<AppreciationDto>> getAllAppreciation(@PathVariable Long userProfileId){
		
		List<AppreciationDto>  appreciations=this.appreciationService.getAllAppreciation(userProfileId);
		return new ResponseEntity<>(appreciations,HttpStatus.OK);
	}
	
	
	@PutMapping("/{appreciationId}/update-appreciation")
	public ResponseEntity<AppreciationDto> updateAppreciation(@PathVariable Long appreciationId, @RequestBody AppreciationDto appreciationDto){
		
		AppreciationDto appreciation=this.appreciationService.updateAppreciation(appreciationDto, appreciationId);
		return new ResponseEntity<>(appreciation,HttpStatus.OK);
	}

	@DeleteMapping("/{appreciationId}/delete-appreciation")
	public ResponseEntity<String> deleteAppreciation(@PathVariable Long appreciationId){
		this.appreciationService.deleteAppreciation(appreciationId);
		return new ResponseEntity<>("deleted",HttpStatus.OK);
	}
}
