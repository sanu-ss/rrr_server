package com.rrrs.mappingconfig.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rrrs.mappingconfig.entity.MappingDetails;
@Service
public interface MappingConfigService {

	Map<String, Object> getCoreAndDestTable(Principal principal);

	boolean checkMappingName(String mappingName,Principal principal);

	boolean checkMappingNameWithId(String trim, int mappingId, Principal principal);

	boolean checkSourcerelations(MappingDetails mappingDetails,Principal principal);

	String getmodeOfProcess(int mappingId,Principal principal);

	Integer saveAndUpdateMapping(MappingDetails mappingDetails, Principal principal);

	boolean checkColunmMapping(MappingDetails mdetails,Principal pricipal);
	

}
