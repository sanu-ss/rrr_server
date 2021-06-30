package com.rrrs.mappingconfig.entity;

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
@Table(name = "RRR_PROCESS_DTLS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RrrProcessDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_PROCESS_DTLS_SEQ")
	@SequenceGenerator(name="RRR_PROCESS_DTLS_SEQ", sequenceName = "RRR_PROCESS_DTLS_SEQ" ,allocationSize = 1)
	@Column(name = "PROCESS_ID",nullable = false)
	private int processId;
	@Column(name="PROCESS_NAME",length = 50,nullable = false,unique = true)
	private String processName;
	@Column(name = "DEST_TABLE",length = 30,nullable = false)
	private String destTableName;
	@Column(name = "PRIMARY_CONDITION",length = 4000)
	private String primaryCondition;
	@Column(name = "STATUS",length = 20)
	private String status;
	@Column(name = "START_DATE",nullable = false)
	private LocalDate startDate;
	@Column(name = "END_DATE",nullable = false)
	private LocalDate endDate;
	@Column(name = "MODE_OF_PROCESS")
	private String modeOfProcess;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id"))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins"))
	private RrrCommonDtls rrrCommonDtls;
}
