/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alok91340.gethired.dto.AddressDto;
import com.alok91340.gethired.entities.Address;
import com.alok91340.gethired.exception.ResourceNotFoundException;
import com.alok91340.gethired.repository.AddressRepository;
import com.alok91340.gethired.repository.UserRepository;
import com.alok91340.gethired.service.AddressService;
import com.alok91340.gethired.entities.User;

/**
 * @author alok91340
 *
 */
@Service
public class AddressServiceImpl implements AddressService{

	@Autowired 
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;

	@Override
	public AddressDto addAddress(AddressDto addressDto, Long userId) {
		User user=this.userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user",(long)0));
		Address address=mapToEntity(addressDto);
		address.setUser(user);
		Address savedAddress=this.addressRepository.save(address);
		return mapToDto(savedAddress);
	}

	@Override
	public AddressDto updateAddress(AddressDto addressDto, Long addressId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddressDto getAddress(Long addressId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAddress(Long addressid) {
		// TODO Auto-generated method stub
		
	}
	
	private Address mapToEntity(AddressDto addressDto) {
		Address address= new Address();
		address.setStreet(addressDto.getStreet());
		address.setCity(addressDto.getCity());
		address.setState(addressDto.getState());
		address.setPincode(addressDto.getPincode());
		address.setCountry(addressDto.getCountry());
		return address;
	}
	
	private AddressDto mapToDto(Address address) {
		AddressDto addressDto= new AddressDto();
		addressDto.setId(address.getId());
		addressDto.setStreet(address.getStreet());
		addressDto.setCity(address.getCity());
		addressDto.setState(address.getState());
		addressDto.setPincode(address.getPincode());
		addressDto.setCountry(address.getCountry());
		return addressDto;
	}
	
	
	
}
