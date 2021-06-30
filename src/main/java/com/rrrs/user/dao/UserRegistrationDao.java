package com.rrrs.user.dao;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.CurrentUser;

@Repository
public interface UserRegistrationDao {

	public Map<String, Object> getRequiredUserRegistrationData(Principal principal);

	public String chekUniqueUserNameEmailAndContact(CurrentUser user);

	public String saveUserData(CurrentUser currentUser);

	public String updateUserPwd();

}
