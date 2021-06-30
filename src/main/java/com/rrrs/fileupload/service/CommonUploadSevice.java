package com.rrrs.fileupload.service;

import java.util.Map;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.rrrs.tableconfig.entities.ConfigInfo;

@Service
public interface CommonUploadSevice {

	boolean checkFileUploaded(String fileName, int companyId);

	boolean isConfigExist(String formatname);

	ConfigInfo getConfigDetails(String formatname);

	Object uploadStatus(ConfigInfo configInfo, Map<String, Object> errorMsg,Session session);

}
