package com.rrrs.processmapping.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTrigger {

	private int triggerId;
	private int mappingId;
	private int destTableId;
	private String status;
	private String destTableName;
	private int recordCount;
	private String errorMsg;
	
}
