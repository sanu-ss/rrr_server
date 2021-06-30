package com.rrrs.mappingconfig.dao;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.mappingconfig.entity.ColumnMappingDtls;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.mappingconfig.entity.RRRCoreTableColDtls;
import com.rrrs.mappingconfig.entity.RrrCoreTablesDtls;
import com.rrrs.mappingconfig.entity.RrrSourceTableDtls;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.ReadPropertyFile;
@Repository
public class MappingConfigDaoImp implements MappingConfigDao{

	private EntityManager entityManager;
	private CurrentUserDbDtls currentUserDbDtls;
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("mapping/mapping.properties");
	private Logger logger=LoggerFactory.getLogger(MappingConfigDaoImp.class);
	public MappingConfigDaoImp(EntityManager entityManager,CurrentUserDbDtls currentUserDbDtls) {
		this.currentUserDbDtls=currentUserDbDtls;
		this.entityManager=entityManager;
	}
	@Override
	public Map<String, Object> getCoreAndDestTable(Principal principal) {
		logger.debug("inside mapping package in getCoreAndDestTable method");
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		Map<String, Object> map=new HashMap<>();
		Session session = this.entityManager.unwrap(Session.class);
		
		@SuppressWarnings("rawtypes")
		NativeQuery query = session.createNativeQuery(this.readPropertyFile.getProperty("GET_TARGET_TABANDCOLS_DETAILS"));
		query.setParameter("companyId", companyId);
		query.setParameter("roleName", principal.getName());
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		Map<Integer, RrrCoreTablesDtls> tabdtls = new HashMap<>();
		for(Object[] obj:resultList) {
			RRRCoreTableColDtls rrrCoreTableColDtls=new RRRCoreTableColDtls();
			BigDecimal data=(BigDecimal)obj[0];
			int tableId=data.intValue();
			RrrCoreTablesDtls rrrCoreTablesDtls=tabdtls.get(tableId);
			if(rrrCoreTablesDtls==null) {
				System.out.println("created--1");
				rrrCoreTablesDtls=new RrrCoreTablesDtls();
			}
			rrrCoreTablesDtls.setTableId(tableId);
			rrrCoreTablesDtls.setTableName((String)obj[1]);
			BigDecimal colCount=(BigDecimal)obj[8];
			rrrCoreTablesDtls.setTotalColumn(colCount.intValue());
			BigDecimal colId=(BigDecimal)obj[3];
			rrrCoreTableColDtls.setColumnId(colId.intValue());
			rrrCoreTableColDtls.setColumnName((String)obj[4]);
			rrrCoreTableColDtls.setDataType((String)obj[5]);
			BigDecimal size=(BigDecimal)obj[6];
			rrrCoreTableColDtls.setDataSize(size.intValue());
			rrrCoreTableColDtls.setNullable((String)obj[7]);
			rrrCoreTablesDtls.getRrrCoreTableColDtls().add(rrrCoreTableColDtls);
			tabdtls.put(tableId, rrrCoreTablesDtls);
		}
		System.out.println("core-->"+tabdtls.values());
		map.put("coreTable",tabdtls.values());
		@SuppressWarnings("unchecked")
		Query<ConfigInfo> sourceTable = session.createQuery(this.readPropertyFile.getProperty("GET_SOURCE_TABANDCOLS_DETAILS"));
		sourceTable.setParameter("companyId", companyId);
		List<ConfigInfo> sourceTableList = sourceTable.getResultList();
		map.put("sourceTable", sourceTableList);
		
		return map;
	}
	@Override
	public boolean checkMappingName(String mappingName,Principal principal) {
		System.out.println("mapping name"+mappingName+" principal--"+principal.getName());
		Session session = this.entityManager.unwrap(Session.class);
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		System.out.println("company id-->"+companyId);
		@SuppressWarnings("unchecked")
		Query<Long> query = session.createQuery(this.readPropertyFile.getProperty("COUNT_RRR_PROCESS_DTLS"));
		query.setParameter("mappingName", mappingName);
		query.setParameter("companyId", companyId);
		System.out.println("count-->"+(Long)query.uniqueResult());
		return 0<(Long)query.uniqueResult();
	}
	@Override
	public boolean checkMappingNameWithId(String mappingName, int mappingId, Principal principal) {
		Session session = this.entityManager.unwrap(Session.class);
		String user=principal.getName();
		int companyId = this.currentUserDbDtls.getCompanyId(user);
		@SuppressWarnings("unchecked")
		Query<Long> query = session.createQuery(this.readPropertyFile.getProperty("COUNT_RRR_PROCESS_DTLS_WITH_ID"));
		query.setParameter("mappingName", mappingName);
		query.setParameter("companyId", companyId);
		query.setParameter("mappingId", mappingId);
		return 0<(Long)query.uniqueResult();
	}
	@Override
	public boolean validateRelationDetails(String sourceTables, String relation,Principal principal) {
		Session session = this.entityManager.unwrap(Session.class);
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		System.out.println("companyId-->"+companyId+" sourceTables-->"+sourceTables+" relation-->"+relation);
		List<String> status=new ArrayList<>();
		status.add(null);
		session.doWork(connection->{
			try(CallableStatement callableStatement=connection.prepareCall("{ ? = call rrr_relation_validate_fun(?,?,?) }")){
				callableStatement.registerOutParameter(1,Types.VARCHAR);
				callableStatement.setString(2,sourceTables);
				callableStatement.setInt(3,companyId);
				callableStatement.setString(4,relation);
				callableStatement.execute();
				System.out.println("relation-->"+relation+"-->sourceTables>>"+sourceTables);
				System.out.println("callableStatement.getString(1)-->"+callableStatement.getString(1));
				status.set(0, callableStatement.getString(1));
			}
		});
		System.out.println("output-->"+status.get(0));
		if(status.get(0) !=null && status.get(0).equalsIgnoreCase("Y")) {
			return true;
		}
		return false;
	}
	@Override
	public String getmodeOfProcess(int mappingId,Principal principal) {
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		Session session = this.entityManager.unwrap(Session.class);
		Query createQuery = session.createQuery(this.readPropertyFile.getProperty("FETCH_RRR_MODEOFPROCESS_WITH_ID"));
		createQuery.setParameter("mappingId", mappingId);
		createQuery.setParameter("companyId", companyId);
		return (String)createQuery.uniqueResult();
	}
	@Override
	public Integer saveAndUpdateMapping(MappingDetails mappingDetails, Principal principal) {
		
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		String source=null;
		int mappid=0;
		if(!mappingDetails.getRelation().isEmpty()) {
			source=mappingDetails.getRelation();
		}
//		if(mappingDetails.getCoreTable()!=null) {
//			mappid=mappingDetails.getCoreTable().getTableId();
//		}
		
		Session session = this.entityManager.unwrap(Session.class);
		
		
		
		return null;
	}
	@Override
	public boolean checkColumnMapping(MappingDetails mappingDetails,Principal pricipal) {
        String username=pricipal.getName();
        System.out.println("user name-->"+username);
		int companyId = this.currentUserDbDtls.getCompanyId(username);
		Session session = this.entityManager.unwrap(Session.class);
		String maprule = mappingDetails.getColumnMappingDtls().stream().map(ColumnMappingDtls::getMappRule).collect(Collectors.joining(","));
		String sourcetable = mappingDetails.getSourceTable().stream().map(RrrSourceTableDtls::getTablename).collect(Collectors.joining(","));		
		int destTabeId = mappingDetails.getDestTableId();
		String destColname = mappingDetails.getColumnMappingDtls().get(0).getDestColumn();
		String primaryCondition =null;
		logger.debug(" P_MAPP_RULE > " + maprule +" ~~~-> P_SOURCE_TAB > " + sourcetable +" ~~~-> P_PRIMARY_COND > "+ primaryCondition+"~~~~> P_DEST_TABLE > "+destTabeId+" ~~~~~> P_DEST_COL_NAME > "+ destColname+" p_company_id ");
		if(!mappingDetails.getRelation().isEmpty()){		
			if(mappingDetails.getRelation()!=null && mappingDetails.getRelation()!="")
				primaryCondition = mappingDetails.getRelation();
		}
		Query<String> destTableQuery = session.createSQLQuery("SELECT TABLE_NAME FROM RRR_CORE_TABLE_DTLS WHERE TABLE_ID=:tableId AND COMPANY_ID=:company");
		destTableQuery.setParameter("tableId", destTabeId);
		destTableQuery.setParameter("company", companyId);		
		String coreTableName = destTableQuery.uniqueResult();
		System.out.println("data 111-->"+maprule+"-->"+sourcetable+"-->"+mappingDetails.getRelation()+"-->"+coreTableName);
		System.out.println("dest column name-->"+destColname+"-->"+companyId);
		List<String> status=new ArrayList<>();
		status.add(null);
		session.doWork(connection->{
			try(CallableStatement callableStatement=connection.prepareCall("{ ? = call rrr_mapping_rulecheck_fun(?,?,?,?,?,?,?) }")){
				callableStatement.registerOutParameter(1,Types.VARCHAR);
				callableStatement.setString(2,maprule);
				callableStatement.setString(3,sourcetable);
				callableStatement.setString(4,mappingDetails.getRelation());
				callableStatement.setString(5,coreTableName);
				callableStatement.setString(6,destColname);
				callableStatement.setInt(7,companyId);
				callableStatement.setString(8,"Y");
				callableStatement.execute();
				System.out.println("callableStatement.getString(1)-->"+callableStatement.getString(1));
				status.set(0, callableStatement.getString(1));
			}
		});
		
		if (status.get(0) != null && status.get(0).equalsIgnoreCase("TRUE")) {
			return true;
		}
		return false;
	}
	@Override
	public Integer updateMappingData(MappingDetails mappingDetails) {
		Session session = this.entityManager.unwrap(Session.class);
//		String source=null;
//		int mappid=0;
//		if(!mappingDetails.getRelation().isEmpty()){
//			source=mappingDetails.getRelation();
//		}
//		mappid=mappingDetails.getDestTableId();
//		Query query = session.createQuery(this.readPropertyFile.getProperty("UPDATE_RRR_PROCESS_DTLS"));
//		query.setParameter("mappingName", mappingDetails.getMappingName());
//		query.setParameter("relation", mappingDetails.getRelation());
//		query.setParameter("destTableId", mappingDetails.getDestTableId());
//		query.setParameter("status", mappingDetails.getStatus());
//		query.setParameter("startDate", mappingDetails.getStartDate());
//		query.setParameter("endDate", mappingDetails.getEndDate());
//		query.setParameter("userIns", mappingDetails.getRrrCommonDtls().getUserIns());
//		query.setParameter("mode", mappingDetails.getMode());
//		query.setParameter("companyId", mappingDetails.getRrrCommonDtls().getCompanyId());
//		query.setParameter("mappingId", mappingDetails.getMappingId());
//		
//		Query deleteMappingData = session.createQuery(this.readPropertyFile.getProperty("DELETE_RRR_PROCESS_MAPPING_DTLS"));
//		deleteMappingData.setParameter("companyId", mappingDetails.getRrrCommonDtls().getCompanyId());
//		deleteMappingData.setParameter("mappingId", mappingDetails.getMappingId());
//		Transaction tx = session.beginTransaction();
//		try {
//			query.executeUpdate();
//			deleteMappingData.executeUpdate();
//			tx.commit();
//		}catch(Exception e) {
//			tx.rollback();
//		}
		for(ColumnMappingDtls colMapDtls:mappingDetails.getColumnMappingDtls()) {
			colMapDtls.setConversionRule(colMapDtls.getMappRule());
			colMapDtls.setMappRule(null);
		}
		
		Transaction tx = session.beginTransaction();
		try {
			session.update(mappingDetails);
			executeMappingConvertProc(session,mappingDetails);
			tx.commit();
		}catch(Exception e) {
			tx.rollback();
		}
		System.out.println("mappingDetails.getMappingId()--<>"+mappingDetails.getMappingId());
		return mappingDetails.getMappingId();
	}
	@Override
	public Integer insertMappingData(MappingDetails mappingDetails) {
		Session session = this.entityManager.unwrap(Session.class);
		for(ColumnMappingDtls colMapDtls:mappingDetails.getColumnMappingDtls()) {
			colMapDtls.setConversionRule(colMapDtls.getMappRule());
			colMapDtls.setMappRule(null);
		}
		Transaction tx = session.beginTransaction();
		try {
			session.save(mappingDetails);
			executeMappingConvertProc(session,mappingDetails);
			tx.commit();
			System.out.println("data inserted");
		}catch(Exception exc){
			System.out.println("error while inserting data in table");
			tx.rollback();
		}
		
		return mappingDetails.getMappingId();
	}
	
	public void executeMappingConvertProc(Session session,MappingDetails mappingDetails) {
		System.out.println("mapping data-->"+mappingDetails);
		StoredProcedureQuery procedure = session.createStoredProcedureQuery(this.readPropertyFile.getProperty("RRR_MAPPING_CONVERT_RULE"));
		procedure.registerStoredProcedureParameter("P_PROCESS_ID", Integer.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("P_COMPANY_ID", Integer.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_status", Integer.class,ParameterMode.OUT);
		
		procedure.setParameter("P_PROCESS_ID",mappingDetails.getMappingId());
		procedure.setParameter("P_COMPANY_ID",mappingDetails.getRrrCommonDtls().getCompanyId());
		procedure.execute();
		String status =(String)procedure.getOutputParameterValue("p_status");
		System.out.println("status-->"+status);
		
	}

}
