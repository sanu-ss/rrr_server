package com.rrrs.mappingconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mapping {
	
	private int columnId;
	private String destinationColumn;
	private String mappingRule;
	private String dataType;
	private char nullable;
	private int dataSize;

}
