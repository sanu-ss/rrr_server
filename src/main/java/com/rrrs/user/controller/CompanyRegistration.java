package com.rrrs.user.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.user.entity.CompanyDtls;
import com.rrrs.user.service.CompanyRegistrationService;
import com.rrrs.util.ResponseData;

@RestController
@RequestMapping(value = "api/resource")
public class CompanyRegistration {

	private CompanyRegistrationService companyRegistrationService;
	public CompanyRegistration(CompanyRegistrationService companyRegistrationService) {
		this.companyRegistrationService=companyRegistrationService;
	}
	@PostMapping(value = "savecompany")
	public ResponseEntity<?> saveCompany(@RequestBody CompanyDtls companyDtls,Principal principal){
		String userIns=principal.getName();
		companyDtls.setUserIns(userIns);
		String status=this.companyRegistrationService.saveCompany(companyDtls);
		return new ResponseEntity<>(new ResponseData(status),HttpStatus.OK);
	}
	
}
