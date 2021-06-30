package com.rrrs.user.dao;

import org.springframework.stereotype.Repository;

import com.rrrs.user.entity.CurrentUser;

@Repository
public interface CurrentUserDao {

	public CurrentUser getCurrentUserDetalis(String name);

}
