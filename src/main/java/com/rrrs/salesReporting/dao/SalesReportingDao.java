package com.rrrs.salesReporting.dao;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.rrrs.salesReporting.entities.CoreAndSourceTableDtls;
import com.rrrs.salesReporting.entities.RRRSalesReportingDtls;
@Repository
public interface SalesReportingDao {

	List<CoreAndSourceTableDtls> getCoreAndSourceTabDtls(Principal principal);

	Boolean checkReportingName(String reportName, Principal principal);

	Boolean checkRelation(String relation, String orgTabName);

	Integer createrule(RRRSalesReportingDtls salesReportingDtls, Principal principal);

}
