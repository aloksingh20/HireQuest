/**
 * 
 */
package com.alok91340.gethired.service;

import com.alok91340.gethired.dto.AddressDto;

/**
 * @author alok91340
 *
 */
public interface AddressService {
	
	AddressDto addAddress(AddressDto addressDto, Long username);
	AddressDto updateAddress(AddressDto addressDto, Long addressId);
	AddressDto getAddress(Long addressId);
	void deleteAddress(Long addressid);
}
