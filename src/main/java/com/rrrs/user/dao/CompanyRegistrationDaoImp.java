package com.rrrs.user.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.CompanyDtls;

@Repository
public class CompanyRegistrationDaoImp implements CompanyRegistrationDao{

	private EntityManager entityManager;
	public CompanyRegistrationDaoImp(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	@Override
	public String saveCompany(CompanyDtls companyDtls) {
		String status="";
		Session session = this.entityManager.unwrap(Session.class);
		Query getQuery = session.createQuery("select user.companyId from CurrentUser user where username=:username");
		getQuery.setParameter("username", companyDtls.getUserIns());
		Integer companyId=(Integer)getQuery.getSingleResult();
		System.out.println("company Id"+companyId);
		companyDtls.setId(companyId);
		Transaction tx = session.beginTransaction();
		try {
			session.persist(companyDtls);
			tx.commit();
			status="success";
		}catch (Exception e) {
			status="exceptionWhileSavingCompanyDtls";
		}
		session.close();
		return status;
	}
	@Override
	public String checkUniqueKeys(CompanyDtls companyDtls) {
		Session session = this.entityManager.unwrap(Session.class);
		Query checkDuplicateCompanyName = session.createQuery("select count(company.companyName) from CompanyDtls company where company.companyName=:companyName");
			checkDuplicateCompanyName.setParameter("companyName", companyDtls.getCompanyName());
			Long nameCount = (Long)checkDuplicateCompanyName.uniqueResult();
		if(nameCount>0) {
			return "duplicateCompanyName";
		}else {
			Query checkDuplicateEmail = session.createQuery("select count(company.email) from CompanyDtls company where company.email=:email");
			checkDuplicateEmail.setParameter("email", companyDtls.getEmail());
			Long emailCount = (Long)checkDuplicateEmail.uniqueResult();
			if(emailCount>0) {
				return "duplicateEmail";
			}
		}
		return "success";
	}

}
