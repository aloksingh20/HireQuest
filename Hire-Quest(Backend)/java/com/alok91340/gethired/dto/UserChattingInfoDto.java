/**
 * 
 */
package com.alok91340.gethired.dto;

import com.alok91340.gethired.entities.Image;

import lombok.Data;

/**
 * @author aloksingh
 *
 */

@Data
public class UserChattingInfoDto {

	private String username;
	private String image;
	private Boolean isRequested;
	private Boolean isSender;
	private Long roomId;
}
