/**
 * 
 */
package com.alok91340.gethired.dto;

import lombok.Data;

/**
 * @author aloksingh
 *
 */
@Data
public class RecentSearchDto {

	private long id;
	
	private Long userId;
	private String searchedText;
	
}
