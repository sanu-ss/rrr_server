package com.rrrs.processmapping.dao;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.processmapping.entity.DownloadAgain;
import com.rrrs.processmapping.entity.ProcessMappingDtls;
import com.rrrs.processmapping.entity.TriggerDetails;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.util.DownloadFileDtls;

@Repository
public interface ProcessMappingDao {

	List<MappingDetails> getMappingList(Principal principal);

	String getCoreTableName(Integer tableId);

	List<ConfigInfo> getConfigFormatDtls(MappingDetails mappingDetails, Principal principal);

	List<UploadFileDtls> getexecutionfiles(MappingDetails mappingDetails, Principal principal, LocalDate startDate, LocalDate endDate);

	TriggerDetails executemapping(ProcessMappingDtls processMappingDtls) throws CustomException;

	Map<String,List<String>> getResultFile(Integer destTabId);

	String getDestTableName(Integer destTabId);

	String getDestFile(Integer triggerId, String processDirectory, Map<String, List<String>> destColDtls,
			String coreTableName,RrrCommonDtls rrrCommonDtls);

	List<DownloadAgain> getDownloadList(Integer mappingId);

	
}
