package com.rrrs.salesReporting.dao;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.rrrs.mappingconfig.dao.MappingConfigDaoImp;
import com.rrrs.salesReporting.entities.CoreAndSourceTabColdtls;
import com.rrrs.salesReporting.entities.CoreAndSourceTableDtls;
import com.rrrs.salesReporting.entities.RRRSalesReportingDtls;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.ReadPropertyFile;
@Repository
public class SalesReportingDaoImp implements SalesReportingDao{

	private EntityManager entityManager;
	private CurrentUserDbDtls currentUserDbDtls;
	private Logger logger=LoggerFactory.getLogger(MappingConfigDaoImp.class);
	public SalesReportingDaoImp(EntityManager entityManager,CurrentUserDbDtls currentUserDbDtls) {
		this.currentUserDbDtls=currentUserDbDtls;
		this.entityManager=entityManager;
	}
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("salesreporting/salesreporting.properties");
	@Override
	public List<CoreAndSourceTableDtls> getCoreAndSourceTabDtls(Principal principal) {
		Session session = this.entityManager.unwrap(Session.class);
		String username=principal.getName();
		System.out.println("username-->"+username);
		int companyId = this.currentUserDbDtls.getCompanyId(username);
		System.out.println("companyId-->"+companyId);
		List<CoreAndSourceTableDtls> coreAndSourceTableDtls=new ArrayList<>();
		NativeQuery query = session.createSQLQuery(this.readPropertyFile.getProperty("GET_CORE_TABANDCOLS_DETAILS"));
		query.setParameter("companyId", companyId);
		query.setParameter("username", username);
		List list = query.list();
		Map<Integer, CoreAndSourceTableDtls> tabdtls = new HashMap<>();
		
		for(Object data:list) {
			Object [] coreTableDtls=(Object [])data;
			BigDecimal id=(BigDecimal)coreTableDtls[0];
			int configId = id.intValue();
			CoreAndSourceTableDtls tableDetail = tabdtls.get(configId);
			if (tableDetail == null) {
				tableDetail = new CoreAndSourceTableDtls();
			}
			tableDetail.setTabId(configId);
			tableDetail.setTabName((String)coreTableDtls[1]);
			tableDetail.setOrgTabName((String)coreTableDtls[1]);
			BigDecimal totalCol=(BigDecimal)coreTableDtls[6];
			tableDetail.setTotalColumn(totalCol.intValue());
			tabdtls.put(configId,tableDetail);
			CoreAndSourceTabColdtls coreAndSourceTabColdtls=new CoreAndSourceTabColdtls();
			BigDecimal colId=(BigDecimal)coreTableDtls[2];
			coreAndSourceTabColdtls.setColId(colId.intValue());
			coreAndSourceTabColdtls.setColName((String)coreTableDtls[3]);
			BigDecimal size=(BigDecimal)coreTableDtls[5];
			coreAndSourceTabColdtls.setDatasize(size.intValue());
			coreAndSourceTabColdtls.setDatatype((String)coreTableDtls[4]);
			tableDetail.getTableColDtls().add(coreAndSourceTabColdtls);
		}
		List<CoreAndSourceTableDtls> coredtls = new ArrayList<>(tabdtls.values());
		
		NativeQuery query2 = session.createSQLQuery(this.readPropertyFile.getProperty("GET_SOURCE_TABANDCOLS_DETAILS"));
		query2.setParameter("companyId", companyId);
		List list2 = query2.list();
		
		Map<Integer, CoreAndSourceTableDtls> tabdtls2 = new HashMap<>();
		
		for(Object data:list2) {
			Object [] coreTableDtls=(Object [])data;
			BigDecimal id=(BigDecimal)coreTableDtls[0];
			int configId = id.intValue();
			CoreAndSourceTableDtls tableDetail = tabdtls2.get(configId);
			if (tableDetail == null) {
				tableDetail = new CoreAndSourceTableDtls();
			}
			tableDetail.setTabId(configId);
			tableDetail.setTabName((String)coreTableDtls[2]);
			tableDetail.setOrgTabName((String)coreTableDtls[3]);
			BigDecimal totalCol=(BigDecimal)coreTableDtls[7];
			tableDetail.setTotalColumn(totalCol.intValue());
			tabdtls2.put(configId,tableDetail);
			CoreAndSourceTabColdtls coreAndSourceTabColdtls=new CoreAndSourceTabColdtls();
			BigDecimal colId=(BigDecimal)coreTableDtls[1];
			coreAndSourceTabColdtls.setColId(colId.intValue());
			coreAndSourceTabColdtls.setColName((String)coreTableDtls[4]);
			BigDecimal size=(BigDecimal)coreTableDtls[6];
			coreAndSourceTabColdtls.setDatasize(size.intValue());
			coreAndSourceTabColdtls.setDatatype((String)coreTableDtls[5]);
			tableDetail.getTableColDtls().add(coreAndSourceTabColdtls);
		}
		
		List<CoreAndSourceTableDtls> sourceDtls = new ArrayList<>(tabdtls2.values());
		coreAndSourceTableDtls.addAll(coredtls);
		coreAndSourceTableDtls.addAll(sourceDtls);
		System.out.println("coreAndSourceTableDtls-->"+coreAndSourceTableDtls);
		return coreAndSourceTableDtls;
	}
	@Override
	public Boolean checkReportingName(String reportName, Principal principal) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CHECK_REPORTING_NAME"));
		query.setParameter("reportName",reportName.toUpperCase());
		return ((Long)query.uniqueResult()>0)?false:true;
	}
	@Override
	public Boolean checkRelation(String relation, String orgTabName) {
		Session session = this.entityManager.unwrap(Session.class);
		System.out.println("relation-->"+relation);
		System.out.println("orgTabName-->"+orgTabName);
		List<String> status=new ArrayList<>();
		status.add("");
		session.doWork(connection->{
			try(CallableStatement callableStatement=connection.prepareCall("{ ? = call RRR_REPORTING_REL_VAL_FUN(?,?) }")){
				callableStatement.registerOutParameter(1,Types.VARCHAR);
				callableStatement.setString(2,orgTabName);
				callableStatement.setString(3,relation);
				callableStatement.execute();
				status.set(0, callableStatement.getString(1));
			}
		});
		if(status.get(0)!=null && status.get(0).equalsIgnoreCase("Y"))
			return true;
		return false;
	}
	@Override
	public Integer createrule(RRRSalesReportingDtls salesReportingDtls, Principal principal) {
		Session session = this.entityManager.unwrap(Session.class);
		String tables=salesReportingDtls.getSelectedTables();
		tables=tables.substring(0,tables.length()-1);
		salesReportingDtls.setSelectedTables(tables);
		String selectedColumn=salesReportingDtls.getSelectedColumn();
		selectedColumn=selectedColumn.substring(0,selectedColumn.length()-1);
		salesReportingDtls.setSelectedColumn(selectedColumn);
		String orgSelectedTab=salesReportingDtls.getOrgSelectedTab();
		orgSelectedTab=orgSelectedTab.substring(0,orgSelectedTab.length()-1);
		salesReportingDtls.setOrgSelectedTab(orgSelectedTab);
		String orgSelectedCol=salesReportingDtls.getOrgSelectedCol();
		orgSelectedCol=orgSelectedCol.substring(0,orgSelectedCol.length()-1);
		salesReportingDtls.setOrgSelectedCol(orgSelectedCol);
		String relation=salesReportingDtls.getPrimaryCondition();
		String[] tab = tables.split(",");
		String[] orgTab = orgSelectedTab.split(",");
		for(int index=0;index<tab.length;index++){
			relation=relation.replace(tab[index], orgTab[index]);
		}
		salesReportingDtls.setOrgPrimaryCondition(relation);
		Transaction tx = session.beginTransaction();
		try {
			session.persist(salesReportingDtls);
			tx.commit();
		} catch (Exception e) {
			salesReportingDtls.setReportId(-1);
			System.out.println("Error while saving data in DB");
			tx.rollback();
		}
		
		
		return salesReportingDtls.getReportId();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
