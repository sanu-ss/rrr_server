package com.rrrs.fileAdmin.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.fileupload.controller.ExcelUploadController;
import com.rrrs.fileupload.entities.UploadFileDtls;

@Repository
public class FileAdminDaoImp implements FileAdminDao{
	
	private Logger logger=LoggerFactory.getLogger(FileAdminDaoImp.class);
	private EntityManager entityManager;
	public FileAdminDaoImp(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	@Override
	public List<UploadFileDtls> getAllUploadedFileList() {
		Session session = this.entityManager.unwrap(Session.class);
		Query<UploadFileDtls> query = session.createQuery("from UploadFileDtls");
		 List<UploadFileDtls> data = query.getResultList();
		 for(UploadFileDtls file:data) {
			 if(file.getDonotProcess().equalsIgnoreCase("p")) {
				 file.setIsprocessable(true);
			 }else {
				 file.setIsprocessable(false);
			 }
		 }
		 return data;
	}
	@Override
	public boolean updateDonotProcess(UploadFileDtls uploadFileDtls) {
		Session session = this.entityManager.unwrap(Session.class);
		String query="update UploadFileDtls upload set upload.donotProcess=:donotProcess where upload.uploadId=:uploadId and upload.configId=:configId";
		Query createQuery = session.createQuery(query);
		Transaction tx = session.beginTransaction();
		int count=-1;
		try {
			System.out.println("uploadFileDtls-->"+uploadFileDtls);
			createQuery.setParameter("donotProcess", uploadFileDtls.getDonotProcess());
			createQuery.setParameter("uploadId", uploadFileDtls.getUploadId());
			createQuery.setParameter("configId", uploadFileDtls.getConfigId());
			count= createQuery.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			logger.info("execption while updating the do not process"+e);
			 count=-1;
		}		
		return (count>0)?true:false;
	}

}
