package com.rrrs.processmapping.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.processmapping.dao.ProcessMappingDao;
import com.rrrs.processmapping.entity.DownloadAgain;
import com.rrrs.processmapping.entity.ProcessMappingDtls;
import com.rrrs.processmapping.entity.TriggerDetails;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.DownloadFileDtls;
import com.rrrs.util.ReadPropertyFile;
import com.rrrs.util.ResponseData;
import com.rrrs.util.Util;

@Service
public class ProcessMappingServiceImp implements ProcessMappingService{

	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("requiredFolderPath/foldderPath.properties");
	private ProcessMappingDao processMappingDao;
	private CurrentUserDbDtls currentUserDbDtls;
	public ProcessMappingServiceImp(ProcessMappingDao processMappingDao,CurrentUserDbDtls currentUserDbDtls) {
		this.processMappingDao=processMappingDao;
		this.currentUserDbDtls=currentUserDbDtls;
	}
	@Override
	public List<MappingDetails> getMAppingList(Principal principal) {
		return this.processMappingDao.getMappingList(principal);
	}
	@Override
	public String getCoreTableName(Integer tableId) {
		return this.processMappingDao.getCoreTableName(tableId);
	}
	
	@Override
	public List<UploadFileDtls> getexecutionfiles(MappingDetails mappingDetails, Principal principal,
			LocalDate startDate, LocalDate endDate) {
		return this.processMappingDao.getexecutionfiles(mappingDetails,principal,startDate,endDate);
	}
	@Override
	public List<ConfigInfo> getConfigFormatDtls(MappingDetails mappingDetails, Principal principal) {
		return this.processMappingDao.getConfigFormatDtls(mappingDetails,principal);
	}
	@Override
	public TriggerDetails executemapping(ProcessMappingDtls processMappingDtls) throws CustomException {
		return this.processMappingDao.executemapping(processMappingDtls);
	}
	@Override
	public String getResultFile(Integer triggerId,Integer destTabId,Principal principal) {
		//core table column details
		System.out.println("fileDirectory");
		Map<String,List<String>> destColDtls=this.processMappingDao.getResultFile(destTabId);
		System.out.println("1...");
		String coreTableName=this.processMappingDao.getDestTableName(destTabId);
		System.out.println("2...");
		String processDirectory = this.readPropertyFile.getProperty("process.dir");
		System.out.println("3...");
		File directory = new File(processDirectory.toString());
	    if(!directory.exists()) {
	    	directory.mkdir();
	    }
	    System.out.println("processDirectory 2-->"+processDirectory);
	    int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		RrrCommonDtls rrrCommonDtls=new RrrCommonDtls();
		rrrCommonDtls.setUserIns(principal.getName());
		rrrCommonDtls.setCompanyId(companyId);
	    String fileDirectory=this.processMappingDao.getDestFile(triggerId,processDirectory,destColDtls,coreTableName,rrrCommonDtls);
		System.out.println("fileDirectory 3-->"+fileDirectory);
	    return fileDirectory;
	}
	@Override
	public String getDestTableName(Integer destTabId) {
		return this.processMappingDao.getDestTableName(destTabId);
	}
	@Override
	public List<DownloadAgain> getDownloadList(Integer mappingId) {
		return this.processMappingDao.getDownloadList(mappingId);
	}
	
}