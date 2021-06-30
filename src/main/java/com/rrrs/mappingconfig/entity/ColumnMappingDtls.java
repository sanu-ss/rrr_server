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
@Table(name = "RRR_COLUMN_MAPPING_DTLS")
public class ColumnMappingDtls {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_COLMAP_DTLS_SEQ")
	@SequenceGenerator(name="RRR_COLMAP_DTLS_SEQ", sequenceName = "RRR_COLMAP_DTLS_SEQ" ,allocationSize = 1)
	@Column(name = "COL_MAPP_ID",nullable = false)
	private int  colMappId;
	@Column(name = "DEST_COLUMN",length = 30,nullable =false)
	private String destColumn;
	@Column(name = "MAPP_RULE",length = 4000)
	private String mappRule;
	@Column(name="DEST_TYPE",length = 30)
	private String destType;
	@Column(name = "CONVERSION_RULE",length = 4000)
	private String conversionRule;
	@Column(name = "IS_MASTER_MAP",length = 1)
	private String isMasterMap;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
