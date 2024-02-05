/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.AppreciationDto;
import com.alok91340.gethired.service.AppreciationService;
import com.alok91340.gethired.entities.Appreciation;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.AppreciationRepository;
import com.alok91340.gethired.repository.UserProfileRepository;

/**
 * @author alok91340
 *
 */
@Service
public class AppreciationServiceImpl implements AppreciationService{
	
	@Autowired
	private AppreciationRepository awardRepository;
	
	@Autowired 
	private UserProfileRepository userProfileRepository;

	@Override
	public AppreciationDto addAppreciation(AppreciationDto awardDto, Long userProfileId) {

		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		Appreciation award=mapToEntity(awardDto);
		award.setUserProfile(userProfile);
		Appreciation savedAward=this.awardRepository.save(award);
		return mapToDto(savedAward);
	}
	
	@Override
	public AppreciationDto getAppreciation(Long awardId) {
		Appreciation award=this.awardRepository.findById(awardId).orElseThrow(()->new ResourceNotFoundException("Award",awardId));
		return mapToDto(award);
	}

	@Override
	public AppreciationDto updateAppreciation(AppreciationDto appreciationDto, Long awardId) {
		Appreciation appreciation=this.awardRepository.findById(awardId).orElseThrow(()->new ResourceNotFoundException("award",awardId));
		appreciation.setAppreciationTitle(appreciationDto.getAppreciationTitle());
		appreciation.setAppreciationUrl(appreciationDto.getAppreciationUrl());
		appreciation.setDescription(appreciationDto.getDescription());
		appreciation.setStart(appreciationDto.getStart());
		appreciation.setEnd(appreciationDto.getEnd());
		appreciation.setIssuedBy(appreciationDto.getIssuedBy());
		
		Appreciation savedAward=this.awardRepository.save(appreciation);
		return mapToDto(savedAward);
	}

	@Override
	public List<AppreciationDto> getAllAppreciation(Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		List<Appreciation> awards=this.awardRepository.findAllByUserProfile(userProfile);
		List<AppreciationDto> awardDtos=awards.stream().map(award->mapToDto(award)).collect(Collectors.toList());
		return awardDtos;
	}

	@Override
	public void deleteAppreciation(Long awardId) {
		// TODO Auto-generated method stub
		
	}
	
	private Appreciation mapToEntity(AppreciationDto appreciationDto) {
		Appreciation appreciation= new Appreciation();
		appreciation.setAppreciationTitle(appreciationDto.getAppreciationTitle());
		appreciation.setAppreciationUrl(appreciationDto.getAppreciationUrl());
		appreciation.setDescription(appreciationDto.getDescription());
		appreciation.setStart(appreciationDto.getStart());
		appreciation.setEnd(appreciationDto.getEnd());
		appreciation.setIssuedBy(appreciationDto.getIssuedBy());
		return appreciation;
	}
	private AppreciationDto mapToDto(Appreciation appreciation) {
		AppreciationDto appreciationDto= new AppreciationDto();
		appreciationDto.setId(appreciation.getId());
		appreciationDto.setAppreciationTitle(appreciation.getAppreciationTitle());
		appreciationDto.setAppreciationUrl(appreciation.getAppreciationUrl());
		appreciationDto.setDescription(appreciation.getDescription());
		appreciationDto.setStart(appreciation.getStart());
		appreciationDto.setEnd(appreciation.getEnd());
		appreciationDto.setIssuedBy(appreciation.getIssuedBy());
		return appreciationDto;
	}

}
