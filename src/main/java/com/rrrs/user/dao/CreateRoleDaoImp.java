package com.rrrs.user.dao;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.RrrUserCoreTableDtls;
import com.rrrs.user.entity.RrrHubList;
import com.rrrs.user.entity.UserRoleDtls;

@Repository
public class CreateRoleDaoImp implements CreateRoleDao {

	private EntityManager entityManager;
	public CreateRoleDaoImp(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	
	@Override
	public UserRoleDtls getCoreTableAndModule(Principal principal) {
		Session session = entityManager.unwrap(Session.class);
		/*
		 * Query <RrrHubList> hubDtls =
		 * session.createQuery("select hub from RrrHubList hub"); List<RrrHubList>
		 * hubAndModules = hubDtls.getResultList(); Query<RrrCoreTableDtls>
		 * coreTableDtls =
		 * session.createQuery("select coreTable from RrrCoreTableDtls coreTable");
		 * List<RrrCoreTableDtls> coreTables=coreTableDtls.getResultList();
		 * Map<String,Object> hubAndTable=new HashMap<>(); hubAndTable.put("hubDtls",
		 * hubAndModules); hubAndTable.put("coreTables", coreTables);
		 */
		System.out.println("principal.getName()-->"+principal.getName());
		Query roleIdQuery = session.createQuery("select user.selectUserRole from CurrentUser user where username=:username");
		roleIdQuery.setParameter("username", principal.getName());
		Integer roleId = (Integer)roleIdQuery.uniqueResult();
		System.out.println("roleId==>"+roleId);
		Query <UserRoleDtls> query = session.createQuery("select role from UserRoleDtls role where role.roleId=:userId");
		query.setParameter("userId", roleId);
		UserRoleDtls userRoleDtls = query.getSingleResult();
		return userRoleDtls;
	}

	@Override
	public boolean checkUniqueUserRole(String roleName) {
		Session session = entityManager.unwrap(Session.class);
		Query query = session.createQuery("Select count(urd.roleName) from UserRoleDtls urd where urd.roleName=:roleName");
		query.setParameter("roleName", roleName);
		Long count=(Long)query.uniqueResult();
		System.out.println("count-->"+count);
		if(count>0) {
			return false;
		}
		return true;
	}

	@Override
	public String saveUserRole(UserRoleDtls userRoleDtls) {
		String status="";
		System.out.println("userRoleDtls--->"+userRoleDtls);
		Session session = this.entityManager.unwrap(Session.class);
		Transaction tx = session.beginTransaction();
		try {
			session.persist(userRoleDtls);
			status="userRoleSaved";
			tx.commit();
		}catch (Exception e) {
			//tx.rollback();
			status="execptionWhileSavingData";
		}
		session.close();
		return status;
	}

}
