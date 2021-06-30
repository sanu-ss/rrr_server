package com.rrrs.tableconfig.entities;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rrrs.tableconfig.annonations.ValidColumn;
import com.rrrs.tableconfig.annonations.ValidDatatype;
import com.rrrs.tableconfig.annonations.ValidHeader;
import com.rrrs.user.entity.RrrCommonDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "RRR_CONFIG_MAPPING_DTLS")
public class MapInfo implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_CONFIG_MAPPING_SEQ")
	@SequenceGenerator(name="RRR_CONFIG_MAPPING_SEQ", sequenceName = "RRR_CONFIG_MAPPING_SEQ" ,allocationSize = 1)
	@Column(name = "SI_NO")
	private Integer siNo;
	@Column(name = "MAPP_ID",nullable = false)
	private Integer seqId;
	@ValidHeader
	@Column(name = "FILE_HEADER_NAME",nullable = false,length = 100)
	private String col;
	@ValidColumn
	@Column(name = "TABLE_COL_NAME",nullable = false,length = 30)
	private String tabcol;
	@ValidDatatype
	@Column(name = "DATA_TYPE",length = 30)
	private String datatype;
	@Column(name = "DATA_SIZE")
	private int datasize;
	@Column(name = "IS_NULL",length = 5)
	private String defaultvalue;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
