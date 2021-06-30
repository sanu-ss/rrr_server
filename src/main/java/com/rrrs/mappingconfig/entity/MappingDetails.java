package com.rrrs.mappingconfig.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "RRR_MAPPING_DTLS")
public class MappingDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_MAPPING_DTLS_SEQ")
	@SequenceGenerator(name="RRR_MAPPING_DTLS_SEQ", sequenceName = "RRR_MAPPING_DTLS_SEQ" ,allocationSize = 1)
	@Column(name = "MAPPING_ID",nullable = false)
	private int  mappingId;
	@Column(name = "mapping_name")
	private String mappingName;
	@Column(name = "DEST_TABLE",nullable = false)
	private int destTableId;
	@Column(name = "start_date")
	private LocalDate startDate;
	@Column(name = "end_date")
	private LocalDate endDate;
	@Column(name = "primary_condition")
	private String relation;
	@Column(name = "status",length =20 )
	private String status;
	@Column(name = "mode_of_process")
	private String mode;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="MAPPING_ID",referencedColumnName = "MAPPING_ID")
	private List<ColumnMappingDtls> columnMappingDtls=new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="MAPPING_ID",referencedColumnName = "MAPPING_ID")
	private List<RrrSourceTableDtls> sourceTable =new ArrayList<>();
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
}
