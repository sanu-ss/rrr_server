package com.rrrs.salesReporting.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.salesReporting.dao.SalesReportingDao;
import com.rrrs.salesReporting.entities.CoreAndSourceTableDtls;
import com.rrrs.salesReporting.entities.RRRSalesReportingDtls;

@Service
public class SalesReportingServiceImp implements SalesReportingService{

	private SalesReportingDao  salesReportingDao;
	public SalesReportingServiceImp(SalesReportingDao  salesReportingDao) {
		this.salesReportingDao=salesReportingDao;
	}
	
	@Override
	public List<CoreAndSourceTableDtls> getCoreAndSourceTabDtls(Principal principal) {
		return this.salesReportingDao.getCoreAndSourceTabDtls(principal);
	}

	@Override
	public Boolean checkReportingName(String reportName, Principal principal) {
		return this.salesReportingDao.checkReportingName(reportName,principal);
	}

	@Override
	public Boolean checkRelation(RRRSalesReportingDtls salesReportingDtls, Principal principal) {
		System.out.println("RRRSalesReportingDtls-->"+salesReportingDtls);
		String relation=(salesReportingDtls.getPrimaryCondition().trim()).toUpperCase();
		String tabName=(salesReportingDtls.getSelectedTables().trim()).toUpperCase();
		tabName=tabName.substring(0,(tabName.length()-1));
		String OrgTabName=(salesReportingDtls.getOrgSelectedTab().trim()).toUpperCase();
		OrgTabName=OrgTabName.substring(0,(OrgTabName.length()-1));
		String[] tables = tabName.split(",");
		String[] orgTables = OrgTabName.split(",");
		for(int index=0;index<tables.length;index++){
			relation=relation.replace(tables[index], orgTables[index]);
		}
		System.out.println("this.salesReportingDao.checkRelation(relation,OrgTabName)==>"+this.salesReportingDao.checkRelation(relation,OrgTabName));
		return this.salesReportingDao.checkRelation(relation,OrgTabName);
	}

}
