/**
 * 
 */
package com.alok91340.gethired.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alok91340.gethired.entities.Otp;
import com.alok91340.gethired.service.OtpService;
import com.alok91340.gethired.dto.Response;

/**
 * @author aloksingh
 *
 */

@RestController
@RequestMapping("api/hireQuest")
public class OtpController {
	
	@Autowired
	private OtpService otpService;
	
	@PostMapping("/{email}/send-otp")
	public ResponseEntity<Otp> createOtp(@PathVariable String email){
		
		Otp otp=this.otpService.createOtp(email);
		return new ResponseEntity<>(otp, HttpStatus.OK);
	}
	@PutMapping("/{email}/resend-otp")
	public ResponseEntity<Otp> resendOtp(@PathVariable String email){
		Otp otp=this.otpService.updateOtp(email);
		return new ResponseEntity<>(otp, HttpStatus.OK);
	}
	
	@PostMapping("/{otpCode}/{email}/otp-verification")
	public ResponseEntity<Response> verifyOtp(@PathVariable String otpCode, @PathVariable String email){
		
		boolean isMatched=this.otpService.verifyOtp(otpCode,email);
		Response response= new Response();
		response.setUrl("/{otpCode}/{email}/otp-verification");
		if(isMatched) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Otp matched");
			return new ResponseEntity<>(response,HttpStatus.OK);
		}else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Otp not matched");
			return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
		}
		
	}
	

}
