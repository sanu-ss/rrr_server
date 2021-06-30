package com.rrrs.user.dao;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.CompanyDtls;
import com.rrrs.user.entity.CurrentUser;
import com.rrrs.user.entity.UserRoleDtls;
import com.rrrs.util.MailSend;
import com.rrrs.util.PWDGenerator;

@Repository
public class UserRegistrationDaoImp implements UserRegistrationDao{
	private PasswordEncoder passwordEncoder;
	private PWDGenerator pwdGenerator;
	private EntityManager entityManager;
	private MailSend mail;
	public UserRegistrationDaoImp(EntityManager entityManager,PasswordEncoder passwordEncoder,
			PWDGenerator pwdGenerator,MailSend mail) {
		this.entityManager=entityManager;
		this.passwordEncoder=passwordEncoder;
		this.pwdGenerator=pwdGenerator;
		this.mail=mail;
	}
	@Override
	public Map<String, Object> getRequiredUserRegistrationData(Principal principal) {
		Session session = entityManager.unwrap(Session.class);
		List<CompanyDtls> companyList=new ArrayList<>();
		Query companyQuery = session.createQuery("select company.id as id,company.companyName as name from CompanyDtls company");
		List<Object[]> rows = companyQuery.list();
		 for (Object[] row: rows) {
			 CompanyDtls company=new CompanyDtls();
			 company.setId((int)row[0]);
			 company.setCompanyName((String)row[1]);
			 companyList.add(company);
		}
		Query userRoleQuery = session.createQuery("select role.roleId,role.roleName from UserRoleDtls role");
		List<UserRoleDtls> roleList=new ArrayList<>();
		List<Object[]> list = userRoleQuery.list();
		 for (Object[] data: list) {
			 UserRoleDtls role=new UserRoleDtls();
			 role.setRoleId((int)data[0]);
			 role.setRoleName((String)data[1]);
			 roleList.add(role);
		}
		System.out.println("user role list "+roleList.size());
		Map<String,Object> resultList=new HashMap<>();
		resultList.put("companyList", companyList);
		resultList.put("roleList", roleList);
		return resultList;
	}
	@Override
	public String chekUniqueUserNameEmailAndContact(CurrentUser user) {
		Session session = this.entityManager.unwrap(Session.class);
		Query duplicateUserName = session.createQuery("select count(user.username) from CurrentUser user where user.username=:username");
		duplicateUserName.setParameter("username", user.getUsername());
		Long usernameCount =(Long)duplicateUserName.uniqueResult();
		if(usernameCount>0) {
			return "duplicateUsername";
		}
		Query duplicateEmail = session.createQuery("select count(user.email) from CurrentUser user where user.email=:email");
		duplicateEmail.setParameter("email", user.getEmail());
		Long duplicateEmailcount =(Long)duplicateEmail.uniqueResult();
		if(duplicateEmailcount>0) {
			return "duplicateEmail";
		}
		Query duplicatecontact = session.createQuery("select count(user.contactNumber) from CurrentUser user where user.contactNumber=:contactNumber");
		duplicatecontact.setParameter("contactNumber", user.getContactNumber());
		Long duplicatecontactCount =(Long)duplicatecontact.uniqueResult();
		if(duplicatecontactCount>0) {
			return "duplicatecontactCount";
		}
		
		return "success";
	}
	@Override
	public String saveUserData(CurrentUser currentUser) {
		String status="";
		String pwd=this.pwdGenerator.getpwd(8);
		currentUser.setPassword(this.passwordEncoder.encode(pwd));
		Session session = this.entityManager.unwrap(Session.class);
		Transaction tx = session.beginTransaction();
		try {
			session.persist(currentUser);
			status="success";
			this.mail.sendMailTo(currentUser.getEmail(),currentUser.getUsername(), pwd,
					currentUser.getFirstName()+" "+currentUser.getLastName());
			tx.commit();
			
		} catch (Exception e) {
			tx.rollback();
			System.out.println(e);
			status="errorWhileSaving";
		}
		session.close();
		return status;
	}
	@Override
	public String updateUserPwd() {
		Session session=this.entityManager.unwrap(Session.class);
		Transaction tx = session.beginTransaction();
		String username="admin";
		String pass="admin123";
		Query set = session.createQuery("update CurrentUser user set user.password=:password where user.username=:username");
		set.setParameter("username", username);
		set.setParameter("password", this.passwordEncoder.encode(pass));
		int executeUpdate = set.executeUpdate();
		System.out.println(executeUpdate);
		tx.commit();
		
		return null;
	}

}
