package com.rrrs.util;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserDbDtls {

	private EntityManager entityManager;
	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	
	public int getCompanyId(String username) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery("select user.rrrCommonDtls.companyId from CurrentUser user where user.username=:username");
		query.setParameter("username", username);
		return (int)query.uniqueResult();
	}
	
	
}
