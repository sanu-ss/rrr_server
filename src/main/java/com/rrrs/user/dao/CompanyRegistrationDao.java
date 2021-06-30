package com.rrrs.user.dao;

import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.CompanyDtls;

@Repository
public interface CompanyRegistrationDao {

	public String saveCompany(CompanyDtls companyDtls);

	public String checkUniqueKeys(CompanyDtls companyDtls);

}
