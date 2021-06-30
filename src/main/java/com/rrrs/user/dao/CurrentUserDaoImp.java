package com.rrrs.user.dao;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rrrs.user.entity.CurrentUser;
import com.rrrs.user.entity.RrrCommonDtls;

@Repository
@Transactional
public class CurrentUserDaoImp implements CurrentUserDao {

	private final EntityManager entityManager;
	public CurrentUserDaoImp(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	
	@Override
	public CurrentUser getCurrentUserDetalis(String name) {
		Session session = entityManager.unwrap(Session.class);
		String queryString="SELECT user.username,user.email,user.rrrCommonDtls.companyId,user.rrrCommonDtls.userIns from CurrentUser user where user.username=:username";
		Query query= session.createQuery(queryString);
		query.setParameter("username", name);
		Object[] data=(Object[])query.getSingleResult();
		CurrentUser currentUser=new CurrentUser();
		currentUser.setUsername((String)data[0]);
		currentUser.setEmail((String)data[1]);
		RrrCommonDtls rrrCommonDtls=new RrrCommonDtls();
		rrrCommonDtls.setUserIns((String)data[3]);
		rrrCommonDtls.setCompanyId((int)data[2]);
		currentUser.setRrrCommonDtls(rrrCommonDtls);
		return currentUser;
	}

}
