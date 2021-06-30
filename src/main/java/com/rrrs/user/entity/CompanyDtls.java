package com.rrrs.user.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RRR_COMPANY_DTLS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDtls {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RRR_COMPANY_ID_SEQ")
	@SequenceGenerator(name="RRR_COMPANY_ID_SEQ", sequenceName = "RRR_COMPANY_ID_SEQ" ,allocationSize = 1)
	@Column(name = "company_id")
	private int id;
	@Column(name = "company_name",length = 50,nullable = false,unique = true)
	private String companyName;
	@Column(name = "email",length = 60,nullable  = false,unique = true)
	private String email;
	@Column(name = "country_code",nullable = false,length = 20)
	private String countryCode;
	@Column(name = "contact",nullable = false)
	private long contact;
	@Column(name = "address",nullable = false,length = 200)
	private String address;
	@Column(name = "user_ins",nullable = false,length = 200)
	private String userIns;
	@Column(name = "date_ins",nullable = false,length = 200)
	private String dateIns;
}
