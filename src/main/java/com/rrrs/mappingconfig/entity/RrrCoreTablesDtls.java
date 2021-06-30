package com.rrrs.mappingconfig.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.user.entity.RrrModuleList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RRR_CORE_TABLE_DTLS")
public class RrrCoreTablesDtls implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_CORE_TABLE_SEQ")
	@SequenceGenerator(name="RRR_CORE_TABLE_SEQ", sequenceName = "RRR_CORE_TABLE_SEQ" ,allocationSize = 1)
	@Column(name = "TABLE_ID",nullable = false)
	private int tableId;
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
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="TABLE_ID",referencedColumnName = "table_id")
	private List<RRRCoreTableColDtls> rrrCoreTableColDtls=new ArrayList<>();
		
}
