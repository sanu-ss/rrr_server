package com.rrrs.fileupload.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.tableconfig.entities.ConfigInfo;

@Service
public interface ExcelUploadService {

	Object excelUpload(String string, ConfigInfo configInfo, UploadFileDtls uploadFileDtls,String username)throws CustomException;

}
