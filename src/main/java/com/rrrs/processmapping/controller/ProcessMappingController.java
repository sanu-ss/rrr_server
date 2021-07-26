package com.rrrs.processmapping.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.processmapping.entity.ProcessMappingDtls;
import com.rrrs.processmapping.service.ProcessMappingService;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.DownloadFileDtls;
import com.rrrs.util.ResponseData;
import com.rrrs.util.Util;

import net.bytebuddy.asm.Advice.This;

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
	@PostMapping(value = "/configFormat")
	public ResponseEntity<?> getConfigFormatDtls(@RequestBody MappingDetails mappingDetails,Principal principal){
		
		return new ResponseEntity<>(this.processMappingService.getConfigFormatDtls(mappingDetails,principal),HttpStatus.OK);
	}
	@PostMapping(value = "/getexecutionfiles/{startDate}/{endDate}")
	public ResponseEntity<?> getexecutionfiles(@RequestBody MappingDetails mappingDetails,Principal principal,@PathVariable("startDate") String start,@PathVariable("endDate") String end){
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//formatter = formatter.withLocale( putAppropriateLocaleHere );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
		LocalDate startDate = LocalDate.parse(start, formatter);
		LocalDate endDate = LocalDate.parse(end, formatter);
		boolean status=!(mappingDetails.getStartDate().isAfter(startDate)) && !(mappingDetails.getEndDate().isBefore(endDate));
		if(!status) {
			return new ResponseEntity<>(new ResponseData(" Upload start and end date should be inbetween validity period"), HttpStatus.BAD_REQUEST);
		}
		List<UploadFileDtls> fileList = this.processMappingService.getexecutionfiles(mappingDetails,principal,startDate,endDate);
		if(fileList==null || fileList.size()<1) {
			return new ResponseEntity<>(new ResponseData(" Please upload the file for given date range"),HttpStatus.BAD_REQUEST);
		}
		System.out.println("file data-->"+fileList);
		return new ResponseEntity<>(fileList,HttpStatus.OK);
	}
	
	@PostMapping(value = "/executemapping")
	public ResponseEntity<?> executemapping(@RequestBody ProcessMappingDtls processMappingDtls) throws CustomException{
		System.out.println("data-->"+processMappingDtls.getFilesDtls());
		return new ResponseEntity<>(this.processMappingService.executemapping(processMappingDtls),HttpStatus.OK);
	}
	@GetMapping(value = "/download/{triggerId}/{destTabId}")
	public ResponseEntity<?> getResultFile(@PathVariable("triggerId") Integer triggerId,
			@PathVariable("destTabId") Integer destTabId,Principal principal){
		System.out.println("triggerId-->"+triggerId+" destTabId-->"+destTabId);
		String fileDirectory=this.processMappingService.getResultFile(triggerId,destTabId,principal);
		System.out.println("fileDirectory-->"+fileDirectory);
		String tabName=this.processMappingService.getDestTableName(destTabId);
		System.out.println("tabName-->"+tabName);
		DownloadFileDtls file=new DownloadFileDtls();
	    Path filePath = Paths.get(fileDirectory.toString());
		if(!Files.exists(filePath)) {
			return new ResponseEntity<>(new ResponseData("Error while reading data form download location"),HttpStatus.BAD_REQUEST);
		}else {
			try {
				file.setFilename(tabName);
				file.setFile(Files.readAllBytes(filePath));
				file.setOrgfilename(tabName);
				file.setExtension(ConfigUtils.EXCEL_FILE_EXTENSION);
			} catch (IOException e) {
				e.printStackTrace();
				return new ResponseEntity<>(new ResponseData("Error while reading data form download location"),HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(file,HttpStatus.OK);
	}
	@GetMapping(value = "/downloadList/{mappingId}")	
	public ResponseEntity<?> getDownloadList(@PathVariable("mappingId") Integer mappingId){
		System.out.println("mappingId-->"+mappingId);
		return new ResponseEntity<>(this.processMappingService.getDownloadList(mappingId),HttpStatus.OK);
	}
	
}
