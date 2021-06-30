package com.rrrs.mappingconfig.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.mappingconfig.entity.MappingResponseData;
import com.rrrs.mappingconfig.entity.RrrProcessDtls;
import com.rrrs.mappingconfig.service.MappingConfigService;
import com.rrrs.util.BooleanResponseData;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.ResponseData;

@RestController
@RequestMapping(value = {"api/resource/mapping"})
public class MappingConfigController {
	private CurrentUserDbDtls currentUserDbDtls;
	private MappingConfigService mappingConfigService;
	private Logger logger=LoggerFactory.getLogger(MappingConfigController.class);
	public MappingConfigController(MappingConfigService mappingConfigService,CurrentUserDbDtls currentUserDbDtls) {
		this.mappingConfigService=mappingConfigService;
		this.currentUserDbDtls=currentUserDbDtls;
	}
	@GetMapping(value = {"/coreAndDestTable"})
	public ResponseEntity<?> getCoreAndDestTable(Principal principal){
		Map<String,Object> data=new HashMap<>();
		data=this.mappingConfigService.getCoreAndDestTable(principal);
		return new ResponseEntity<>(data,HttpStatus.OK);
	}
	
	@GetMapping(value = "/validateMappingName/{mappingId}/{mappingName}")
	public ResponseEntity<?> checkMapping(@PathVariable("mappingId") int mappingId,@PathVariable("mappingName") String mappingName,Principal principal){
		boolean checkMap = false;
		System.out.println(mappingId+"-->>"+mappingName);
		if(mappingId == 0)
			checkMap = mappingConfigService.checkMappingName(mappingName.trim().toUpperCase(), principal);

		if(mappingId != 0)
			checkMap = mappingConfigService.checkMappingNameWithId(mappingName.trim(),mappingId, principal);
		System.out.println("check map-->"+checkMap);
		return new ResponseEntity<>(new BooleanResponseData(checkMap),HttpStatus.OK);
	}
	@PostMapping(value = {"/sourcerelations"})
	public ResponseEntity<?> checkSourcerelations(@RequestBody MappingDetails mappingDetails,Principal principal){
		boolean status=this.mappingConfigService.checkSourcerelations(mappingDetails,principal);
		System.out.println("process details-->"+mappingDetails);
		return new ResponseEntity<>(new BooleanResponseData(status),HttpStatus.OK);
	}
	@PostMapping(value={"/columnmapping"},consumes={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> checkColunmMapping(@RequestBody MappingDetails mdetails,Principal pricipal){
		return new ResponseEntity<>(mappingConfigService.checkColunmMapping(mdetails,pricipal),HttpStatus.OK);
	}
	@PostMapping(value= {"/mappingcreate"},consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> saveAndUpdateMapping(@RequestBody MappingDetails mappingDetails,Principal principal){
		String username=principal.getName();
		int companyId = this.currentUserDbDtls.getCompanyId(username);
		mappingDetails.getRrrCommonDtls().setCompanyId(companyId);
		mappingDetails.getRrrCommonDtls().setUserIns(username);
		mappingDetails.getRrrCommonDtls().setDateIns(LocalDateTime.now());
		
		System.out.println("mapping data-->"+mappingDetails);
		
		if(mappingDetails.getMode().equals("save")) {
			boolean checkmap=false;
			if(mappingDetails.getMappingId()==0)
				checkmap=mappingConfigService.checkMappingName(mappingDetails.getMappingName().trim(), principal);
			if(mappingDetails.getMappingId()!=0)
				checkmap=mappingConfigService.checkMappingNameWithId(mappingDetails.getMappingName(), mappingDetails.getMappingId(), principal);
			System.out.println("check up-->"+checkmap);
			if(checkmap)
				return new ResponseEntity<>(new ResponseData("Mapping name already used, Try another name"),HttpStatus.CONFLICT);		
		}
		String modeOfprocess = "";
		String message = "";
		if(mappingDetails.getMappingId() !=0) 
			modeOfprocess=mappingConfigService.getmodeOfProcess(mappingDetails.getMappingId(),principal);
		Integer processId = mappingConfigService.saveAndUpdateMapping(mappingDetails,principal);		
		
		if ("SAVE".equalsIgnoreCase(mappingDetails.getMode())){
			message = "Your mapping progress saved";
		}else if (("CREATE".equalsIgnoreCase(mappingDetails.getMode()) && (mappingDetails.getMappingId() == 0))){
			message = "Mapping has been created successfully";
		}else {
			if(mappingDetails.getMappingId() != 0){
				if((mappingDetails.getMappingId() != 0) && (modeOfprocess.equalsIgnoreCase("CREATE")) && ("CREATE".equalsIgnoreCase(mappingDetails.getMode())))
					message = "Mapping has been updated successfully";
				else
					message = "Mapping has been created successfully";
			}
		}
		System.out.println("last datat-->"+mappingDetails);
		return new ResponseEntity<>(new MappingResponseData(mappingDetails.getMappingId(),message),HttpStatus.OK);
	}
	
	
}
