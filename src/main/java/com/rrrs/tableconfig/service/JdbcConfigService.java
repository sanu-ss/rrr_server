package com.rrrs.tableconfig.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.exception.module.CustomException;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;

@Service
public interface JdbcConfigService {

	public boolean checkTable(String tablename);

	public boolean checkFormat(String formatname);

	public boolean persistCsv(ConfigInfo format);

	public List<ConfigInfo> getFormatList(String userIns);

	public String getFile(int configId, String formatTableStructureDirectory, Map<String, List<String>> colAndDatatype,
			String fileName,String username) throws CustomException;

	public boolean updateFormats(ConfigInfo format);

	public boolean getFormatsUse(int configId);

	public boolean checkUpdateFormat(String formatname, int configId);

	public boolean checkUpdateTable(String tablename, int configId);

	public boolean deleteFormat(Integer formatId);

	public List<MapInfo> getFormatMappingData(int formatId, int companyI);

}
