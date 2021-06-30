package com.rrrs.user.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="RRR_REGISTERED_USER")
public class CurrentUser implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_REGISTERED_USER_SEQ")
	@SequenceGenerator(name="RRR_REGISTERED_USER_SEQ", sequenceName = "RRR_REGISTERED_USER_SEQ" ,allocationSize = 1)
	@Column(name = "USER_ID")
	private int userId;
	@Column(name="USER_FIRSTNAME",length = 45,nullable = false)
	private String firstName;
	@Column(name = "USER_LASTNAME",length = 45,nullable = true)
	private String lastName;
	@Column(name = "USER_USERNAME",unique = true,length = 45)
	private String username;
	@Column(name = "USER_EMAIL",length = 45,nullable = false)
	private String email;
	@Column(name = "country_CODE",length=20,nullable = false)
	private String selectCountryCode;
	@Column(name = "USER_CONTACT_NO",length = 15,nullable = false)
	private String contactNumber;
	@Column(name="USER_PASSWORD",length = 500,nullable = false)
	private String password;
	@Column(name = "USER_ROLE_ID",nullable = false)
	private int selectUserRole;
	@Column(name = "START_DATE")
	private LocalDate startDate;
	@Column(name = "END_DATE")
	private LocalDate endDate;
	@Embedded
	@AttributeOverride(name = "companyId",column = @Column(name="company_id",nullable = false))
	@AttributeOverride(name = "userIns",column = @Column(name="user_ins",length = 30,nullable = false))
	@AttributeOverride(name = "dateIns",column = @Column(name="date_ins",nullable = false))
	private RrrCommonDtls rrrCommonDtls;
		
}
