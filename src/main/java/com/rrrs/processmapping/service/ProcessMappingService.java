package com.rrrs.processmapping.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.processmapping.entity.DownloadAgain;
import com.rrrs.processmapping.entity.ProcessMappingDtls;
import com.rrrs.processmapping.entity.TriggerDetails;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.util.DownloadFileDtls;

@Service
public interface ProcessMappingService {

	List<MappingDetails> getMAppingList(Principal principal);

	String getCoreTableName(Integer tableId);

	List<ConfigInfo> getConfigFormatDtls(MappingDetails mappingDetails, Principal principal);

	List<UploadFileDtls> getexecutionfiles(MappingDetails mappingDetails, Principal principal, LocalDate startDate, LocalDate endDate);

	TriggerDetails executemapping(ProcessMappingDtls processMappingDtls) throws CustomException;

	String getResultFile(Integer triggerId, Integer destTabId,Principal principal);

	String getDestTableName(Integer destTabId);

	List<DownloadAgain> getDownloadList(Integer mappingId);

}
