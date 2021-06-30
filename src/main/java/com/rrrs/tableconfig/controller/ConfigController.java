package com.rrrs.tableconfig.controller;

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

import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monitorjbl.xlsx.StreamingReader;
import com.rrrs.exception.module.CustomException;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;
import com.rrrs.tableconfig.service.JdbcConfigService;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.DownloadFileDtls;
import com.rrrs.util.ReadPropertyFile;
import com.rrrs.util.ResponseData;
import com.rrrs.util.Util;

@RestController
@RequestMapping(value = "api/resource/tableconfig")
public class ConfigController {
    private Logger logger=LoggerFactory.getLogger(ConfigController.class);
	private Environment environment;
	private JdbcConfigService jdbcConfigService;
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("requiredFolderPath/foldderPath.properties");
	public ConfigController(Environment environment,JdbcConfigService jdbcConfigService) {
		this.environment=environment;
		this.jdbcConfigService=jdbcConfigService;
	}
	private String formatTableStructureDirectory=this.readPropertyFile.getProperty("formatTableStructure.dir");

	@PostMapping(value = { "/excel" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> fileUpload(@RequestParam MultipartFile file) throws IOException, CustomException{
		//This list will keep all the details of file column and converted file column to table column
		List<MapInfo> map = new ArrayList<>();
		
		//creating a folder directory so uploaded file will be available on uploadedDate folder.
		String configDirectory=this.environment.getProperty("config.dir");
		StringBuilder directoryBuilder =  new StringBuilder(configDirectory) .append( File.separator).append(ConfigUtils.currentDateString());
		StringBuilder fileDirectory = new StringBuilder(directoryBuilder);
		//if file is not empty then only read operation will be performed else return empty file error
		if(file.isEmpty()) {
			return new ResponseEntity<>(new ResponseData("Uploaded file is empty, Please upload valid file"),HttpStatus.OK);
		}
		if (!file.getOriginalFilename().endsWith(ConfigUtils.EXCEL_FILE_EXTENSION)){
			return new ResponseEntity<>(new ResponseData("Please select valid file"), HttpStatus.OK);
		}
		//if user will upload more then time same file then it should be store to achive this we can append the file name with current datetime
		fileDirectory.append( File.separator).append( Util.filenameWithDate(file.getOriginalFilename()));
		
		//check file fileDirectory is available or not if not first create the file directory
		Path path = Paths.get(directoryBuilder.toString());
		if(!Files.exists(path))
			Files.createDirectories(path);
		//create file inside the above directory
		Path createFile = Paths.get(fileDirectory.toString());
		Files.createFile(createFile);
		//transfer the comming file data on this created file
		file.transferTo(createFile.toFile());
		//checking file is encrypted or not if encrypted the remove the file form above directory and rise the error
		boolean encriptedFile = ConfigUtils.isEncrypted(fileDirectory.toString());

		if(encriptedFile){
			ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
			return new ResponseEntity<>(new ResponseData("Please unlock the selected file"), HttpStatus.BAD_REQUEST);
		}
		try (InputStream is = new FileInputStream(new File(fileDirectory.toString()));
				Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {
		
			//workbook will give the excel file where we can  create sheet 
			//each sheet will be having row and cell to write the data on it
			//now we have excel file.create excel sheet
			Sheet sheet = workbook.getSheetAt(0);
			//first row of the sheet will give the column of the table 
			//check column name should not be empty
			List<String> colums = new ArrayList<>();
			String checkNull = null;
			int colCount=0;

			for(Row row : sheet) {
				for(Cell cell : row){

					if(cell.getColumnIndex() != colCount){
						checkNull= "File Columns should not be empty or null";
						colCount++;
					}
						
					colums.add(cell.getStringCellValue());
					colCount++;
				}
				break;
			}
			//remove all the columns which is either null or empty
			List<String> checkColumns = ConfigUtils.checkFileColumn(colums);
			//check column name should not be duplicate otherwise we will get error while creating the format table
			List<String> duplicateHeaders = ConfigUtils.checkColumnNameDuplicate(colums);
			//check column name should not contain special characters.
			List<String> specialCharacter = ConfigUtils.checkSpecialCharacter(colums);				
			
			String errorMsg=null;
			
			if(checkNull != null){
				errorMsg = checkNull;
			}else if(checkColumns.contains("false")){
				errorMsg = "File Columns should not be empty or null";
			}else if(!specialCharacter.isEmpty()){					
				errorMsg = "File Columns should not be a special character :" +specialCharacter.stream().collect(Collectors.joining(","));
			}else if (!duplicateHeaders.isEmpty()){			
				errorMsg = "File Columns should not be duplicate :"+ duplicateHeaders.stream().collect(Collectors.joining(","));
			}
			System.out.println("error message-->"+errorMsg);
			if(errorMsg != null){
				ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
				return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.CONFLICT);
			}
			List<String> tabcols = ConfigUtils.convertFileColumnNametoTableColumnName(colums);

			MapInfo m = null;
			for (int i = 0; i < colums.size(); i++) {
				m = new MapInfo();
				m.setSeqId(i + 1);
				m.setCol(colums.get(i));
				m.setTabcol(tabcols.get(i));
				m.setDefaultvalue("Y");
				map.add(m);			
			}

			logger.info("colums : " + colums.toString());
			logger.info("tabcols : " + tabcols.toString());

		} catch (IOException e) {
			ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
			logger.error("Exception IOException : " + e);
			throw new CustomException("500", "Error while processing the excel document , Please contact admin");
		}catch(Exception e){
			ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
			logger.error("Exception : " + e);
			throw new CustomException("500", "Error while processing the excel document , Please contact admin");
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	
	@PostMapping(value = { "/csvformats", "/excelformats" ,"/formats/csv","/formats/xlsx" }, consumes = { //formats/csv,formats/excel
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> createCsvFomarts(@Valid @RequestBody ConfigInfo format, BindingResult result,Principal principal)throws CustomException {
        String userIns=principal.getName();
        format.getRrrCommonDtls().setUserIns(userIns);
		logger.info("Inside createCsvFomart method ");
		if (result.hasErrors()) {
			FieldError filederror = result.getFieldError();
			logger.info(">>> has errors : >>> " + filederror.getDefaultMessage());
			List<String> errorsList = new ArrayList<>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError error : errors) {
				errorsList.add(error.getRejectedValue() + " >> " + error.getDefaultMessage());
				logger.info(">>> errors  >>> " + error.getRejectedValue() + "-" + error.getObjectName() + " - "+ error.getDefaultMessage());
			}
			return new ResponseEntity<>(new ResponseData( filederror.getRejectedValue() + " : " + filederror.getDefaultMessage()),HttpStatus.OK);
		}
		String errorMsg = null;
		List<String> duplicateHeaders = ConfigUtils.checkColumnNameDuplicate(format.getMappings().stream().map(MapInfo::getCol).collect(Collectors.toList()));
		if(!duplicateHeaders.isEmpty()) {
			errorMsg =  "File Columns should not be duplicate :"+ duplicateHeaders.stream().collect(Collectors.joining(","));
		  return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		List<String> duplicateColumns = ConfigUtils.checkColumnNameDuplicate(format.getMappings().stream().map(MapInfo::getTabcol).collect(Collectors.toList()));
		if(!duplicateColumns.isEmpty()) {
			errorMsg = "Database Columns should not be duplicate :"+ duplicateColumns.stream().collect(Collectors.joining(","));
		  return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		List<String> defaultDuplicatecolumn = ConfigUtils.checkDefaultDuplicate(format.getMappings().stream().map(MapInfo::getTabcol).collect(Collectors.toList()));
		if(!defaultDuplicatecolumn.isEmpty()) {
			errorMsg = "Database Columns should not contains :"+ defaultDuplicatecolumn.stream().collect(Collectors.joining(","));			
			return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		List<String> datasizevalidate = ConfigUtils.checkDatatypeSize(
				format.getMappings().stream().map(MapInfo::getDatatype).collect(Collectors.toList()),
				format.getMappings().stream().map(MapInfo::getDatasize).collect(Collectors.toList()));
		if(!datasizevalidate.isEmpty()) {
			errorMsg = "Data size should be 1 to 4000 digits for:"+ datasizevalidate.stream().collect(Collectors.joining(","));
		  return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		
		boolean table = jdbcConfigService.checkTable(format.getTablename());
		if(table) {
			errorMsg = "Table name already exists in database";
			return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		boolean formatflag = jdbcConfigService.checkFormat(format.getFormatname());
		if(formatflag) {
			errorMsg = "Format name already exists in database";
		    return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		boolean status=jdbcConfigService.persistCsv(format);
		if(status)
			return new ResponseEntity<>(new ResponseData("Format created successfully"), HttpStatus.CREATED);
		else
			return new ResponseEntity<>(new ResponseData("error while creating original and shadow table"), HttpStatus.OK);
	}
	
	//getting all format list
	@GetMapping(value = {"/formatList"},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getFormatList(Principal principal){
		String userIns=principal.getName();
		return new ResponseEntity<>( this.jdbcConfigService.getFormatList(userIns),HttpStatus.OK);
	}
	@GetMapping(value = {"/formatMappingData/{formatId}/{companyId}"},produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getFormatMappingData(@PathVariable("formatId") int formatId,@PathVariable("companyId") int companyId){
		System.out.println("getting format mapping for "+formatId);
		logger.info("get format mapping info");
		return new ResponseEntity<>(this.jdbcConfigService.getFormatMappingData(formatId,companyId),HttpStatus.OK);
	}
	@GetMapping(value = {"/formatDownload/{configId}"})
	public ResponseEntity<?> formatDownload(@PathVariable("configId") int configId,Principal principal) throws IOException, CustomException{
		System.out.println(configId);
		 String username=principal.getName();
		 List<String> column=new ArrayList<>();
		 List<String> dataType=new ArrayList<>();
		 column.add("FILE COLUMN");
		 column.add("DATABASE COLUMN");
		 column.add("DATATYPE");
		 column.add("LENGTH");
		 column.add("NULLABLE");
		
		 dataType.add("String");
		 dataType.add("String");
		 dataType.add("String");
		 dataType.add("String");
		 dataType.add("String");
		 
		 Map<String,List<String>> colAndDatatype=new HashMap<>();
		 colAndDatatype.put("column", column);
		 colAndDatatype.put("datatype", dataType);
		 String fileName="FormatTableDecription";
		 
		 
		 Path path = Paths.get(formatTableStructureDirectory);
		 if(!Files.exists(path)) {
			 Files.createDirectories(path);
		 }
		 //xlsx file creation and writing data on it 
		 String fileDirectory=this.jdbcConfigService.getFile(configId,formatTableStructureDirectory,colAndDatatype,fileName,username);
		 //pdf file data
		 //String fileDirectory=formatTableStructureDirectory+"resume-Nikhitha.pdf";
		 DownloadFileDtls file=new DownloadFileDtls();
		 byte[] data=null;
		 try {
			 file.setFilename(fileName);
			 file.setExtension(ConfigUtils.EXCEL_FILE_EXTENSION);
			 file.setFilepath(formatTableStructureDirectory);
			 Path path2 = Paths.get(fileDirectory);
			 data = Files.readAllBytes(path2);
		} catch (Exception e) {
			ConfigUtils.removeFilefromDirectory(fileDirectory.toString());
			throw new CustomException("","Exception while dowloading the file, please contact help desk");
		}
		
		 file.setFile(data);
		return new ResponseEntity<>(file,HttpStatus.OK);
	}
	
	@PutMapping(value = {"/updateFormat"},consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> updateFormatDtls(@Valid @RequestBody ConfigInfo format, BindingResult result,Principal principal){
		String userIns=principal.getName();
        format.getRrrCommonDtls().setUserIns(userIns);
		
		System.out.println("data-->"+format);
		if (result.hasErrors()) {
			FieldError filederror = result.getFieldError();
			logger.info(">>> has errors : >>> " + filederror.getDefaultMessage());
			List<String> errorsList = new ArrayList<>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError error : errors) {
				errorsList.add(error.getRejectedValue() + " >> " + error.getDefaultMessage());
				logger.info(">>> errors  >>> " + error.getRejectedValue() + "-" + error.getObjectName() + " - "+ error.getDefaultMessage());
			}
			return new ResponseEntity<>(new ResponseData(filederror.getRejectedValue() + " : " + filederror.getDefaultMessage()),HttpStatus.OK);
		}
		
		String errorMsg = null;
		List<String> duplicateHeaders = ConfigUtils.checkColumnNameDuplicate(format.getMappings().stream().map(MapInfo::getCol).collect(Collectors.toList()));
		if(!duplicateHeaders.isEmpty()) {
			errorMsg =  "File Columns should not be duplicate :"+ duplicateHeaders.stream().collect(Collectors.joining(","));
		  return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		List<String> duplicateColumns = ConfigUtils.checkColumnNameDuplicate(format.getMappings().stream().map(MapInfo::getTabcol).collect(Collectors.toList()));
		if(!duplicateColumns.isEmpty()) {
			errorMsg = "Database Columns should not be duplicate :"+ duplicateColumns.stream().collect(Collectors.joining(","));
		  return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		List<String> defaultDuplicatecolumn = ConfigUtils.checkDefaultDuplicate(format.getMappings().stream().map(MapInfo::getTabcol).collect(Collectors.toList()));
		if(!defaultDuplicatecolumn.isEmpty()) {
			errorMsg = "Database Columns should not contains :"+ defaultDuplicatecolumn.stream().collect(Collectors.joining(","));			
			return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		List<String> datasizevalidate = ConfigUtils.checkDatatypeSize(
				format.getMappings().stream().map(MapInfo::getDatatype).collect(Collectors.toList()),
				format.getMappings().stream().map(MapInfo::getDatasize).collect(Collectors.toList()));
		if(!datasizevalidate.isEmpty()) {
			errorMsg = "Data size should be 1 to 4000 digits for:"+ datasizevalidate.stream().collect(Collectors.joining(","));
		  return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		
		boolean table = jdbcConfigService.checkUpdateTable(format.getTablename(),format.getConfigId());
		if(table) {
			errorMsg = "Table name already exists in database";
			return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		boolean formatflag = jdbcConfigService.checkUpdateFormat(format.getFormatname(),format.getConfigId());
		if(formatflag) {
			errorMsg = "Format name already exists in database";
		    return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		boolean flag = jdbcConfigService.getFormatsUse(format.getConfigId());
		if(flag) {
			errorMsg = "Format details used in application";
		    return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		boolean status=jdbcConfigService.updateFormats(format);
		if(status)
			return new ResponseEntity<>(new ResponseData("Format updated successfully"), HttpStatus.OK);
		else
			return new ResponseEntity<>(new ResponseData("error while creating original and shadow table"), HttpStatus.OK);
		
	}
	@DeleteMapping(value = {"/deleteFormat/{configId}"},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> deleteFormatDtls(@PathVariable("configId") Integer formatId){
		System.out.println("id-->"+formatId);
		String message="";
		boolean flag = jdbcConfigService.getFormatsUse(formatId);
		if(flag) {
			String errorMsg = "Format details used in application";
		    return new ResponseEntity<>(new ResponseData(errorMsg),HttpStatus.OK);		
		}
		boolean status=this.jdbcConfigService.deleteFormat(formatId);
		if(status)
			message="Format deleted successfully";
		else 
			message="Something  went wrong while deleting the format";
		return new ResponseEntity(new ResponseData(message),HttpStatus.OK);
	}
}
