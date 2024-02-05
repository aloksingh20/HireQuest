/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alok91340.gethired.dto.PdfDto;
import com.alok91340.gethired.entities.UserProfile;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.PdfRepository;
import com.alok91340.gethired.repository.UserProfileRepository;
import com.alok91340.gethired.service.PdfService;
import com.alok91340.gethired.entities.Pdf;

/**
 * @author aloksingh
 *
 */
@Service
public class PdfServiceImpl implements PdfService{
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private PdfRepository pdfRepository;

	@Override
	public PdfDto addPdf(Long userProfileId, MultipartFile file) {
		
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		Pdf pdf= new Pdf();
		
		mapToPdf(file,pdf);
		pdf.setUserProfile(userProfile);
		
		Pdf savedPdf=this.pdfRepository.save(pdf);
		
		return mapToDto(savedPdf);
       
        
		
	}

	/**
	 * @param savedPdf
	 * @return
	 */
	private PdfDto mapToDto(Pdf savedPdf) {
		PdfDto pdfDto= new PdfDto();
		pdfDto.setId(savedPdf.getId());
		pdfDto.setFileName(savedPdf.getFileName());
		pdfDto.setFileSize(savedPdf.getFileSize());
		pdfDto.setTimeStamp(savedPdf.getTimeStamp());
		return pdfDto;
	}
	private void mapToPdf(MultipartFile file, Pdf pdf) {
		pdf.setFileName(file.getOriginalFilename());
		pdf.setFileSize(Long.toString(file.getSize())+ "kb");
		try {
			pdf.setFileData(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pdf.setFileData(null);
		}
		LocalDateTime timeStamp=LocalDateTime.now();
        int hour=timeStamp.getHour();
                int min=timeStamp.getMinute();
                int day=timeStamp.getDayOfMonth();
                int year=timeStamp.getYear();
                Month month=timeStamp.getMonth();


                String time =" ";
                if(hour>=12){
                    hour %= 12;
                    time =Integer.valueOf(hour)+":"+Integer.valueOf(min)+"pm";

                }else{
                	time =Integer.valueOf(hour)+":"+Integer.valueOf(min)+"am";
                }
                
                String monthYear = Integer.valueOf(day)+" "+ month.toString().substring(0,3).toLowerCase()+" "+ Integer.valueOf(year);

		pdf.setTimeStamp(monthYear+" "+ time);
	}

	@Override
	public List<PdfDto> getAllPdf(Long userProfileId) {
		UserProfile userProfile=this.userProfileRepository.findById(userProfileId).orElseThrow(()->new ResourceNotFoundException("user-profile",userProfileId));
		List<Pdf> pdfs=this.pdfRepository.findAllByUserProfile(userProfile);
		List<PdfDto>pdfDtos=pdfs.stream().map(pdf->mapToDto(pdf)).collect(Collectors.toList());
		return pdfDtos;
	}

	@Override
	public PdfDto getPdf(Long pdfId) {
		Pdf pdf=this.pdfRepository.findById(pdfId).orElseThrow(()->new ResourceNotFoundException("pdf",pdfId));
		
		return mapToDto(pdf);
	}

	@Override
	public void deletePdf(Long pdfId) {
		Pdf pdf=this.pdfRepository.findById(pdfId).orElseThrow(()->new ResourceNotFoundException("pdf",pdfId));
		this.pdfRepository.delete(pdf);
		
	}

}
