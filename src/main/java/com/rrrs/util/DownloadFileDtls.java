package com.rrrs.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileDtls implements Serializable {

	private String filename;
	private String filepath;
	private String extension;
	private String orgfilename;	
	private byte[] file;

}
