package com.rrrs.fileupload.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.tableconfig.dao.JdbcConfigDaoImp;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.util.ReadPropertyFile;
@Repository
public class CommonUploadDaoImp implements CommonUploadDao{

	private EntityManager entityManager;
	private Logger logger=LoggerFactory.getLogger(JdbcConfigDaoImp.class);
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("fileupload/fileupload.properties");
	public CommonUploadDaoImp(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	@Override
	public boolean checkFileUpload(String fileName, int companyId) {
		Session session = this.entityManager.unwrap(Session.class);
		logger.info("checking uploaded file is already available or not");
		Query query = session.createQuery(readPropertyFile.getProperty("UPLOAD_FILE_CHECK"));
		query.setParameter("companyId", companyId);
		query.setParameter("filename", fileName);
		Long count = (Long)query.uniqueResult();
		return (count>0)?true:false;
	}
	@Override
	public boolean isConfigExist(String formatname) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CHECK_FORMAT_EXIST"));
		query.setParameter("formatname", formatname);
		Long count = (Long)query.uniqueResult();
		return (count>0)?true:false;
	}
	@Override
	public ConfigInfo getConfigDetails(String formatname) {
		Session session = this.entityManager.unwrap(Session.class);
		Query<ConfigInfo> query =session.createQuery(this.readPropertyFile.getProperty("GET_CONFIG_DTLS"));
		query.setParameter("formatname", formatname);
		return query.uniqueResult();
	}
	@Override
	public Map<String, Object> uploadStatus(Map<String, Object> errorMsg,ConfigInfo configInfo) {
		Map<String, Object> processDtls = new HashMap<>();
		Session session = this.entityManager.unwrap(Session.class);
		System.out.println(configInfo.getRrrCommonDtls().getCompanyId()+"errorMsg-->"+errorMsg);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CHECK_RRR_LOG_ERROR_MSG"));
		query.setParameter("companyId", configInfo.getRrrCommonDtls().getCompanyId());
		query.setParameter("fileUploadId", errorMsg.get("fileUploadId"));
		query.setParameter("logId", errorMsg.get("status"));
		//Object result = query.uniqueResult();
		Object[] data=(Object[])query.uniqueResult();
		//System.out.println("check-->"+data[0]+"-->"+data[1]+"-->"+data[2]);
		processDtls.put("STATUS",data[0]);
		processDtls.put("LINENO", data[1]);
		processDtls.put("ERRCOLNAME", data[2]);
		if(!processDtls.get("STATUS").equals("PROCESSED")){
			Query delete = session.createQuery(this.readPropertyFile.getProperty("DELETE_RRR_FILE_UPLD_ID"));
			delete.setParameter("uploadId", errorMsg.get("fileUploadId"));
			delete.setParameter("companyId", configInfo.getRrrCommonDtls().getCompanyId());
			delete.executeUpdate();
		}
		return processDtls;
	}

}
