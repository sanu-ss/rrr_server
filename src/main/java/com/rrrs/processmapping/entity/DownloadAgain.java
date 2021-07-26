package com.rrrs.processmapping.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadAgain {
	private int coreTabId;
	private String coreTabName;
	private int triggerId;
	private LocalDateTime dateOfDownload;
}
