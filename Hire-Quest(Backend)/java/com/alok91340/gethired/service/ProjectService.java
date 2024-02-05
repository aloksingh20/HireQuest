/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import com.alok91340.gethired.dto.ProjectDto;

/**
 * @author alok91340
 *
 */
public interface ProjectService {
	
	ProjectDto addProject(ProjectDto projectDto,Long userProfileId);
	ProjectDto updateProject(ProjectDto projectDto, Long projectId);
	ProjectDto getProject(Long projectId);
	List<ProjectDto> getAllProject(Long userProfileId);
	void deleteProject(Long projectId);
}
