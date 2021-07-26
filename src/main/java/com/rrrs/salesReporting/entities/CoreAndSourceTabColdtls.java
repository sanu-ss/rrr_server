package com.rrrs.salesReporting.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreAndSourceTabColdtls implements Serializable{

	private int colId;
	private String colName;
	private String datatype;
	private int datasize;
}
