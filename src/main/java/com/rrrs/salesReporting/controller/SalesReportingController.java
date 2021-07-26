package com.rrrs.salesReporting.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.salesReporting.entities.RRRSalesReportingDtls;
import com.rrrs.salesReporting.service.SalesReportingService;

import net.bytebuddy.asm.Advice.This;

@RestController
@RequestMapping(value = "api/resource/salesreporting")
public class SalesReportingController {

	private SalesReportingService salesReportingService;
	SalesReportingController(SalesReportingService salesReportingService){
		this.salesReportingService=salesReportingService;
	}
	
	@GetMapping(value = "/getCoreAndSourceTab")
	public ResponseEntity<?> getCoreAndSourceTabDtls(Principal principal ){
		
		return new ResponseEntity<>(this.salesReportingService.getCoreAndSourceTabDtls(principal),HttpStatus.OK);
	}
	@GetMapping(value = "/checkReporting/{reportName}")
	public ResponseEntity<?> checkReportingName(@PathVariable("reportName")String reportName,Principal principal ){
		
		return new ResponseEntity<>(this.salesReportingService.checkReportingName(reportName,principal),HttpStatus.OK);
	}
	@PostMapping(value = "/checkRelation")
	public ResponseEntity<?> checkRelation(@RequestBody RRRSalesReportingDtls salesReportingDtls,Principal principal ){
		
		return new ResponseEntity<>(this.salesReportingService.checkRelation(salesReportingDtls,principal),HttpStatus.OK);
	}

//	@PostMapping(value = "/createrule")
//	public ResponseEntity<?> createrule(@RequestBody RRRSalesReportingDtls salesReportingDtls,Principal principal ){
//		
//		return new ResponseEntity<>();
//	}
}
