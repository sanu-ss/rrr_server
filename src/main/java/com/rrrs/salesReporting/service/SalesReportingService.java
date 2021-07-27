package com.rrrs.salesReporting.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.salesReporting.entities.CoreAndSourceTableDtls;
import com.rrrs.salesReporting.entities.RRRSalesReportingDtls;
@Service
public interface SalesReportingService {

	List<CoreAndSourceTableDtls> getCoreAndSourceTabDtls(Principal principal);

	Boolean checkReportingName(String reportName, Principal principal);

	Boolean checkRelation(RRRSalesReportingDtls salesReportingDtls, Principal principal);

	Integer createrule(RRRSalesReportingDtls salesReportingDtls, Principal principal);

}
