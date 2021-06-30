package com.rrrs.mappingconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappingResponseData {
	private int mappingId;
	private String message;
}
