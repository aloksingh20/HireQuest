/**
 * 
 */
package com.alok91340.gethired.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;

import com.alok91340.gethired.entities.Otp;
import com.alok91340.gethired.repository.OtpRepository;
import com.alok91340.gethired.service.EmailServiceImpl;
import com.alok91340.gethired.service.OtpService;

/**
 * @author aloksingh
 *
 */

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    private final EmailServiceImpl emailService;

    @Autowired
    public OtpServiceImpl(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Override
    public Otp createOtp(String email) {
        Otp otp = otpRepository.findOtpByEmail(email);
        if (otp == null) {
            otp = generateAndSendOtp(email);
        } else {
            otp = updateAndSendOtp(otp);
        }
        return otp;
    }

    @Override
    public Otp updateOtp(String email) {
        Otp otp = otpRepository.findOtpByEmail(email);
        if (otp != null) {
            return updateAndSendOtp(otp);
        }
        return generateAndSendOtp(email); // Handle the case when OTP doesn't exist
    }

    @Override
    public Boolean verifyOtp(String otpCode, String email) {
        Otp otp = otpRepository.findOtpByEmail(email);
        return otp != null && otp.getOtpCode().equals(otpCode);
    }

    private Otp updateAndSendOtp(Otp otp) {
        String otpCode = generateRandomOtpCode();
        otp.setOtpCode(otpCode);
        try {
			emailService.sendSimpleEmail(otp.getEmail(), "OTP for Email Verification", otpCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return otpRepository.save(otp);
    }

    private Otp generateAndSendOtp(String email) {
        String otpCode = generateRandomOtpCode();
        Otp newOtp = new Otp();
        newOtp.setOtpCode(otpCode);
        newOtp.setEmail(email);
        try {
			emailService.sendSimpleEmail(email, "OTP for Email Verification", otpCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return otpRepository.save(newOtp);
    }

    private String generateRandomOtpCode() {
        Random random = new Random();
        int otpCode = 100_00 + random.nextInt(900_00);
        return String.valueOf(otpCode);
    }
}
