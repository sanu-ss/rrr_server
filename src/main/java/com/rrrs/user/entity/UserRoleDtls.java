package com.rrrs.user.entity;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "RRR_USER_ROLE_DTLS")
public class UserRoleDtls implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_USER_ROLE_SEQ")
	@SequenceGenerator(name="RRR_USER_ROLE_SEQ", sequenceName = "RRR_USER_ROLE_SEQ" ,allocationSize = 1)
	@Column(name = "role_id")
	private int roleId;
	@Column(name = "role_name")
	private String roleName;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="user_role_id",referencedColumnName = "role_id")
	private List<RrrHubList> rrrHubList;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="user_role_id",referencedColumnName = "role_id")
	private List<RrrUserCoreTableDtls> rrrCoreTable;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id"))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins"))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins"))
	private RrrCommonDtls rrrCommonDtls;
		
}
