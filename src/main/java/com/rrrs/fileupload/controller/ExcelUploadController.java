package com.rrrs.fileupload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monitorjbl.xlsx.StreamingReader;
import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.fileupload.service.CommonUploadSevice;
import com.rrrs.fileupload.service.ExcelUploadService;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.ReadPropertyFile;
import com.rrrs.util.ResponseData;
import com.rrrs.util.Util;

@RestController
@RequestMapping(value = "api/resource/fileUpload")
public class ExcelUploadController {
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("requiredFolderPath/foldderPath.properties");
	private ExcelUploadService excelUploadService;
	private CommonUploadSevice commonUploadService;
	private Logger logger=LoggerFactory.getLogger(ExcelUploadController.class);
	public ExcelUploadController(ExcelUploadService excelUploadService,CommonUploadSevice commonUploadService) {
		this.excelUploadService=excelUploadService;
		this.commonUploadService=commonUploadService;
	}
	private String uploadDirectory;

	@PostMapping(value = {"/uploadfileexcel"},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile multipartFile,
			@RequestParam("formatname") String formatname,Principal principal) throws IOException,CustomException{
		System.out.println(multipartFile.getOriginalFilename()+"--->"+formatname);
		String username=principal.getName();
		if(!multipartFile.getOriginalFilename().contains(formatname)) {
			logger.info("uploaded file name does not contain format name");
			return new ResponseEntity<>(new ResponseData("file does not contain the format name"),HttpStatus.BAD_REQUEST);
		}
		if(multipartFile.isEmpty()) {
			logger.info("uploaded file is empty");
			return new ResponseEntity<>(new ResponseData("fileIsEmpty"),HttpStatus.OK);
		}
		if(!multipartFile.getOriginalFilename().endsWith(ConfigUtils.EXCEL_FILE_EXTENSION)) {
			logger.info("uploaded file is not of type "+ConfigUtils.EXCEL_FILE_EXTENSION);
			return new ResponseEntity<>(new ResponseData("Please upload the valid file"),HttpStatus.BAD_REQUEST);
		}
		if(!this.commonUploadService.isConfigExist(formatname)) {
			logger.info("Provided format name does not exist");
			return new ResponseEntity<>(new ResponseData("format name does not exist"),HttpStatus.BAD_REQUEST);
		}
		uploadDirectory=this.readPropertyFile.getProperty("upload.dir");
		StringBuilder directoryBuilder=new StringBuilder(uploadDirectory).append(File.separator).append(ConfigUtils.currentDateString());
		StringBuilder fileDirectory=new StringBuilder(directoryBuilder);
		logger.info("Content type : " + multipartFile.getContentType() +" > Name  : "+multipartFile.getName()+" > Original filename : " + multipartFile.getOriginalFilename()+" > Size : " + multipartFile.getSize()+" formatName "+formatname);
		fileDirectory=fileDirectory.append(File.separator).append(Util.filenameWithDate(multipartFile.getOriginalFilename()));
		Path folderPath = Paths.get(directoryBuilder.toString());
		Path filePath = Paths.get(fileDirectory.toString());
		if(!Files.exists(folderPath))
			Files.createDirectories(folderPath);
		if(!Files.exists(filePath))
			Files.createFile(filePath);
		
		multipartFile.transferTo(filePath);
		if(ConfigUtils.isEncrypted(fileDirectory.toString())) {
			logger.info("File is locked.Please unlock it");
			return new ResponseEntity<>(new ResponseData("lockedFile"),HttpStatus.OK);
		}
		try (InputStream is = new FileInputStream(new File(fileDirectory.toString()));
				Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {
			
			//reading file header
			List<String> colums = new ArrayList<>();
			Sheet sheet = workbook.getSheetAt(0);
			logger.debug("After getting the sheet details");
			int colCount=0;
			
			for(Row row : sheet) {
				for(Cell cell : row){

					if(cell.getColumnIndex() != colCount){
						colums.add("");
						colCount++;
					}

					colums.add(cell.getStringCellValue());
					colCount++;
				}
				break;
			}
			//header reading end
			
			//now we have to compare uploaded file header with DB file header
			
			//getting config data from Db
			ConfigInfo configInfo=this.commonUploadService.getConfigDetails(formatname);
			
			//extracting Db File Header
			List<MapInfo> map = configInfo.getMappings();
			List<String> dbColumn = map.stream().map(MapInfo::getCol).collect(Collectors.toList());
			//List<String> datatype= map.stream().map(MapInfo::getDatatype).collect(Collectors.toList());
			//List<Integer> dataSize = map.stream().map(MapInfo::getDatasize).collect(Collectors.toList());
			//List<String> defaultValues = map.stream().map(MapInfo::getDefaultvalue).collect(Collectors.toList());
			logger.info("file column--"+colums);
			logger.info("Db column--"+dbColumn);
			if(dbColumn !=null && !dbColumn.equals(colums)) {
				logger.info("file column not matching");
				ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
				return new ResponseEntity<>(new ResponseData("file columns not matching"),HttpStatus.CONFLICT);
			}
			UploadFileDtls uploadFileDtls=new UploadFileDtls();
			uploadFileDtls.setFileName(Util.filenameWithDate(multipartFile.getOriginalFilename()));
			uploadFileDtls.setConfigId(configInfo.getConfigId());
			uploadFileDtls.setFilePath(ConfigUtils.currentDateString());
			uploadFileDtls.setStatus("OK");
			uploadFileDtls.setFileSize(multipartFile.getSize());
			uploadFileDtls.setArchiveStatus("N");
			uploadFileDtls.setTypeOfUpload("MANUAL");
			uploadFileDtls.setDonotProcess("P");
			uploadFileDtls.setOriginalFileName(Util.filenameWithCaseInsensitive(multipartFile.getOriginalFilename()));
			//Map<String,Object> data=new HashMap<>();
			//data.put("configInfo", configInfo);
			//data.put("typeofupload","MANUAL");
			//data.put("process", "P");
			//data.put("originalfname",Util.filenameWithCaseInsensitive(multipartFile.getOriginalFilename()));
			System.out.println("calling method");
			Object status=this.excelUploadService.excelUpload(fileDirectory.toString(), configInfo, uploadFileDtls,username);
			//Object status = commonUploadService.uploadStatus(configInfo,errorMsg);
			if(!status.equals("PROCESSED")){
				ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
				throw new CustomException("500", status.toString());
			}
		}catch (IllegalStateException | IOException  e) {
			ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
			logger.error("Exception : " + e);
			throw new CustomException("500", "Error while processing the xlsx document , Please contact admin");
		}
		return new ResponseEntity<>(new ResponseData( "file data is successfully uploaded"), HttpStatus.OK);
	}

}
