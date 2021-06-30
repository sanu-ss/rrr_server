package com.rrrs.user.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.user.entity.UserRoleDtls;

@Service
public interface CreateRoleService {

	public UserRoleDtls getCoreTableAndModule(Principal principal);

	public String saveUserRole(UserRoleDtls userRoleDtls);

}
