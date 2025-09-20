package com.leftoverlink.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OtpVerifyRequest {
    private String email;
    private String otp;
    private SignupRequest signupData;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public SignupRequest getSignupData() {
		return signupData;
	}
	public void setSignupData(SignupRequest signupData) {
		this.signupData = signupData;
	}
    
    
}
