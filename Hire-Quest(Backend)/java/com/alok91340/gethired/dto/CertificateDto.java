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
public class CertificateDto {
private long id;
	
	private String certificateTitle;
	private String start;
	private String end;
	private String issuedBy;
	private String certificateUrl;
	private String description;
	
}
