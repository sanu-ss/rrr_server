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
@Table(name = "RRR_MAPPING_SOURCE_TABLE_DTLS")
public class RrrSourceTableDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_MAPPING_SOURCE_TABLE_SEQ")
	@SequenceGenerator(name="RRR_MAPPING_SOURCE_TABLE_SEQ", sequenceName = "RRR_MAPPING_SOURCE_TABLE_SEQ" ,allocationSize = 1)
	@Column(name = "SI_NO",nullable = false)
	private int seqNo;
	@Column(name = "config_id",nullable = false)
	private int configId;
	@Column(name = "table_name")
	private String tablename;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
