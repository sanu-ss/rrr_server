package com.rrrs.fileupload.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.tableconfig.entities.ConfigInfo;

@Repository
public interface ExcelUploadDao {

	Object uploadExcelData(ConfigInfo configInfo, UploadFileDtls uploadFileDtls,
			List<Map<Integer, String>> sheetData,String username);

	//Map<String, Object> executeProcedure(int uploadedId,ConfigInfo configInfo);

}
