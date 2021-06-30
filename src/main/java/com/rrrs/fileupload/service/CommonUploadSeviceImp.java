package com.rrrs.fileupload.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rrrs.fileupload.dao.CommonUploadDao;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.ReadPropertyFile;
@Service
public class CommonUploadSeviceImp implements CommonUploadSevice{
	private ReadPropertyFile errorRead=new ReadPropertyFile("predefinederror/predefinederror.properties");
	private Logger logger=LoggerFactory.getLogger(CommonUploadSeviceImp.class);
	private CommonUploadDao commonUploadDao;
	public CommonUploadSeviceImp(CommonUploadDao commonUploadDao) {
		this.commonUploadDao=commonUploadDao;
	}
	@Override
	public boolean checkFileUploaded(String fileName, int companyId) {
		return this.commonUploadDao.checkFileUpload(fileName,companyId);
	}
	@Override
	public boolean isConfigExist(String formatname) {
		return this.commonUploadDao.isConfigExist(formatname);
	}
	@Override
	public ConfigInfo getConfigDetails(String formatname) {
		return this.commonUploadDao.getConfigDetails(formatname);
	}
	@Override
	public Object uploadStatus(ConfigInfo configInfo, Map<String, Object> errorMsg,Session session) {
		this.logger.info("inside upload status method");
		Map<String, Object> errormsg = 	null;
		errormsg=this.commonUploadDao.uploadStatus(errorMsg,configInfo);
		StringBuilder message = new StringBuilder(errormsg.get("STATUS").toString()).delete(0, 1);
		String procErrorMsg = null;		
		String fileColname=null;
		
		if(errormsg.get("ERRCOLNAME") !=null && errormsg.get("ERRCOLNAME") !="") {
			List<MapInfo> map = configInfo.getMappings();
			List<String> fileColumn = map.stream().map(MapInfo::getCol).collect(Collectors.toList());
			List<String> tabColumn = map.stream().map(MapInfo::getTabcol).collect(Collectors.toList());
			int index=tabColumn.indexOf(errormsg.get("ERRCOLNAME"));
			fileColname=fileColumn.get(index);
		}
		if(!errormsg.get("STATUS").equals("PROCESSED")){
			if(this.errorRead.getProperty("ORA-0"+message)!=null){	
				if(ConfigUtils.isNumeric(errormsg.get("LINENO").toString())) {
					if(fileColname != null && fileColname != "" && fileColname != " ")
						procErrorMsg =  new StringBuilder(this.errorRead.getProperty("ORA-0"+message)).append(" for a file column").append("  '").append(fileColname).append(" at line number ").append(Integer.valueOf(errormsg.get("LINENO").toString())+2).toString();
					else
						procErrorMsg = new StringBuilder(this.errorRead.getProperty("ORA-0"+message)).toString();

				}else {
					procErrorMsg=this.errorRead.getProperty("ORA-0"+message);
				}
				
			}else {
				procErrorMsg = "We encountered an exception in your file , Please check the file";
			}
			return procErrorMsg;
			
		}
		//end of outer if
		return errormsg.get("STATUS");
	}

	
}
