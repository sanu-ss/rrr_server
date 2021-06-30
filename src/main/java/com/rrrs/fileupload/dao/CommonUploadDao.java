package com.rrrs.fileupload.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.tableconfig.entities.ConfigInfo;

@Repository
public interface CommonUploadDao {

	boolean checkFileUpload(String fileName, int companyId);

	boolean isConfigExist(String formatname);

	ConfigInfo getConfigDetails(String formatname);

	Map<String, Object> uploadStatus(Map<String, Object> errorMsg, ConfigInfo configInfo);

}
