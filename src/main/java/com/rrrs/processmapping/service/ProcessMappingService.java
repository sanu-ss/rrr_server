package com.rrrs.processmapping.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.mappingconfig.entity.MappingDetails;

@Service
public interface ProcessMappingService {

	List<MappingDetails> getMAppingList(Principal principal);

	String getCoreTableName(Integer tableId);

}
