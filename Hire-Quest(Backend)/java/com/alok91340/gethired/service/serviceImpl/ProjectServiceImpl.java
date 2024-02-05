/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.ProjectDto;
import com.alok91340.gethired.entities.Project;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.ProjectRepository;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.ProjectService;

/**
 * @author alok91340
 *
 */
@Service
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	@Override
	public ProjectDto addProject(ProjectDto projectDto, Long userProfileId) {
		
		UserProfile userProfile = this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		Project project=mapToEntity(projectDto);
		project.setUserProfile(userProfile);
		Project savedProject = this.projectRepository.save(project);
		
		return mapToDto(savedProject);
	}

	@Override
	public ProjectDto updateProject(ProjectDto projectDto, Long projectId) {
		
		Project project = this.projectRepository.findById(projectId).orElseThrow(()->new ResourceNotFoundException("project",projectId));
		project.setTitle(projectDto.getTitle());
		project.setProjectUrl(projectDto.getProjectUrl());
		project.setStart(projectDto.getStart());
		project.setEnd(projectDto.getEnd());
		Project savedProject=this.projectRepository.save(project);
		return mapToDto(savedProject);
	}

	@Override
	public ProjectDto getProject(Long projectId) {
		Project project = this.projectRepository.findById(projectId).orElseThrow(()->new ResourceNotFoundException("project",projectId));

		return mapToDto(project);
	}

	@Override
	public List<ProjectDto> getAllProject(Long userProfileId) {
	    UserProfile userProfile = this.userProfileRepository.findById(userProfileId)
	            .orElseThrow(() -> new ResourceNotFoundException("user-profile", userProfileId));
	    List<Project> projects = this.projectRepository.findAllByUserProfile(userProfile);
	    List<ProjectDto> projectDtos = projects.stream().map(project -> mapToDto(project)).collect(Collectors.toList());

	    return projectDtos;
	}


	@Override
	public void deleteProject(Long projectId) {
		this.projectRepository.deleteById(projectId);
		return;
	}
	
	
	private Project mapToEntity(ProjectDto projectDto) {
		Project project = new Project();
		project.setTitle(projectDto.getTitle());
		project.setProjectUrl(projectDto.getProjectUrl());
		project.setStart(projectDto.getStart());
		project.setEnd(projectDto.getEnd());
		project.setDescription(projectDto.getDescription());
		return project;
		
	}
	
	/**
	 * @param savedProject
	 * @return
	 */
	private ProjectDto mapToDto(Project project) {
		ProjectDto projectDto= new ProjectDto();
		projectDto.setId(project.getId());
		projectDto.setTitle(project.getTitle());
		projectDto.setProjectUrl(project.getProjectUrl());
		projectDto.setStart(project.getStart());
		projectDto.setEnd(project.getEnd());
		projectDto.setDescription(project.getDescription());
		return projectDto;
	}


}
