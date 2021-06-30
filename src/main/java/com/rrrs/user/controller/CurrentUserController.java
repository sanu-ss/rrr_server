package com.rrrs.user.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.user.service.CurrentUserService;

@RestController
@RequestMapping(value = "/api/resource")
public class CurrentUserController {

	private final CurrentUserService currentUserService;
	//if only one constructor is there in class then from spring 4.3 version no need to use @autowire for dependency injection
	//if we have multiple constructors in a class then we must use @autowire annonation before one of the constructor so
	//spring framework will know that which dependency it have to inject
	
	public CurrentUserController(CurrentUserService currentUserService){
		this.currentUserService=currentUserService;
	}
	@GetMapping(value = "/currentuser")
	public ResponseEntity<?> userDetails(Principal principal){
		System.out.println("current user--"+principal);
		return new ResponseEntity<>(currentUserService.getUserDtls(principal),HttpStatus.OK);
	}
}
