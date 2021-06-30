package com.rrrs.fileupload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.fileupload.service.CommonUploadSevice;
import com.rrrs.tableconfig.controller.ConfigController;
import com.rrrs.util.ReadPropertyFile;
import com.rrrs.util.ResponseData;

@RestController
@RequestMapping(value = {"api/resource/fileUpload"})
public class CommonUploadController {
	private Logger logger=LoggerFactory.getLogger(CommonUploadController.class);
	private CommonUploadSevice commonUploadSevice; 
	public CommonUploadController(CommonUploadSevice commonUploadSevice) {
		this.commonUploadSevice=commonUploadSevice;
	}
	@GetMapping("checkfileuploaded/{filename}/{companyId}")
	public ResponseEntity<?> checkFileUploaded(@PathVariable("filename") String fileName,@PathVariable("companyId") int companyId){
		System.out.println(fileName+"-->"+companyId);
		boolean status=this.commonUploadSevice.checkFileUploaded(fileName,companyId);
		System.out.println("status-->"+status);
		if(status) {
			return new ResponseEntity<>(new ResponseData("fileAvailable"),HttpStatus.OK);
		}else {
			return new ResponseEntity<>(new ResponseData("fileNotAvailable"+status),HttpStatus.OK);
		}
	}
}
