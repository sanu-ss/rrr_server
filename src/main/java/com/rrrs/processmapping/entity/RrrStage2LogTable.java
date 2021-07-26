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
@Table(name = "rrr_stage2_log_table")
public class RrrStage2LogTable {

	@Id
	@Column(name = "LOG_ID")
	private int logId;
	@Column(name = "TRIGGER_ID")
	private int triggerId;
	@Column(name = "MAP_ID",nullable = false)
	private int mappingId;
	@Column(name="STATUS",nullable = false,length = 20)
	private String status;
	@Column(name = "ERROR_NO",length = 20)
	private String errorNo;
	@Column(name = "ERROR_MSG",length = 4000)
	private String errorMsg;
	@Column(name = "LINE_NO",length = 5)
	private String lineNo;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;

	
}
