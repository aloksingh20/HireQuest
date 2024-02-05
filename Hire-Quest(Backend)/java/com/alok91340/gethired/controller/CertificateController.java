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

import com.alok91340.gethired.dto.CertificateDto;
import com.alok91340.gethired.service.CertificateService;

/**
 * @author alok91340
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class CertificateController {

	@Autowired
	private CertificateService certificateService;
	
	@PostMapping("{userProfileId}/add-certificate")
	public ResponseEntity<CertificateDto> addCertificate(@PathVariable Long userProfileId, @RequestBody CertificateDto certificateDto){
		CertificateDto result=this.certificateService.addCertificate(certificateDto, userProfileId);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	@PutMapping("{certificateId}/update-certificate")
	public ResponseEntity<CertificateDto> updateLanguage(@PathVariable Long certificateId,@RequestBody CertificateDto certificateDto){
		CertificateDto result=this.certificateService.updateCertificate(certificateDto, certificateId);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	
	@GetMapping("{userProfileId}/get-certificates")
	public ResponseEntity<List<CertificateDto>> getAllLanguage(@PathVariable Long userProfileId){
		List<CertificateDto> certificateDtos=this.certificateService.getAllCertificate(userProfileId);
		return new ResponseEntity<>(certificateDtos,HttpStatus.OK);
	}
	@DeleteMapping("{certificateId}/delete-certificate")
	public ResponseEntity<String> deleteCertificate(@PathVariable Long certificateId){
		this.certificateService.deleteCertificate(certificateId);
		return new ResponseEntity<>("deleted",HttpStatus.OK);
	}
}