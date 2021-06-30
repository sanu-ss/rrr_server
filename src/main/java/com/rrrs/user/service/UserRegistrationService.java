package com.rrrs.user.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.user.entity.CurrentUser;

@Service
public interface UserRegistrationService {

	public Map<String, Object> getRequiredUserRegistrationData(Principal principal);

	public String saveUser(CurrentUser currentUser);

	public String updatePwd();

}
