package com.rrrs.mappingconfig.service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rrrs.mappingconfig.dao.MappingConfigDao;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.mappingconfig.entity.RrrSourceTableDtls;
@Service
public class MappingConfigServiceImp implements MappingConfigService{

	private MappingConfigDao mappingConfigDao;
	public MappingConfigServiceImp(MappingConfigDao mappingConfigDao) {
		this.mappingConfigDao=mappingConfigDao;
	}
	@Override
	public Map<String, Object> getCoreAndDestTable(Principal principal) {
		System.out.println(principal.getName());
		return this.mappingConfigDao.getCoreAndDestTable(principal);
	}
	@Override
	public boolean checkMappingName(String mappingName,Principal principal) {
		return mappingConfigDao.checkMappingName(mappingName, principal);
	}
	@Override
	public boolean checkMappingNameWithId(String mappingName, int mappingId, Principal principal) {
		return mappingConfigDao.checkMappingNameWithId( mappingName,  mappingId,  principal);
	}
	@Override
	public boolean checkSourcerelations(MappingDetails mappingDetails,Principal principal) {
		List<Integer> tableIdwithoutsort = mappingDetails.getSourceTable().stream().map(RrrSourceTableDtls::getConfigId).collect(Collectors.toList());
		List<Integer> tableIdwithsort = mappingDetails.getSourceTable().stream().map(RrrSourceTableDtls::getConfigId).collect(Collectors.toList());		
		Collections.sort(tableIdwithsort);

		StringBuilder source = new StringBuilder();
		for(int i=0;i<tableIdwithsort.size();i++){			
			String tabName = mappingDetails.getSourceTable().stream().map(RrrSourceTableDtls::getTablename).collect(Collectors.toList()).get(tableIdwithoutsort.indexOf(tableIdwithsort.get(i)));
			source.append(tabName).append(",");
		}
		String relation=mappingDetails.getRelation();
		return this.mappingConfigDao.validateRelationDetails(source.substring(0, source.length()-1).trim(),relation.trim(),principal);
	}
	@Override
	public String getmodeOfProcess(int mappingId,Principal principal) {
		return this.mappingConfigDao.getmodeOfProcess(mappingId,principal);
	}
	@Override
	public Integer saveAndUpdateMapping(MappingDetails mappingDetails, Principal principal) {
		Integer mappingId=0;
		if(mappingDetails.getMappingId()>0) {
			mappingId=this.mappingConfigDao.updateMappingData(mappingDetails);
		}else {
			mappingId=this.mappingConfigDao.insertMappingData(mappingDetails);
		}
		return mappingId;
	}
	@Override
	public boolean checkColunmMapping(MappingDetails mdetails,Principal pricipal) {
		return this.mappingConfigDao.checkColumnMapping(mdetails,pricipal);
	}

}
