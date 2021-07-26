package com.rrrs.salesReporting.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreAndSourceTableDtls implements Serializable{

	private int tabId;
	private String tabName;
	private String orgTabName;
	private int totalColumn;
	private List<CoreAndSourceTabColdtls> tableColDtls=new ArrayList<>();
}
