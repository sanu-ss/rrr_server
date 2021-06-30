package com.rrrs.user.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Embeddable;

@Embeddable
public class RrrCommonDtls implements Serializable {

	private int companyId;
	private String userIns;
	private LocalDateTime dateIns;
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public String getUserIns() {
		return userIns;
	}
	public void setUserIns(String userIns) {
		this.userIns = userIns;
	}
	public LocalDateTime getDateIns() {
		return dateIns;
	}
	public void setDateIns(LocalDateTime dateIns) {
		this.dateIns = dateIns;
	}
	@Override
	public String toString() {
		return "RrrCommonDtls [companyId=" + companyId + ", userIns=" + userIns + ", dateIns=" + dateIns + "]";
	}
	
}
