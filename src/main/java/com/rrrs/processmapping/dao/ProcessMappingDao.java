package com.rrrs.processmapping.dao;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.rrrs.mappingconfig.entity.MappingDetails;

@Repository
public interface ProcessMappingDao {

	List<MappingDetails> getMappingList(Principal principal);

	String getCoreTableName(Integer tableId);

	
}
