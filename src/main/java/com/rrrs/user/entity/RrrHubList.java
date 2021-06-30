package com.rrrs.user.entity;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "RRR_MODULE_HUB_LIST")
public class RrrHubList implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_MODULE_HUB_SEQ")
	@SequenceGenerator(name="RRR_MODULE_HUB_SEQ", sequenceName = "RRR_MODULE_HUB_SEQ" ,allocationSize = 1)
	@Column(name = "SEQ_NO")
	private int seqNo;
	@Column(name = "MODULE_HUB_ID",nullable = false)
	private int id;
	@Column(name = "MODULE_HUB_NAME")
	private String name;
	@Transient
	private boolean isSelectedHub;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id"))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins"))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins"))
	private RrrCommonDtls rrrCommonDtls;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="hub_seq_id",referencedColumnName = "SEQ_NO")
	private List<RrrModuleList> rrrModuleList;
		
}
