package com.rrrs.mappingconfig.dao;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rrrs.mappingconfig.entity.MappingDetails;
@Repository
public interface MappingConfigDao {

	Map<String, Object> getCoreAndDestTable(Principal principal);

	boolean checkMappingName(String mappingName,Principal principal);

	boolean checkMappingNameWithId(String mappingName, int mappingId, Principal principal);

	boolean validateRelationDetails(String sourceTables, String relation,Principal principal);

	String getmodeOfProcess(int mappingId,Principal principal);

	Integer saveAndUpdateMapping(MappingDetails mappingDetails, Principal principal);

	boolean checkColumnMapping(MappingDetails mdetails,Principal pricipal);

	Integer updateMappingData(MappingDetails mappingDetails);

	Integer insertMappingData(MappingDetails mappingDetails);

}
