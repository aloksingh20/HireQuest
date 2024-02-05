/**
 * 
 */
package com.alok91340.gethired.service;

import com.alok91340.gethired.entities.Otp;

/**
 * @author aloksingh
 *
 */
public interface OtpService {
	Otp createOtp(String email);
	Otp updateOtp(String email);
	Boolean verifyOtp(String otpCode, String email);
}
