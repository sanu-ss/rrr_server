package com.rrrs.user.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.user.dao.CreateRoleDao;
import com.rrrs.user.entity.UserRoleDtls;

@Service
public class CreateRoleServiceImp implements CreateRoleService {

	private CreateRoleDao createRoleDao;
	public CreateRoleServiceImp(CreateRoleDao createRoleDao) {
		this.createRoleDao=createRoleDao;
	}
	@Override
	public UserRoleDtls getCoreTableAndModule(Principal principal) {
		return this.createRoleDao.getCoreTableAndModule(principal);
	}
	@Override
	public String saveUserRole(UserRoleDtls userRoleDtls) {
		boolean checkUniqueUserRole = this.createRoleDao.checkUniqueUserRole(userRoleDtls.getRoleName());
		 if(!checkUniqueUserRole) {
			 return "duplicateUserRole";
		 }
			 return this.createRoleDao.saveUserRole(userRoleDtls);	
	}
	
	

}
