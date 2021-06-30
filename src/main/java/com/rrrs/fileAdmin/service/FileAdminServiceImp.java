package com.rrrs.fileAdmin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rrrs.fileAdmin.dao.FileAdminDao;
import com.rrrs.fileupload.entities.UploadFileDtls;
@Service
public class FileAdminServiceImp implements FileAdminService {

	private FileAdminDao fileAdminDao; 
	public FileAdminServiceImp(FileAdminDao fileAdminDao) {
		this.fileAdminDao=fileAdminDao;
	}
	@Override
	public List<UploadFileDtls> getAllUploadedFileList() {
		return fileAdminDao.getAllUploadedFileList();
	}
	@Override
	public boolean updateDonotProcess(UploadFileDtls uploadFileDtls) {
		return fileAdminDao.updateDonotProcess(uploadFileDtls);
	}

}
