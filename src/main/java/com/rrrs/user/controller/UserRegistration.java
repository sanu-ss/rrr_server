package com.rrrs.user.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.user.entity.CurrentUser;
import com.rrrs.user.service.UserRegistrationService;
import com.rrrs.util.PWDGenerator;
import com.rrrs.util.ResponseData;

@RestController
@RequestMapping(value = "api/resource/")
public class UserRegistration {

	private UserRegistrationService userRegistrationService;
	public UserRegistration(UserRegistrationService userRegistrationService) {
		this.userRegistrationService=userRegistrationService;
	}
	@GetMapping(value = "requiredUserRegistration")
	public ResponseEntity<?> getRequiredUserRegistrationData(Principal principal){
		Map<String,Object> companyAndUserRole=this.userRegistrationService.getRequiredUserRegistrationData(principal);
		return new ResponseEntity<>(companyAndUserRole,HttpStatus.OK);
	}
	
	@PostMapping(value = "userRegistration")
	public ResponseEntity<?> userRegistration(@RequestBody CurrentUser currentUser,Principal principal){
		String userIns=principal.getName();
		currentUser.getRrrCommonDtls().setUserIns(userIns);
		System.out.println("current user -->"+currentUser);
		String status=this.userRegistrationService.saveUser(currentUser);
		return new ResponseEntity<>(new ResponseData(status),HttpStatus.OK);
	}
	@PostMapping("updatepwd")
	public ResponseEntity<?> updatePwd(){
		String status=this.userRegistrationService.updatePwd();
		return null;
	}
}
