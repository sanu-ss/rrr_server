package com.rrrs.user.service;

import org.springframework.stereotype.Service;

import com.rrrs.user.entity.CompanyDtls;

@Service
public interface CompanyRegistrationService {

	String saveCompany(CompanyDtls companyDtls);

}
