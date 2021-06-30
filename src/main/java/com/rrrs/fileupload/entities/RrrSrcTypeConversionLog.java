package com.rrrs.fileupload.entities;

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
@Table(name = "RRR_SRC_TYPE_CONVERSION_LOG")
public class RrrSrcTypeConversionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_SRC_CONVER_LOG_SEQ")
	@SequenceGenerator(name="RRR_SRC_CONVER_LOG_SEQ", sequenceName = "RRR_SRC_CONVER_LOG_SEQ" ,allocationSize = 1)
	@Column(name = "log_id")
	private int logId;
	@Column(name = "FILE_UPLD_ID")
	private int fileUploadId;
	@Column(name="status",length =50 )
	private String status;
	@Column(name = "ERROR_NO")
	private int errorNo;
	@Column(name = "ERROR_MSG",length = 50)
	private String errorMsg;
	@Column(name = "LINE_NO",length = 50)
	private String lineNo;
	@Column(name = "COLUMN_NAME",length = 50)
	private String columnName;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_id",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
	
}
