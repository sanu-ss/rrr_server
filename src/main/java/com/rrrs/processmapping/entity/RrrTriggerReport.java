package com.rrrs.processmapping.entity;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.rrrs.user.entity.RrrCommonDtls;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RRR_TRIGGER_REPORT")
public class RrrTriggerReport {

	@Id
	@Column(name = "T_ID")
	private int triggerId;
	@Column(name = "M_ID",nullable = false)
	private int mappingId;
	@Column(name="UPLD_START_DATE",nullable = false)
	private LocalDate uploadStartDate;
	@Column(name="UPLD_END_DATE",nullable = false)
	private LocalDate uploadEndDate;
	@Column(name="STATUS",nullable = false,length = 20)
	private String status;
	@Column(name = "ERROR_CODE")
	private int errorCode;
	@Column(name = "ERROR_MSG",length = 4000)
	private String errorMsg;
	@Column(name = "COLUMN_NAME",length = 30)
	private String columnName;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
