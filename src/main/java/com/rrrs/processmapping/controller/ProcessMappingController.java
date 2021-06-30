package com.rrrs.processmapping.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.processmapping.service.ProcessMappingService;
import com.rrrs.util.ResponseData;

@RestController
@RequestMapping(value = "api/resource/mappingProcess")
public class ProcessMappingController {
	
	private ProcessMappingService processMappingService;
	public ProcessMappingController(ProcessMappingService processMappingService) {
		this.processMappingService=processMappingService;
	}
	@GetMapping(value = "/mappingList")
	public ResponseEntity<?> getMappingList(Principal principal){
		List<MappingDetails> dtls=this.processMappingService.getMAppingList(principal);
		return new ResponseEntity<>(dtls,HttpStatus.OK);
	}
	@GetMapping(value = "/coreTable/{coreTableId}")
	public ResponseEntity<?> getCoreTableName(@PathVariable("coreTableId") Integer tableId){
		String coreTable=this.processMappingService.getCoreTableName(tableId);
		return new ResponseEntity<>(new ResponseData(coreTable),HttpStatus.OK);
	}
}
