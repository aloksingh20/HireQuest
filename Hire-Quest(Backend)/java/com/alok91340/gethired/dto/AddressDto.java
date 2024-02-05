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
public class AddressDto {
	private Long id;
	private String street;
	private String country;
	private String state;
	private String pincode;
	private String city;
}
