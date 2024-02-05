/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.dto.ProjectDto;
import com.alok91340.gethired.service.ProjectService;
import com.alok91340.gethired.utils.isAuthenticatedAsAdminOrUser;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@isAuthenticatedAsAdminOrUser
	@PostMapping("/{userProfileId}/add-project")
	public ResponseEntity<ProjectDto> addProject(@RequestBody ProjectDto projectDto, @PathVariable Long userProfileId){
		ProjectDto result=this.projectService.addProject(projectDto, userProfileId);
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/{projectId}/update-project")
	public ResponseEntity<ProjectDto> updateProject(@PathVariable Long projectId, @RequestBody ProjectDto projectDto){
		ProjectDto result=this.projectService.updateProject(projectDto, projectId);
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping("/{projectId}/delete-project")
	public ResponseEntity<String> deleteProject(@PathVariable Long projectId){
		this.projectService.deleteProject(projectId);
		return ResponseEntity.ok("deleted");
	}
	
	@GetMapping("/{userProfileId}/get-projects")
	public ResponseEntity<List<ProjectDto>> getProjects(@PathVariable long userProfileId){
		
		List<ProjectDto> result=this.projectService.getAllProject(userProfileId);
		return ResponseEntity.ok(result);
	}

}
