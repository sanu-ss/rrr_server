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
import javax.persistence.Transient;

import com.rrrs.user.entity.RrrCommonDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RRR_CONFIG_FILE_UPLD_DTLS")
public class UploadFileDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_CONFIG_FILE_UPLD_SEQ")
	@SequenceGenerator(name="RRR_CONFIG_FILE_UPLD_SEQ", sequenceName = "RRR_CONFIG_FILE_UPLD_SEQ" ,allocationSize = 1)
	@Column(name = "upload_id")
	private int uploadId;
	@Column(name="config_id",nullable = false)
	private int configId;
	@Column(name="file_name",nullable = false,length = 100)
	private String fileName;
	@Column(name = "file_path",nullable = false,length = 200)
	private String filePath;
	@Column(name = "file_size",nullable = false)
	private Long fileSize;
	@Column(name = "status",nullable = false,length = 15)
	private String status;
	@Column(name = "donot_process",nullable = false,length = 15)
	private String donotProcess;
	@Column(name = "archive_status",nullable = false,length = 15)
	private String archiveStatus;
	@Column(name = "error_log",length = 200)
	private String errorLog;
	@Column(name = "record_cont",nullable = false)
	private int recordCount;
	@Column(name = "type_of_upload",nullable = false,length = 15)
	private String typeOfUpload;
	@Column(name = "original_file_name",nullable = false,length = 100)
	private String originalFileName;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
	@Transient
	private boolean isprocessable;
	
}
