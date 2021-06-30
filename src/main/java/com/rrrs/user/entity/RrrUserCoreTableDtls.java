package com.rrrs.user.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "RRR_USER_CORE_TABLE_DTLS")
public class RrrUserCoreTableDtls implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_USER_CORE_TABLE_SEQ")
	@SequenceGenerator(name="RRR_USER_CORE_TABLE_SEQ", sequenceName = "RRR_USER_CORE_TABLE_SEQ" ,allocationSize = 1)
	@Column(name = "SEQ_NO")
	private int seqNo;
	@Column(name = "TABLE_ID",nullable = false)
	private int id;
	@Column(name = "TABLE_NAME",nullable = false)
	private String tableName;
	@Column(name = "TABLE_DESC",length = 50)
	private String tableDescription;
	@Column(name = "TOT_COLUMNS",nullable = false)
	private int totalColumn;
	@Transient
	private boolean isTableSelected;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id"))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins"))
	private RrrCommonDtls rrrCommonDtls;
	
		
}
