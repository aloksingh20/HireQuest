/**
 * 
 */
package com.alok91340.gethired.dto;

import lombok.Data;

/**
 * @author alok91340
 *
 */
@Data
public class ImageDto {
	
	private Long id;
	private String name;
	private byte[] data;
	private String type;
	
}
