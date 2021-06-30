package com.rrrs.tableconfig.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;
@Repository
public interface JdbcConfigDao {

	boolean checkTable(String tablename);

	boolean checkFormat(String formatname);

	boolean persistConfigAndMapInfoDtls(ConfigInfo format);

	ConfigInfo setRequidFieldData(ConfigInfo format);

	List<ConfigInfo> getFormatList(String userIns);

	String getFile(int configId, String formatTableStructureDirectory, Map<String, List<String>> colAndDatatype,
			String fileName,String username);

	boolean getFormatUse(int configId);

	boolean updateFormats(ConfigInfo format);

	boolean checkUpdateFormat(String formatname, int configId);

	boolean checkUpdateTable(String tablename, int configId);

	boolean deleteFormat(Integer formatId);

	List<MapInfo> getFormatMappingData(int formatId, int companyId);
	
}
