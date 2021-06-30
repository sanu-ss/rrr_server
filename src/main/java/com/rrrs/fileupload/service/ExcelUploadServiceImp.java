package com.rrrs.fileupload.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Service;

import com.monitorjbl.xlsx.StreamingReader;
import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.dao.ExcelUploadDao;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.ReadPropertyFile;
@Service
public class ExcelUploadServiceImp implements ExcelUploadService {
	private Logger logger=LoggerFactory.getLogger(ExcelUploadServiceImp.class);
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("predefinederror/predefinederror.properties");
	private ExcelUploadDao excelUploadDao;
	public ExcelUploadServiceImp(ExcelUploadDao excelUploadDao) {
		this.excelUploadDao=excelUploadDao;
	}
	@Override
	public Object excelUpload(String filePath, ConfigInfo configInfo, UploadFileDtls uploadFileDtls,
			String username) throws CustomException {
		List<Map<Integer,String>> sheetData=new ArrayList<>();
		Map<String,Object> fileDetails = new HashMap<>();
		//Map<String, Object> processDtls = new HashMap<>();
		Object status=null;
		int uploadId = 0;
		String errorMsg = null;
		int errorNumber = 0;
		try(InputStream is = new FileInputStream(new File(filePath));
				Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)){
			
			//reading all data from file and storing in map where key will be column id and value will be the sheet data
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				Map<Integer,String> rowData=new HashMap<>();
				for (Cell cell : row) {
					rowData.put(cell.getColumnIndex(), cell.getStringCellValue());
				}
				//reading full column data and storing in list so size of the list will be same as size of the row in sheet
				sheetData.add(rowData);
			}
			//first row of the list will store columns name of the file so we should remove the first row from list and remaining data we will save in database
			sheetData.remove(0);
			//now list store only required data to save in the format table
			
			status=this.excelUploadDao.uploadExcelData(configInfo,uploadFileDtls,sheetData,username);
			
		}catch (IOException | IllegalStateException e) {
			logger.info("error "+e);
			errorNumber++;			
			errorMsg = "We encountered an exception in your file , Please check the file";
		}catch (Exception e) {
			
			errorNumber++;
			logger.info("exception="+e.getCause().getMessage());
			List<String> errorLst=Arrays.stream(e.getCause().getMessage().split(":")).collect(Collectors.toList());			
			fileDetails.put("errormsg", e.getCause().getMessage().trim());
			fileDetails.put("filedtls", configInfo);
			fileDetails.put("diserrmsg", readPropertyFile.getProperty(errorLst.get(0).trim()));
			fileDetails.put("errorCode", errorLst.get(0));
			fileDetails.put("sheetdata", sheetData);
			if(this.readPropertyFile.getProperty(errorLst.get(0))!=null)
				errorMsg = ConfigUtils.getErrmsg(fileDetails); 
			else
				errorMsg = "We encountered an exception in your file , Please check the file";
		}
		if(errorMsg != null){
			ConfigUtils.removeFilefromDirectory(filePath);
			throw new CustomException("500", errorMsg);
		}	
		return status;
	}
}
