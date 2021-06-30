package com.rrrs.fileupload.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.fileupload.service.CommonUploadSevice;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;
import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.ReadPropertyFile;

@Repository
public class ExcelUploadDaoImp implements ExcelUploadDao {
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("fileupload/fileupload.properties");
	private Logger logger=LoggerFactory.getLogger(ExcelUploadDaoImp.class);
	private EntityManager entityManager;
	private CurrentUserDbDtls currentUserDbDtlas;
	private CommonUploadSevice commonUploadService;
	public ExcelUploadDaoImp(EntityManager entityManager,CurrentUserDbDtls currentUserDbDtlas,CommonUploadSevice commonUploadService) {
		this.entityManager=entityManager;
		this.currentUserDbDtlas=currentUserDbDtlas;
		this.commonUploadService=commonUploadService;
	}
	@Override
	public Object uploadExcelData(ConfigInfo configInfo, UploadFileDtls uploadFileDtls,
			List<Map<Integer, String>> sheetData,String username) {
		this.logger.info("inside upload excel data");
		Object status=null;
		Map<String, Object> processDtls=new HashMap<>();
		int companyId=currentUserDbDtlas.getCompanyId(username);
		uploadFileDtls=setUploadFileDtls(uploadFileDtls,sheetData.size(),username,companyId);
		System.out.println("uploadFileDtls-->"+uploadFileDtls);
		Session session = this.entityManager.unwrap(Session.class);
		Transaction tx = session.beginTransaction();
		try {
			session.persist(uploadFileDtls);
			logger.info("Data saved in RRR_UPLOAD_FILE_DTLS");
			uploadDataInOrgConfigTab(sheetData,configInfo,uploadFileDtls,session);
			processDtls=executeProcedure(uploadFileDtls.getUploadId(),configInfo,session);
			processDtls.put("fileUploadId", uploadFileDtls.getUploadId());
			status=this.commonUploadService.uploadStatus(configInfo,processDtls,session);
			tx.commit();
			session.close();
			return status;
		}catch (Exception e) {
			logger.info("error while saving the uploaded file data in excel "+e);
			tx.rollback();
			session.close();
			return null;
		}
	
	}

	public UploadFileDtls setUploadFileDtls(UploadFileDtls uploadFileDtls,int recordCound,String username,int companyId) {
		uploadFileDtls.setErrorLog("");
		uploadFileDtls.setRecordCount(recordCound);
		RrrCommonDtls commondtls=new RrrCommonDtls();
		commondtls.setUserIns(username);
		commondtls.setCompanyId(companyId);
		commondtls.setDateIns(LocalDateTime.now());
		uploadFileDtls.setRrrCommonDtls(commondtls);
		return uploadFileDtls;
	}
	public void uploadDataInOrgConfigTab(List<Map<Integer, String>> sheetData,ConfigInfo configInfo,
			UploadFileDtls uploadFileDtls,Session session) {

		List<String> tableCols = configInfo.getMappings().stream().map(MapInfo::getTabcol).collect(Collectors.toList());
		tableCols.add(0, "SEQ_NO");
		tableCols.add(1, "FILE_UPLD_SEQ");
		tableCols.add(2, "USER_INS");
		tableCols.add(3, "DATE_INS");
		tableCols.add(4, "COMPANY_ID");
		String tableName=configInfo.getShadowTabName();
		System.out.println("config info-->>"+configInfo);
		int noOfColumns=configInfo.getColCount();

		StringBuilder columnQry = new StringBuilder().append('(').append(tableCols.stream().collect(Collectors.joining(", "))).append(')');
		StringBuilder valueQry = new StringBuilder().append('(').append(tableCols.stream().map(c -> "?").collect(Collectors.joining(", "))).append(')');		

		System.out.println(columnQry);
		System.out.println("value query -------------------");
		System.out.println(valueQry);
		String query="Insert into "+tableName+" "+columnQry+" values "+valueQry;
		System.out.println("final insert query-->"+query);
		int count=0;
		NativeQuery inserQuery = session.createSQLQuery(query);
		inserQuery.setParameter(2, uploadFileDtls.getUploadId());
		inserQuery.setParameter(3, uploadFileDtls.getRrrCommonDtls().getUserIns());
		inserQuery.setParameter(4, LocalDateTime.now());
		inserQuery.setParameter(5, uploadFileDtls.getRrrCommonDtls().getCompanyId());

		for(int len=0;len<sheetData.size();len++) {

			inserQuery.setParameter(1, len);

			//session.createQuery("")
			int setCount=6;
			Map<Integer,String> insertData=sheetData.get(len);
			for(int i=0;i<noOfColumns;i++) {
				if(insertData.containsKey(i))
					inserQuery.setParameter(setCount++, insertData.get(i));
				else
					inserQuery.setParameter(setCount++, "");
			}	
			inserQuery.executeUpdate();
			if (++count % 30 == 0){
				session.flush();
				session.clear();
			}

		}
		//last batch data will not be flushed in db so we should flush it once again
		session.flush();
		session.clear();
	}
	public Map<String, Object> executeProcedure(int uploadedId,ConfigInfo configInfo,Session session) {
		StoredProcedureQuery procedure = session.createStoredProcedureQuery(this.readPropertyFile.getProperty("RRR_DATA_TYPE_CONVERSION_PRC_CALL"));
		System.out.println("compsny-->"+configInfo.getRrrCommonDtls().getCompanyId());
		System.out.println(uploadedId);
		System.out.println(configInfo.getRrrCommonDtls().getUserIns());
		procedure.registerStoredProcedureParameter("p_company", Integer.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_upload_id", Integer.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("v_user_ins", String.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_status", Integer.class,ParameterMode.OUT);
		procedure.setParameter("p_company", configInfo.getRrrCommonDtls().getCompanyId());
		procedure.setParameter("p_upload_id", uploadedId);
		procedure.setParameter("v_user_ins", configInfo.getRrrCommonDtls().getUserIns());
		procedure.execute();
		Integer status =(Integer)procedure.getOutputParameterValue("p_status");
		Map<String, Object> processDtls=new HashMap<>();
		processDtls.put("status", status);
		processDtls.put("configid", configInfo.getConfigId());
		//System.out.println(count);
		return processDtls;
	}
}
