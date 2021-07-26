package com.rrrs.processmapping.entity;

import java.time.LocalDate;
import java.util.List;

import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.mappingconfig.entity.MappingDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessMappingDtls  {

	private MappingDetails mapDtls;
	private List<UploadFileDtls> filesDtls;
	private LocalDate startDate;
	private LocalDate endDate;
	
}
