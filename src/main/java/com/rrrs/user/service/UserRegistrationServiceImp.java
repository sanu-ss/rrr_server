package com.rrrs.user.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.user.dao.UserRegistrationDao;
import com.rrrs.user.entity.CurrentUser;

@Service
public class UserRegistrationServiceImp implements UserRegistrationService {

	private UserRegistrationDao userRegistrationDao;
	public UserRegistrationServiceImp(UserRegistrationDao userRegistrationDao) {
		this.userRegistrationDao=userRegistrationDao;
	}
	@Override
	public Map<String, Object> getRequiredUserRegistrationData(Principal principal) {
		return this.userRegistrationDao.getRequiredUserRegistrationData(principal);
	}
	@Override
	public String saveUser(CurrentUser currentUser) {
		String status="";
		status=this.userRegistrationDao.chekUniqueUserNameEmailAndContact(currentUser);
		if(status.equalsIgnoreCase("success")) {
			return this.userRegistrationDao.saveUserData(currentUser);	
		}
		return status;
	}
	@Override
	public String updatePwd() {
		
		return this.userRegistrationDao.updateUserPwd();
	}

}
