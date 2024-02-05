/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alok91340.gethired.service.ImageService;
import com.alok91340.gethired.utils.ImageUtils;
import com.alok91340.gethired.dto.ImageDto;
import com.alok91340.gethired.entities.Image;
import com.alok91340.gethired.entities.User;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.ImageRepository;
import com.alok91340.gethired.repository.UserRepository;

/**
 * @author alok91340
 *
 */
@Service
public class ImageServiceImpl implements ImageService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ImageRepository imageRepository;

	@Override
    public ImageDto saveImage(Long userId, MultipartFile file) {
        Image image = uploadImage(file);
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", (long) 0));

        Image img = this.imageRepository.findImageByUser(user);
        if (img != null) {
            try {
				img.setData((file.getBytes()));
			} catch (IOException e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
			}
            
            Image updatedImage = this.imageRepository.save(img);
            user.setImage(encodeImageToString(updatedImage.getData()));
            userRepository.save(user);
//            updatedImage.setData(ImageUtils.decompressImage(updatedImage.getData()));
            return mapToDto(updatedImage);
        } else {
            image.setUser(user);

            Image savedImage = this.imageRepository.save(image);
            savedImage.setData(ImageUtils.decompressImage(savedImage.getData()));
            
            user.setImage(encodeImageToString(savedImage.getData()));
            userRepository.save(user);
            
            return mapToDto(savedImage);
        }
    } 

    private Image uploadImage(MultipartFile file) {
        Image imageData = new Image();
        imageData.setName(file.getOriginalFilename()); // Use original filename
        imageData.setType(file.getContentType());
        try {
			imageData.setData(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return imageData;
    }

	@Override
	public ImageDto getImage(Long userId) {
		User user=this.userRepository.findById(userId).orElseThrow();
		Image image=this.imageRepository.findImageByUser(user);
		return mapToDto(image);
		
	
	}
	
	ImageDto mapToDto(Image image) {
		ImageDto imageDto= new ImageDto();
		imageDto.setId(image.getId());
		imageDto.setData(image.getData());
		imageDto.setName(image.getName());
		imageDto.setType(image.getType());
		return imageDto;

	}
	
	String encodeImageToString(byte[] imageData) {
		return Base64.getEncoder().encodeToString(imageData);
	}


}
