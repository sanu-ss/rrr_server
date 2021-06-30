package com.rrrs.user.service;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.rrrs.user.entity.CurrentUser;

@Service
public interface CurrentUserService {

	public CurrentUser getUserDtls(Principal principal);

}
