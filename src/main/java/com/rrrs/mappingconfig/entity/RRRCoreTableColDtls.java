package com.rrrs.mappingconfig.entity;

import java.time.LocalDateTime;

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
@Table(name = "RRR_CORE_TABLE_COLUMN_DTLS")
public class RRRCoreTableColDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_CORE_TABLE_COLUMN_SEQ")
	@SequenceGenerator(name="RRR_CORE_TABLE_COLUMN_SEQ", sequenceName = "RRR_CORE_TABLE_COLUMN_SEQ" ,allocationSize = 1)
	@Column(name="seq_no")
	private int seqId;
	@Column(name="column_id",nullable = false)
	private int columnId;
	@Column(name = "column_name",nullable = false,length = 50)
	private String columnName;
	@Column(name = "data_type",nullable = false,length = 50)
	private String dataType;
	@Column(name="data_size",nullable = false)
	private int dataSize;
	@Column(name="nullable",length = 2,nullable = false)
	private String nullable;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id"))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins"))
	private RrrCommonDtls rrrCommonDtls;
	
}
