package com.rrrs.tableconfig.entities;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.user.entity.RrrUserCoreTableDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RRR_CONFIG_FORMAT_DTLS")
public class ConfigInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_CONFIG_FORMAT_SEQ")
	@SequenceGenerator(name="RRR_CONFIG_FORMAT_SEQ", sequenceName = "RRR_CONFIG_FORMAT_SEQ" ,allocationSize = 1)
	@Column(name = "CONFIG_ID")
	private int configId;
	@Column(name = "TAB_NAME",nullable = false,length = 30)
	private String tablename;
	@Column(name = "ORIGINAL_TABLE_NAME",nullable = false,length = 40)
	private String orgTabName;
	@Column(name = "SHADOW_TABLE_NAME",length = 45)
	private String shadowTabName;
	@Column(name = "FORMAT_NAME",nullable = false,length = 50)
	private String formatname;
	@Column(name = "FILE_DESC",length = 100)
	private String desc;
	@Column(name = "FILE_EXT",nullable = false,length = 10)
	private String fileExt;
	@Column(name = "TOT_COL",nullable = false)
	private int colCount;
	@Transient
	private boolean flag;
	@Transient
	private boolean isTableSelected;
	@Column(name = "DELIMITER",length = 5)
	private String delimiter;
	@Column(name = "HEADER_TAG",length = 100)
	private String headerTag;
	@Column(name = "NESTED_TAG",length = 100)
	private String nestedTag;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",nullable = false,length = 30))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="CONFIG_ID",referencedColumnName = "CONFIG_ID")
	private List<MapInfo> mappings;
	
	
}
