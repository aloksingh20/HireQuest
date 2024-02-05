/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.CertificateDto;
import com.alok91340.gethired.entities.Certificate;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.CertificateRepository;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.CertificateService;

/**
 * @author alok91340
 *
 */
@Service
public class CertificateServiceImpl implements CertificateService{
	
	

	@Autowired 
	private CertificateRepository certificateRepository;
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Override
	public CertificateDto addCertificate(CertificateDto certificateDto, Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()-> new ResourceNotFoundException("user-profile",userProfileId));
		Certificate certificate= new Certificate();
		mapToEntity(certificateDto, certificate);
		certificate.setUserProfile(userProfile);
		Certificate savedCertificate=this.certificateRepository.save(certificate);
		return mapToDto(savedCertificate);
	}
	
	@Override
	public CertificateDto getCertificate(Long certificateId) {
		Certificate certificate=this.certificateRepository.findById(certificateId).orElseThrow(()->new ResourceNotFoundException("Certificate",certificateId));

		return mapToDto(certificate);
	}

	@Override
	public CertificateDto updateCertificate(CertificateDto certificateDto, Long certificateId) {
		Certificate certificate=this.certificateRepository.findById(certificateId).orElseThrow(()->new ResourceNotFoundException("Certificate",certificateId));
		mapToEntity(certificateDto, certificate);
		Certificate savedCertificate=this.certificateRepository.save(certificate);
		return mapToDto(savedCertificate);
	}

	@Override
	public List<CertificateDto> getAllCertificate(Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()-> new ResourceNotFoundException("user-profile",userProfileId));
		List<Certificate> certificates=this.certificateRepository.findAllByUserProfile(userProfile);
		List<CertificateDto> certificateDto=certificates.stream().map(certificate->mapToDto(certificate)).collect(Collectors.toList());
		return certificateDto;
	}

	@Override
	public void deleteCertificate(Long certificateId) {
		Certificate certificate=this.certificateRepository.findById(certificateId).orElseThrow(()->new ResourceNotFoundException("Certificate",certificateId));
		this.certificateRepository.delete(certificate);
		
	}
	
	public void mapToEntity(CertificateDto certificateDto,Certificate certificate) {
		certificate.setCertificateTitle(certificateDto.getCertificateTitle());
		certificate.setCertificateUrl(certificateDto.getCertificateUrl());
		certificate.setDescription(certificateDto.getDescription());
		certificate.setIssuedBy(certificateDto.getIssuedBy());
		
		certificate.setStart(certificateDto.getStart());
		certificate.setEnd(certificateDto.getEnd());
	}
	
	public CertificateDto mapToDto(Certificate certificate) {
		CertificateDto certificateDto=new CertificateDto();
		certificateDto.setId(certificate.getId());
		certificateDto.setCertificateTitle(certificate.getCertificateTitle());
		certificateDto.setCertificateUrl(certificate.getCertificateUrl());
		certificateDto.setDescription(certificate.getDescription());
		certificateDto.setIssuedBy(certificate.getIssuedBy());
		
		certificateDto.setStart(certificate.getStart());
		certificateDto.setEnd(certificate.getEnd());
		return certificateDto;
	}

}
