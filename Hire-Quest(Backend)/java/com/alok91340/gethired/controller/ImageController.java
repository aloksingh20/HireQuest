/**
 * 
 */
package com.alok91340.gethired.controller;

import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alok91340.gethired.dto.ImageDto;
import com.alok91340.gethired.service.ImageService;

/**
 * @author aloksingh
 *
 */

@RestController
@RequestMapping("api/hireQuest")
public class ImageController {
	
	@Autowired
	private ImageService imageService;
	
	
	@PostMapping("/upload-image")
	private ResponseEntity<Resource> uploadImage(@RequestParam("userId") Long userId, @RequestParam("file") MultipartFile file){
		
		ImageDto image=null;
		try {
			image = this.imageService.saveImage(userId, file);
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 ByteArrayResource imageResource = new ByteArrayResource(image.getData());

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageResource);

		
	
	}
	
	@GetMapping("/get-image")
	public ResponseEntity<Resource> getImage(@RequestParam("userId") Long userId){
		ImageDto image=this.imageService.getImage(userId);
		
        ByteArrayResource imageResource = new ByteArrayResource(image.getData());

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageResource);

	}
	
	

}
