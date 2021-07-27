package com.rrrs.salesReporting.entities;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.rrrs.user.entity.RrrCommonDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RRR_SALES_REPORTING_DTLS")
public class RRRSalesReportingDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_SALES_REPORTING_SEQ")
	@SequenceGenerator(name="RRR_SALES_REPORTING_SEQ", sequenceName = "RRR_SALES_REPORTING_SEQ" ,allocationSize = 1)
	@Column(name = "REPORT_ID",nullable = false)
	private int reportId;
	@Column(name = "REPORT_NAME",nullable = false,length = 50,unique = true)
	private String reportName;
	@Column(name = "PRIMARY_CONDITION",nullable = false,length = 500)
	private String primaryCondition;
	@Column(name = "SELECTED_TABLES",nullable = false,length = 500)
	private String selectedTables;
	@Column(name = "SELECTED_COLUMN",nullable = false,length = 2500)
	private String selectedColumn;
	@Column(name = "STATUS",nullable = false,length = 10)
	private String status;
	@Column(name = "MODE_OF_PROCESS",nullable = false,length = 10)
	private String modeOfProcess;
	@Column(name = "ORG_TABLE",nullable = false,length = 600)
	private String orgSelectedTab;
	@Column(name = "ORG_COLUMN",nullable = false,length = 2700)
	private String orgSelectedCol;
	@Column(name = "ORG_PRIMARY_CONDITION",nullable = false,length = 1100)
	private String orgPrimaryCondition;
	@Column(name = "START_DATE",nullable = false)
	private LocalDate startDate;
	@Column(name = "END_DATE",nullable = false)
	private LocalDate endDate;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
