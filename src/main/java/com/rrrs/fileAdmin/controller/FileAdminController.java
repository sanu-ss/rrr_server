package com.rrrs.fileAdmin.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rrrs.fileAdmin.service.FileAdminService;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.DownloadFileDtls;
import com.rrrs.util.ReadPropertyFile;
import com.rrrs.util.ResponseData;
import com.rrrs.util.Util;

@RequestMapping("api/resource/fileAdmin")
@RestController
public class FileAdminController {
	private FileAdminService fileAdminService;
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("requiredFolderPath/foldderPath.properties");
	public FileAdminController(FileAdminService fileAdminService) {
		this.fileAdminService=fileAdminService;
	}
	
	@GetMapping(value={"/getAllUploadedFile"},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getAllUploadedFileList(){
		 List<UploadFileDtls> data=this.fileAdminService.getAllUploadedFileList();
		 return new ResponseEntity<>(data,HttpStatus.OK);
	}
	@GetMapping(value = {"/downloadFile"},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> downloadFile(@RequestParam Map<String,String> data){
		DownloadFileDtls file=new DownloadFileDtls();
		System.out.println("uploadFileDtls-->"+data);
		StringBuilder folderLocation =new StringBuilder( this.readPropertyFile.getProperty("upload.dir"));
		StringBuilder filelocation=new StringBuilder(folderLocation).append(File.separator)
		 .append(data.get("path")).append(File.separator).append(data.get("filename"));
		System.out.println();
		Path filePath = Paths.get(filelocation.toString());
		if(!Files.exists(filePath)) {
			return new ResponseEntity<>(new ResponseData("this file is deleted from upload location"),HttpStatus.BAD_REQUEST);
		}else {
			try {
				file.setFile(Files.readAllBytes(filePath));
				file.setOrgfilename(data.get("originalFileName"));
				file.setExtension(Util.fileextensionWithoutName(data.get("filename")));
				
			} catch (IOException e) {
				e.printStackTrace();
				return new ResponseEntity<>(new ResponseData("Error while reading data form upload location"),HttpStatus.BAD_REQUEST);
			}
		}
		System.out.println("folder-->"+folderLocation);
		System.out.println("-->"+Files.exists(filePath));
		return new ResponseEntity<>(file,HttpStatus.OK);
	}
	@PostMapping(value = {"/updateDonotProcess"})
	public ResponseEntity<?> updateDonotProcess(@RequestBody UploadFileDtls uploadFileDtls){
		
		boolean status=this.fileAdminService.updateDonotProcess(uploadFileDtls);
		if(status) {

			return new ResponseEntity<>(new ResponseData("Donot process is updated"),HttpStatus.OK);
		}else {

			return new ResponseEntity<>(new ResponseData("Donot process is not updated"),HttpStatus.BAD_REQUEST);
		}
	}
}
