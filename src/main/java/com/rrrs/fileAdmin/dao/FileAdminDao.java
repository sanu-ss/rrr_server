package com.rrrs.fileAdmin.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.rrrs.fileupload.entities.UploadFileDtls;
@Repository
public interface FileAdminDao {

	List<UploadFileDtls> getAllUploadedFileList();

	boolean updateDonotProcess(UploadFileDtls uploadFileDtls);

}
