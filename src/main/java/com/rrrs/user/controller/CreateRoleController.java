package com.rrrs.user.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.user.entity.UserRoleDtls;
import com.rrrs.user.service.CreateRoleService;
import com.rrrs.util.ResponseData;

@RestController
@RequestMapping(value = "api/resource")
public class CreateRoleController {

	private CreateRoleService createRoleService;
	
	public CreateRoleController(CreateRoleService createRoleService) {
		this.createRoleService=createRoleService;
	}
	@GetMapping("/getCoreTableAndModule")
	private ResponseEntity<?> getCoreTableAndModules(Principal principal){
		System.out.println("principal--"+principal);
		return new ResponseEntity<>(this.createRoleService.getCoreTableAndModule(principal),HttpStatus.OK);
	}
	@PostMapping("/saveUserRole")
	public ResponseEntity<?> saveUserRole(@RequestBody UserRoleDtls userRoleDtls){
		//System.out.println("user role ----->"+userRoleDtls);
		String status=this.createRoleService.saveUserRole(userRoleDtls);
		if(status.equalsIgnoreCase("duplicateUserRole")) {
			return new ResponseEntity<>(new ResponseData("DuplicateRole"),HttpStatus.OK);
		}else if(status.equalsIgnoreCase("execptionWhileSavingData")) {
			return new ResponseEntity<>(new ResponseData("exceptionWhileSavingTheUserRole"),HttpStatus.OK);
		}
		return new ResponseEntity<>(new ResponseData("success"),HttpStatus.OK);
	}
	
}
