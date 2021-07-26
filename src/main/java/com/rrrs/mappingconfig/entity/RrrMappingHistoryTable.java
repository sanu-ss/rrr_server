package com.rrrs.mappingconfig.entity;

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
@Table(name = "RRR_PROCESS_HISTORY_TABLE")
public class RrrMappingHistoryTable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_HISTORY_TABLE_SEQ")
	@SequenceGenerator(name="RRR_HISTORY_TABLE_SEQ", sequenceName = "RRR_HISTORY_TABLE_SEQ" ,allocationSize = 1)
	@Column(name = "seq_no",nullable = false)
	private int  seqNo;
	@Column(name = "TRIGGER_ID")
	private int triggerId;
	@Column(name = "PROCESS_ID",nullable = false)
	private int processId;
	@Column(name = "UPLOAD_ID",nullable = false)
	private int uploadId;
	@Column(name = "status",length = 20)
	private String status;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
