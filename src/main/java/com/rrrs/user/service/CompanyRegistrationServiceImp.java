package com.rrrs.user.service;

import org.springframework.stereotype.Service;

import com.rrrs.user.dao.CompanyRegistrationDao;
import com.rrrs.user.entity.CompanyDtls;

@Service
public class CompanyRegistrationServiceImp implements CompanyRegistrationService {

	private CompanyRegistrationDao companyRegistrationDao;
	public CompanyRegistrationServiceImp(CompanyRegistrationDao companyRegistrationDao) {
		this.companyRegistrationDao=companyRegistrationDao;
	}
	@Override
	public String saveCompany(CompanyDtls companyDtls) {
		String status="";
		status=this.companyRegistrationDao.checkUniqueKeys(companyDtls);
		if(status.equals("success")) {
			status=this.companyRegistrationDao.saveCompany(companyDtls);
		}
		return status;
	}

}
