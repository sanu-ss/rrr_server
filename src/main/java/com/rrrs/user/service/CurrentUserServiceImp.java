package com.rrrs.user.service;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.rrrs.user.dao.CurrentUserDao;
import com.rrrs.user.entity.CurrentUser;

@Service
public class CurrentUserServiceImp  implements CurrentUserService{

	private final CurrentUserDao currentUserDao;
	public CurrentUserServiceImp(CurrentUserDao currentUserDao) {
		this.currentUserDao=currentUserDao;
	}
	@Override
	public CurrentUser getUserDtls(Principal principal) {
		return currentUserDao.getCurrentUserDetalis(principal.getName());
	}

}
