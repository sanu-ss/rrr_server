package com.rrrs.processmapping.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RRR_TEMP_UPLOAD_IDS")
public class RrrTempUploadIds {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_TEMP_UPLOAD_IDS_SEQ")
	@SequenceGenerator(name="RRR_TEMP_UPLOAD_IDS_SEQ", sequenceName = "RRR_COLMAP_DTLS_SEQ" ,allocationSize = 1)
	@Column(name = "SEQ_NO",nullable = false)
	private int seqNo;
	@Column(name = "UPLOAD_ID")
	private int uploadId;
	@Column(name = "USER_NAME",length = 30)
	private String userIns;
	
}
