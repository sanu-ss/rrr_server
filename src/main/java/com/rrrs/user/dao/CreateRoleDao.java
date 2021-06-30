package com.rrrs.user.dao;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.UserRoleDtls;

@Repository
public interface CreateRoleDao {

	public UserRoleDtls getCoreTableAndModule(Principal principal);

	public boolean checkUniqueUserRole(String roleName);

	public String saveUserRole(UserRoleDtls userRoleDtls);

}
