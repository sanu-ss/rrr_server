package com.rrrs.fileAdmin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.fileupload.entities.UploadFileDtls;
@Service
public interface FileAdminService {

	List<UploadFileDtls> getAllUploadedFileList();

	boolean updateDonotProcess(UploadFileDtls uploadFileDtls);

}
