/**
 * 
 */
package com.alok91340.gethired.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.net.URI;

import com.alok91340.gethired.dto.PdfDto;
import com.alok91340.gethired.repository.ImageRepository;
import com.alok91340.gethired.repository.PdfRepository;
import com.alok91340.gethired.service.PdfService;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import lombok.extern.java.Log;

/**
 * @author aloksingh
 *
 */
@RestController
@RequestMapping("api/hireQuest")
public class PdfController {
	
	@Autowired
	private PdfService pdfService;
	
	@Autowired
	private PdfRepository pdfRepository;
	
	@Autowired 
	private ImageRepository imageRepository;
	
	
	@PostMapping("/{userProfileId}/add-pdf")
	public ResponseEntity<PdfDto> addPdf(@PathVariable Long userProfileId, @RequestParam("file") MultipartFile file){
		PdfDto pdf=this.pdfService.addPdf(userProfileId, file);
		
		return new ResponseEntity<>(pdf,HttpStatus.OK);
	}
	
	@GetMapping("/{userProfileId}/get-pdfs")
	public ResponseEntity<List<PdfDto>> getAllPdf(@PathVariable Long userProfileId){
		List<PdfDto> pdfs=this.pdfService.getAllPdf(userProfileId);
		return new ResponseEntity<>(pdfs,HttpStatus.OK);
	}
	
	@GetMapping("/{pdfId}/get-pdf")
	public ResponseEntity<String> getPdf(@PathVariable Long pdfId){
		String pdf=this.imageRepository.findById(pdfId).orElseThrow(null).getData().toString();
		return new ResponseEntity<>(pdf,HttpStatus.OK);
	}
	
	@DeleteMapping("/{pdfId}/delete-pdf")
	public void deletePdf(@PathVariable Long pdfId){
		this.pdfService.deletePdf(pdfId);
	}

	
	@GetMapping("/{pdfId}/download-pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long pdfId) {
        // Retrieve the PDF content by its name
        byte[] pdfContent = this.pdfRepository.findById(pdfId).orElseThrow(null).getFileData();


        // Convert byte array to InputStreamResource
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfContent);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
	
//	@GetMapping("/{pdfId}/download-url")
//	public ResponseEntity<String> getDownloadUrl(@PathVariable Long pdfId) {
//	    // Retrieve the PDF byte array from the database
//	    byte[] pdfContent = getPdfFromDatabase(pdfId);
//
//	    // Generate a temporary download URL
//	    String downloadUrl = generateTemporaryDownloadUrl();
//
//	    // Store the PDF byte array in a temporary location
//	    storePdfInTemporaryLocation(pdfContent, downloadUrl);
//
//	    // Return the download URL in the response body
//	    return ResponseEntity.ok(downloadUrl);
//	}
//
//	private byte[] getPdfFromDatabase(Long pdfId) {
//	    // Implement logic to retrieve the byte array from your SQL database using the provided ID
//	    // This might involve using a JPA repository or a raw SQL query
//		return this.pdfRepository.findById(pdfId).orElseThrow(null).getFileData();
//	}

//	private String generateTemporaryDownloadUrl() {
//	    // Generate a UUID
//	    String identifier = UUID.randomUUID().toString();
//
//	    // Define the base download URL
//	    String baseDownloadUrl = "https://your-server.com/temp-downloads/";
//
//	    // Combine the identifier with the base URL and add an expiration time parameter
//	    long expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15);
//	    String downloadUrl = String.format("%s%s?expires=%d", baseDownloadUrl, identifier, expirationTime);
//
//	    return downloadUrl;
//	}


//	private void storePdfInTemporaryLocation(byte[] pdfContent, String downloadUrl) {
//	    // Check if the download URL is an HTTP or HTTPS URL
//	    String protocol = URI.create(downloadUrl).getScheme();
//
//	    if (protocol.equals("http")) {
//	        // Use Java's FileOutputStream to write the byte array to a temporary file
//	        File tempFile = File.createTempFile("pdf", ".pdf");
//	        FileOutputStream outputStream = new FileOutputStream(tempFile);
//	        outputStream.write(pdfContent);
//	        outputStream.close();
//
//	        // Use Apache Commons IO to upload the temporary file to the specified URL
//	        URL url = new URL(downloadUrl);
//	        FileUtils.copyFile(tempFile, new File(url.toURI()));
//	    } else if (protocol.equals("https")) {
//	        // Use the API provided by your chosen cloud storage provider to upload the byte array to the specified URL
//	        // This will require additional implementation specific to your chosen cloud storage provider
//	        // For example, using AWS S3 would involve using the PutObject API call
//	    }
//	}



}
