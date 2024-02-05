/**
 * 
 */
package com.alok91340.gethired.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.alok91340.gethired.dto.PdfDto;

/**
 * @author aloksingh
 *
 */
public interface PdfService {
	
	PdfDto addPdf(Long userProfileId, MultipartFile file);
	List<PdfDto> getAllPdf(Long userProfileId);
	PdfDto getPdf(Long pdfId);
	void deletePdf(Long pdfId);
}
