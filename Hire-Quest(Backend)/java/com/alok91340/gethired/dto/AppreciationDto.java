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
public class AppreciationDto {
private Long id;
	
	private String appreciationTitle;
	private String Start;
	private String end;
	private String appreciationUrl;
	private String issuedBy;
	private String description;
}
