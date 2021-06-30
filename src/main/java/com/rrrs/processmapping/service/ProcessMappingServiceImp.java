package com.rrrs.processmapping.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.processmapping.dao.ProcessMappingDao;

@Service
public class ProcessMappingServiceImp implements ProcessMappingService{

	private ProcessMappingDao processMappingDao;
	public ProcessMappingServiceImp(ProcessMappingDao processMappingDao) {
		this.processMappingDao=processMappingDao;
	}
	@Override
	public List<MappingDetails> getMAppingList(Principal principal) {
		return this.processMappingDao.getMappingList(principal);
	}
	@Override
	public String getCoreTableName(Integer tableId) {
		return this.processMappingDao.getCoreTableName(tableId);
	}
}
