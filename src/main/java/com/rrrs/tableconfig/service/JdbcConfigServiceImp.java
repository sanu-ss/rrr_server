package com.rrrs.tableconfig.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rrrs.exception.module.CustomException;
import com.rrrs.tableconfig.dao.JdbcConfigDao;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;

@Service
public class JdbcConfigServiceImp implements JdbcConfigService {

	private JdbcConfigDao jdbcConfigDao;
	private Logger logger=LoggerFactory.getLogger(JdbcConfigServiceImp.class);
	public JdbcConfigServiceImp(JdbcConfigDao jdbcConfigDao) {
		this.jdbcConfigDao=jdbcConfigDao;
	}
	@Override
	public boolean checkTable(String tablename) {
		this.logger.info("Checking table name before creating for given formats in service class");
		return this.jdbcConfigDao.checkTable(tablename);
	}

	@Override
	public boolean checkFormat(String formatname) {
		
		return this.jdbcConfigDao.checkFormat(formatname);
	}

	@Override
	public boolean persistCsv(ConfigInfo format) {
		format=this.jdbcConfigDao.setRequidFieldData(format);
		System.out.println("dat111___"+format);
		boolean status= this.jdbcConfigDao.persistConfigAndMapInfoDtls(format);
		return status;
	}
	@Override
	public List<ConfigInfo> getFormatList(String userIns) {
		return this.jdbcConfigDao.getFormatList(userIns);
	}
	@Override
	public String getFile(int configId, String formatTableStructureDirectory, Map<String, List<String>> colAndDatatype,
			String fileName,String username) throws CustomException {
		String fileDirectory= this.jdbcConfigDao.getFile( configId,  formatTableStructureDirectory,  colAndDatatype, fileName,username);
		if(fileDirectory.equals("false"))
			throw new CustomException("MDFE", "Error occurred while downloading file, please contact admin");
		return fileDirectory;
	}
	@Override
	public boolean updateFormats(ConfigInfo format) {
		format=this.jdbcConfigDao.setRequidFieldData(format);
		System.out.println("dat111___"+format);
		return this.jdbcConfigDao.updateFormats(format);
	}
	@Override
	public boolean getFormatsUse(int configId) {
		return this.jdbcConfigDao.getFormatUse(configId);
	}
	@Override
	public boolean checkUpdateFormat(String formatname,int configId) {
		return this.jdbcConfigDao.checkUpdateFormat(formatname,configId);
	}
	@Override
	public boolean checkUpdateTable(String tablename, int configId) {
		return this.jdbcConfigDao.checkUpdateTable(tablename,configId);
	}
	@Override
	public boolean deleteFormat(Integer formatId) {
		return this.jdbcConfigDao.deleteFormat(formatId);
	}
	@Override
	public List<MapInfo> getFormatMappingData(int formatId, int companyId) {
		return jdbcConfigDao.getFormatMappingData(formatId,companyId);
	}

}
