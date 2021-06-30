package com.rrrs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;

public class ConfigUtils {

	public static String EXCEL_FILE_EXTENSION=".xlsx";
	static Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);
	private static ReadPropertyFile dataBase=new ReadPropertyFile("application.properties");
	public static String currentDateString(){
		LocalDate date=LocalDate.now();
		String format = date.format(DateTimeFormatter.ofPattern("ddMMMyyyy"));
		return format;
	}

	@SuppressWarnings("resource")
	public static boolean isEncrypted(String path) {

		try {
			LOGGER.info("enter into isencrypted");
			new POIFSFileSystem(new FileInputStream(path));	        
			return true;
		} catch (Exception e) {
			LOGGER.info("Exception->"+e.getMessage());	        
		}		
		return false;
	}

	public static void removeFilefromDirectory(String fileDirectory){
		LOGGER.info("ConfigUtils >>> Inside removeFilefromDirectory method");
		try {
			Files.deleteIfExists(Paths.get(fileDirectory));
		} catch (IOException e) {
			LOGGER.error("Error : Exception while removing data from "+fileDirectory);
		}
	}
	public static List<String> checkFileColumn(List<String> colums) {

		LOGGER.info("ConfigUtils >>> Inside checkFileColumn method checking column should not be null and empty");
		return colums.stream()
				.map(e->(!e.trim().equals(null) && !e.trim().equals("")))
				.map(e->e.toString())
				.collect(Collectors.toList());
	}
	public static List<String> checkColumnNameDuplicate(List<String> columns) {

		LOGGER.info("ConfigUtils >>> Inside checkColumnNameDuplicate method");
		List<String> duplicateElement = new ArrayList<>();
		HashSet<String> checkUnique = new HashSet<>(columns);
		for (String column : checkUnique) {
			LOGGER.info("Configutils ->"+Collections.frequency(columns, column) + "\t\t" + column);
			if (Collections.frequency(columns, column) > 1)
				duplicateElement.add(column);
		}
		return duplicateElement;
	}
	public static List<String> checkSpecialCharacter(List<String> colums) {

		LOGGER.info("ConfigUtils >>> Inside checkSpecialCharacter method");
		List<String> specialcharacter = new ArrayList<>();
		for(String val : colums){
			String specialCharacters = "[" + "-/@#!*$%^&~*,:;|`.'_+={}()\\[\\]"+ "]+" ;
			if ( val.matches(specialCharacters)) {
				specialcharacter.add(val);
			}			
		}
		return specialcharacter;
	}
	public static List<String> convertFileColumnNametoTableColumnName( List<String> fileColumnName){

		LOGGER.info("ConfigUtils >>> Inside convertFileColumnNametotableColumnName method");

		return fileColumnName.stream()
				.map(e -> e.trim().replaceAll("[" + "-/@#!*$%^&~*,:;|`.'_+={}()\\[\\]"+ "]+", ""))
				.map(e -> e.trim().replaceAll("\\s", "_"))
				.map(e -> e.replaceAll("[^a-zA-Z0-9_-]", ""))				
				.map(ConfigUtils::getCoulmnLength)
				.map(String::toUpperCase)
				.map(e -> e.trim())
				.collect(Collectors.toList());
	}
	public static String getCoulmnLength(String col) {
		if (col == null)
			return "";
		//if column length >30 return 0 to 29 character of column name else return full column name
		return ((col.length() > 30) ? col.substring(1, 30) : col);
	}

	public static List<String> checkDefaultDuplicate(List<String> columns) {

		LOGGER.info("ConfigUtils >>> Inside checkDefaultDuplicate method");	
		List<String> defaultColumns = Arrays.stream(new String[]{"SEQ_NO","FILE_UPLD_SEQ","COMPANY_ID","USER_INS","DATE_INS"}).collect(Collectors.toList());		
		List<String> dublicateColumns = new ArrayList<>();		

		for(String col:columns){
			if(defaultColumns.contains(col)){
				dublicateColumns.add(col);
			}		
		}
		return dublicateColumns;
	}

	public static List<String> checkDatatypeSize(List<String> datatype, List<Integer> dataSize) {

		LOGGER.info("ConfigUtils >>> Inside checkDatatypeSize method");
		List<String> datatypes=new ArrayList<>();
		for(int i=0;i<datatype.size();i++){

			if(datatype.get(i).equals("VARCHAR") || datatype.get(i).equals("VARCHAR2") || datatype.get(i).equals("CHAR") || datatype.get(i).equals("NCHAR") || datatype.get(i).equals("NVARCHAR2")){			
				LOGGER.info("enter into the condition");
				if(dataSize.get(i)<1 || dataSize.get(i)>4000){
					datatypes.add(datatype.get(i));
				}
			}			
		}
		return datatypes;		
	}

	public static Map<String, String> createQry(ConfigInfo format, int companyId) {


		LOGGER.info("ConfigUtils >>> Inside createQry method");
		List<MapInfo> map = format.getMappings();
		List<String> columnName = map.stream().map(MapInfo::getTabcol).collect(Collectors.toList());
		List<String> datatype= map.stream().map(MapInfo::getDatatype).collect(Collectors.toList());
		List<Integer> dataSize = map.stream().map(MapInfo::getDatasize).collect(Collectors.toList());
		List<String> defaultValues = map.stream().map(MapInfo::getDefaultvalue).collect(Collectors.toList());
		StringBuilder structuredQuery = new StringBuilder();
		StringBuilder shadowQuery = new StringBuilder();
		Map<String,String> queries = new HashMap<>();

		String constarints=" NOT NULL ";
		String createScript=null;
		String duplicateScript=null;
		String dataType=null;

		if(dataBase.getProperty("spring.datasource.driver-class-name").contains("OracleDriver")) {
			dataType="VARCHAR2";

			for(int i=0;i<columnName.size();i++){
				if(datatype.get(i).equals("NUMBER")){	
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+38+")"+constarints+",");
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+38+")"+",");
					}				
				}else if(datatype.get(i).equals("DATE") || datatype.get(i).equals("TIMESTAMP") ){
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+constarints+",");
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+",");
					}				
				}else{				
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+"("+dataSize.get(i).toString()+")"+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+dataSize.get(i).toString()+")"+constarints+",");		
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+"("+dataSize.get(i).toString()+")"+",");
						shadowQuery.append(columnName.get(i)+" "+datatype.get(i)+"("+dataSize.get(i).toString()+")"+" ,");
					}					
				}
			}	

			createScript="CREATE TABLE " + format.getTablename()+"_"+format.getRrrCommonDtls().getCompanyId()+"("+"SEQ_NO NUMBER , FILE_UPLD_SEQ NUMBER"+","+structuredQuery.toString()+"COMPANY_ID NUMBER,USER_INS VARCHAR2(30),DATE_INS DATE"+")";
			duplicateScript="CREATE TABLE " + format.getTablename()+"_SH"+"_"+companyId+"("+"SEQ_NO NUMBER , FILE_UPLD_SEQ NUMBER"+","+shadowQuery.toString()+"COMPANY_ID NUMBER,USER_INS VARCHAR2(30),DATE_INS DATE"+")";

		}else if(dataBase.getProperty("database.driver").contains("SQLServerDriver")) {
			dataType="VARCHAR";

			for(int i=0;i<columnName.size();i++){
				if(datatype.get(i).equals("NUMBER")){	
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+38+")"+constarints+",");
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+38+")"+",");
					}				
				}else if(datatype.get(i).equals("INT")){
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+constarints+",");
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+",");
					}				
				}else if(datatype.get(i).equals("DECIMAL")){
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+constarints+",");
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+",");
					}				
				}else if(datatype.get(i).equals("DATE") || datatype.get(i).equals("TIMESTAMP") ){
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+constarints+",");
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+50+")"+",");
					}				
				}else{				
					if(defaultValues.get(i).equals("N")){
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+"("+dataSize.get(i).toString()+")"+constarints+",");
						shadowQuery.append(columnName.get(i)+" "+dataType+"("+dataSize.get(i).toString()+")"+constarints+",");		
					}else{
						structuredQuery.append(columnName.get(i)+" "+datatype.get(i)+"("+dataSize.get(i).toString()+")"+",");
						shadowQuery.append(columnName.get(i)+" "+datatype.get(i)+"("+dataSize.get(i).toString()+")"+" ,");
					}					
				}
			}	

			createScript="CREATE TABLE " + format.getTablename()+"_"+companyId+"("+"SEQ_NO INT , FILE_UPLD_SEQ INT"+","+structuredQuery.toString()+"COMPANY_ID INT,USER_INS VARCHAR(30),DATE_INS DATE"+")";
			duplicateScript="CREATE TABLE " + format.getTablename()+"_SH"+"_"+companyId+"("+"SEQ_NO INT , FILE_UPLD_SEQ INT"+","+shadowQuery.toString()+"COMPANY_ID INT,USER_INS VARCHAR(30),DATE_INS DATE"+")";

		}


		queries.put("createScript", createScript);
		queries.put("duplicateScript", duplicateScript);

		return queries;
	}

	public static boolean getDownloadFile(String directoryBuilder, Workbook wb) {
		try {
			FileOutputStream out = new FileOutputStream(new File(directoryBuilder));
			wb.write(out);
			out.close();
			wb.close();
		} catch (Exception e) {				
			LOGGER.info("Exception->"+e.getMessage());	  
			return false;
		} 
		return true;
	}

	@SuppressWarnings("unchecked")
	public static String getErrmsg(Map<String, Object> fileDetails) {

		LOGGER.info("ConfigUtils >>> Inside getErrmsg method");		
		Pattern pattern = Pattern.compile("\"(.*?)\"");
		Matcher match = pattern.matcher(fileDetails.get("errormsg").toString());
		List<Integer> checkNulls = new ArrayList<>();
		List<Integer> checkSize = new ArrayList<>();
		List<Map<Integer, String>> sheetData = new ArrayList<>();
		List<String[]> rows = new ArrayList<>();
		StringBuilder errorMsg=null;
		String columnName="";
		Map<String, Object> dtls = (Map<String, Object>)fileDetails.get("filedtls");

		while (match.find()) { columnName=match.group(1); }									

		if(fileDetails.keySet().contains("sheetdata"))
			sheetData = (List<Map<Integer, String>>)fileDetails.get("sheetdata");

		if(fileDetails.keySet().contains("rows"))
			rows =  (List<String[]>)fileDetails.get("rows");

		String fileColname = ((List<String>)dtls.get("filecols")).get(((List<String>)dtls.get("tabcols")).subList(5, ((List<String>)dtls.get("tabcols")).size()).indexOf(columnName));
		Integer fileCol = ((List<String>)dtls.get("filecols")).indexOf(fileColname);
		Integer dataSizes = ((List<Integer>)dtls.get("datasize")).get(fileCol);	

		if(sheetData.size()>0){			
			for(int i=0;i<sheetData.size();i++){
				if(sheetData.get(i).get(fileCol)!=null && !sheetData.get(i).get(fileCol).equals("")){	
					if(dataSizes==0){
						if(sheetData.get(i).get(fileCol).length()>=38){ checkSize.add(i+2);	}
					}else{
						if(sheetData.get(i).get(fileCol).length()>=dataSizes){ checkSize.add(i+2); }
					}
				}else{
					checkNulls.add(i+2);
				}
			}
		}else{
			int lineNum=0;
			for (String[] values : rows){
				lineNum++;
				if(Arrays.asList(values).get(fileCol)!=null && !Arrays.asList(values).get(fileCol).equals("")){
					if(dataSizes==0){
						if(Arrays.asList(values).get(fileCol).length()>=dataSizes){ checkSize.add(lineNum+1); }						
					}else{
						if(Arrays.asList(values).get(fileCol).length()>=dataSizes){ checkSize.add(lineNum+1); }
					}
				}else{
					checkNulls.add(lineNum+1);
				}
			}
		}

		if(fileDetails.get("errorCode").toString().equals("ORA-12899")){
			if(checkSize.size() >= 10){
				errorMsg =  new StringBuilder(fileDetails.get("diserrmsg").toString()).append("  '").append(fileColname).append("'  ").append("at multiple lines");
			}else{	
				errorMsg =  new StringBuilder(fileDetails.get("diserrmsg").toString()).append("  '").append(fileColname).append("'  ").append("at line number ").append(checkSize.toString().substring(1, checkSize.toString().length()-1));}
		}else if(fileDetails.get("errorCode").toString().equals("ORA-01400")){
			if(checkNulls.size() >= 10){
				errorMsg =  new StringBuilder(fileDetails.get("diserrmsg").toString()).append("  '").append(fileColname).append("'  ").append("at multiple lines");
			}else{	
				errorMsg =  new StringBuilder(fileDetails.get("diserrmsg").toString()).append("  '").append(fileColname).append("'  ").append("at line number ").append(checkNulls.toString().substring(1, checkNulls.toString().length()-1));}
		}else{
			errorMsg =  new StringBuilder(fileDetails.get("diserrmsg").toString()).append("  '").append(fileColname).append("'  ");
		}

		return errorMsg.toString();	
	}
	
	public static boolean isNumeric(String str){

		LOGGER.info("ConfigUtils >>> Inside isNumeric method");
		return (str ==null) ? false : str.trim().matches("-?\\d+(\\.\\d+)?");  
	}


}
