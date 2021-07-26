package com.rrrs.processmapping.dao;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.exception.module.CustomException;
import com.rrrs.fileupload.entities.UploadFileDtls;
import com.rrrs.mappingconfig.dao.MappingConfigDaoImp;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.mappingconfig.entity.RRRCoreTableColDtls;
import com.rrrs.mappingconfig.entity.RrrSourceTableDtls;
import com.rrrs.processmapping.entity.DownloadAgain;
import com.rrrs.processmapping.entity.ProcessMappingDtls;
import com.rrrs.processmapping.entity.ProcessTrigger;
import com.rrrs.processmapping.entity.RrrTempUploadIds;
import com.rrrs.processmapping.entity.TriggerDetails;
import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.DownloadFileDtls;
import com.rrrs.util.ReadPropertyFile;
@Repository
public class ProcessMappingDaoImp implements ProcessMappingDao{

	private static final String Long = null;
	private EntityManager entityManager;
	private CurrentUserDbDtls currentUserDbDtls;
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("mappingProcess/process.properties");
	private Logger logger=LoggerFactory.getLogger(MappingConfigDaoImp.class);
	public ProcessMappingDaoImp(EntityManager entityManager,CurrentUserDbDtls currentUserDbDtls) {
		this.currentUserDbDtls=currentUserDbDtls;
		this.entityManager=entityManager;
	}
	
	@Override
	public List<MappingDetails> getMappingList(Principal principal) {
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		Session session = this.entityManager.unwrap(Session.class);
		Query<MappingDetails> query = session.createQuery(this.readPropertyFile.getProperty("MAPPING_LIST_DTLS"));
		query.setParameter("companyId", companyId);
		List<MappingDetails> list = query.list();
		System.out.println("list of mappinng-->"+list);
		return list;
	}

	@Override
	public String getCoreTableName(Integer tableId) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CORE_TABLE_NAME"));
		query.setParameter("tableId", tableId);
		String data = (String)query.uniqueResult();
		System.out.println("data-->"+data);
		return data;
	}

	@Override
	public List<UploadFileDtls> getexecutionfiles(MappingDetails mappingDetails, Principal principal,
			LocalDate startDate, LocalDate endDate) {
			System.out.println("company id-->"+mappingDetails.getRrrCommonDtls().getCompanyId());
			System.out.println("mapping id"+mappingDetails.getMappingId());
			System.out.println("date-->"+startDate.atStartOfDay()+"-->"+endDate.atStartOfDay());
			Session session = this.entityManager.unwrap(Session.class);
			List<Integer> configIdList=new ArrayList<>();
			for(RrrSourceTableDtls table:mappingDetails.getSourceTable()) {
				configIdList.add(table.getConfigId());
			}
			Query query = session.createQuery(this.readPropertyFile.getProperty("RRR_UNPROCESSED_COUNT"));
			query.setParameter("companyId",mappingDetails.getRrrCommonDtls().getCompanyId());
			//query2.setParameter("mappingId",mappingDetails.getMappingId());
			query.setParameter("startDate",startDate.atStartOfDay());
			query.setParameter("endDate",endDate.atStartOfDay());
			query.setParameterList("configIds", configIdList);
			Long count=(Long)query.uniqueResult();
			System.out.println("configIdList-->"+configIdList);
			System.out.println("count-->"+count);
			List<UploadFileDtls> files=new ArrayList<>();
			if(count>0) {
				Query<UploadFileDtls> query2 = session.createQuery(this.readPropertyFile.getProperty("RRR_GET_UNPROCESSED_FILE"));
				query2.setParameter("companyId",mappingDetails.getRrrCommonDtls().getCompanyId());
				//query2.setParameter("mappingId",mappingDetails.getMappingId());
				query2.setParameter("startDate",startDate.atStartOfDay());
				query2.setParameter("endDate",endDate.atStartOfDay());
				query2.setParameterList("configIds", configIdList);
				files= query2.list();
				for(UploadFileDtls file:files) {
					 if(file.getDonotProcess().equalsIgnoreCase("p")) {
						 file.setIsprocessable(true);
					 }else {
						 file.setIsprocessable(false);
					 }
				 }
				return files;
			}
			
			
		return null;
	}

	@Override
	public List<ConfigInfo> getConfigFormatDtls(MappingDetails mappingDetails, Principal principal) {

		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		Session session = this.entityManager.unwrap(Session.class);
		List<Integer> formatList=new ArrayList<Integer>();
		for(RrrSourceTableDtls format:mappingDetails.getSourceTable()) {
			formatList.add(format.getConfigId());
		}
		System.out.println("format list-->"+formatList);
		Query<ConfigInfo> query = session.createQuery(this.readPropertyFile.getProperty("Config_format_dtls"));
		query.setParameterList("configList", formatList);
		//query.setParameter("configList", mappingId);
		//query.setParameter("companyId", companyId);
		List<ConfigInfo> list = query.list();
		
		System.out.println("list-->"+list);
		return list;
	}
	
	public void updateFileUploadIds(Session session,ProcessMappingDtls processMappingDtls) {
		//deleting old uploaded file ids
				Query deleteQuery = session.createQuery(this.readPropertyFile.getProperty("DELETE_UPLOAD_IDS"));
				deleteQuery.executeUpdate();
				int recCount=0;
				//saving file upload id in temp table
				for(UploadFileDtls file:processMappingDtls.getFilesDtls()){
					RrrTempUploadIds rrTempUploadIds=new RrrTempUploadIds();
					rrTempUploadIds.setUploadId(file.getUploadId());
					rrTempUploadIds.setUserIns(file.getRrrCommonDtls().getUserIns());
					session.persist(rrTempUploadIds);
					recCount++;
					System.out.println("record count-->"+rrTempUploadIds);
					if(recCount%50==0) {
						session.flush();
						session.clear();
					}
					
				}
				session.flush();
				session.clear();
				
	}

	@Override
	public TriggerDetails executemapping(ProcessMappingDtls processMappingDtls) throws CustomException {
		Session session = this.entityManager.unwrap(Session.class);
		
		//running procedure
		System.out.println("mapping id-->"+processMappingDtls.getMapDtls().getMappingId());
		System.out.println("start date-->"+processMappingDtls.getStartDate());
		System.out.println("end date--->"+processMappingDtls.getEndDate());
		System.out.println("company id-->"+processMappingDtls.getMapDtls().getRrrCommonDtls().getCompanyId());
		System.out.println("user ins--->"+processMappingDtls.getMapDtls().getRrrCommonDtls().getUserIns());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		//return formatter.format(date);
		
		StoredProcedureQuery procedure = session.createStoredProcedureQuery(this.readPropertyFile.getProperty("RRR_MAPPING_PROCESS_PRC"));
		procedure.registerStoredProcedureParameter("p_process_id", Integer.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_start_date", LocalDate.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_end_date", LocalDate.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_company_id", Integer.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_user_ins", String.class,ParameterMode.IN);
		procedure.registerStoredProcedureParameter("p_status", Integer.class,ParameterMode.OUT);
		procedure.setParameter("p_process_id", processMappingDtls.getMapDtls().getMappingId());
		procedure.setParameter("p_start_date", LocalDate.parse(formatter.format(processMappingDtls.getStartDate()),formatter));
		procedure.setParameter("p_end_date", LocalDate.parse(formatter.format(processMappingDtls.getEndDate()),formatter));
		procedure.setParameter("p_company_id", processMappingDtls.getMapDtls().getRrrCommonDtls().getCompanyId());
		procedure.setParameter("p_user_ins", processMappingDtls.getMapDtls().getRrrCommonDtls().getUserIns());
		Integer triggerId=0;
		Transaction tx = session.beginTransaction();
		try {
			updateFileUploadIds(session,processMappingDtls);
			tx.commit();
		}catch(Exception e) {
			tx.rollback();
		}
		procedure.execute();
		triggerId=(Integer)procedure.getOutputParameterValue("p_status");
		System.out.println("trigger id-->>"+triggerId);
		ProcessTrigger processTrigger =new ProcessTrigger();
		processTrigger.setTriggerId(triggerId);
		processTrigger.setMappingId(processMappingDtls.getMapDtls().getMappingId());
		
		
		Query query = session.createQuery(this.readPropertyFile.getProperty("RRR_PROCESS_TRIGGER_DTLS"));
		query.setParameter("triggerId", triggerId);
		query.setParameter("companyId", processMappingDtls.getMapDtls().getRrrCommonDtls().getCompanyId());
		Object result=query.uniqueResult();
		Object [] data=(Object[])result;
		processTrigger.setStatus((String)data[0]);
		processTrigger.setErrorMsg((String)data[1]);
		processTrigger.setDestTableId((Integer)data[2]);
		processTrigger.setDestTableName((String)data[3]);
		
		Query query2 = session.createSQLQuery(MessageFormat.format(this.readPropertyFile.getProperty("FETCH_RRR_DEST_COUNT"),processTrigger.getDestTableName()));
		query2.setParameter("companyId", processMappingDtls.getMapDtls().getRrrCommonDtls().getCompanyId());
		query2.setParameter("triggerId", triggerId);
		BigDecimal res=(BigDecimal)query2.uniqueResult();
		int count=res.intValue();
		processTrigger.setRecordCount(count);
		
		TriggerDetails triggerStatus = new TriggerDetails();
		if(processTrigger.getStatus().equals("FAILED")){
			throw new CustomException("MAPT01",  processTrigger.getErrorMsg().substring(11));
			
		}else {
			triggerStatus.setMessage("Process executed sucessfully "+processTrigger.getRecordCount()+" records updated");
			triggerStatus.setProcessTrigger(processTrigger);
		}
		
		return triggerStatus;
	}

	@Override
	public Map<String,List<String>> getResultFile(Integer destTabId) {
		Session session = this.entityManager.unwrap(Session.class);
		List<String> destColumns = new ArrayList<>();
		List<String> dataType = new ArrayList<>();
		Map<String,List<String>> destDtls = new HashMap<>();
		
		Query<RRRCoreTableColDtls> query = session.createQuery(this.readPropertyFile.getProperty("FETCH_CORE_COLS_DTLS"));
		query.setParameter("tableId", destTabId);
		List<RRRCoreTableColDtls> data = query.list();
		for(RRRCoreTableColDtls rrrCoreTableColDtls:data) {
			destColumns.add(rrrCoreTableColDtls.getColumnName());
			dataType.add(rrrCoreTableColDtls.getDataType());
		}
		destDtls.put("destColumns", destColumns);
		destDtls.put("destDataTypes", dataType);
		
		return destDtls;
	}

	@Override
	public String getDestTableName(Integer destTabId) {
		
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("FETCH_CORE_TAB_NAME"));
		query.setParameter("tableId", destTabId);
		String table = (String)query.uniqueResult();
		return table;
	}

	@Override
	public String getDestFile(Integer triggerId, String processDirectory, Map<String, List<String>> destColDtls,
			String coreTableName,RrrCommonDtls rrrCommonDtls) {
		Session session=this.entityManager.unwrap(Session.class);
		
		List<String> destColumns = destColDtls.get("destColumns");
		Workbook wb = new SXSSFWorkbook();
		String directoryBuilder =  new StringBuilder(processDirectory) .append( File.separator)
				.append(coreTableName+"_"+triggerId+"_"+rrrCommonDtls.getCompanyId()).append(ConfigUtils.EXCEL_FILE_EXTENSION).toString();			
		NativeQuery createSQLQuery = session.createSQLQuery(MessageFormat.format(this.readPropertyFile.getProperty("FETCH_RRR_CORE_FILE"),destColumns.stream().map(e -> e.toString()).collect(Collectors.joining(", ")),coreTableName));
		createSQLQuery.setParameter("companyId", rrrCommonDtls.getCompanyId());
		createSQLQuery.setParameter("triggerId", triggerId);
		List dataList = createSQLQuery.list();
		int sheetNum = 1;
		int rowNumber=0;
		int reCount=0;
		SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("sheet"+"-"+sheetNum);				
		
		for(Object obj:dataList) {
			
			Object [] result=(Object[])obj;
			if(rowNumber==0){
				Row row = sheet.createRow(rowNumber++);
				int cellnum=0;
				for(int i=0;i<destColumns.size();i++){
					Cell cell = row.createCell(cellnum++);
					if(destColumns.get(i) != null && destColumns.get(i) !="")
						cell.setCellValue(destColumns.get(i).toString());
						}
			}
			
			Row row = sheet.createRow(rowNumber++);
			int cellnum=0;
			for(int i=0;i<destColumns.size();i++){
				Cell cell = row.createCell(cellnum++);

				if(destColDtls.get("destDataTypes").get(i).equals("DATE")){

					if((String)result[i] != null && (String)result[i] !="")
						cell.setCellValue(ConfigUtils.convertDateString((LocalDate)result[i]));
				}else if(destColDtls.get("destDataTypes").get(i).equalsIgnoreCase("NUMBER")) {
					BigDecimal bigDecimal=(BigDecimal)result[i];
					if(bigDecimal != null && bigDecimal.toString() != "") {
						cell.setCellValue(bigDecimal.toString());
					}
				}else{
					if((String)result[i] != null && (String)result[i] !="")
						cell.setCellValue((String)result[i]);
				}
			}
			
			if(reCount==1000000){
				sheetNum++;
				sheet = (SXSSFSheet) wb.createSheet("sheet"+"-"+sheetNum);
				rowNumber=0;
			}
			reCount++;
			
		}
		boolean storedFile = ConfigUtils.getDownloadFile(directoryBuilder,wb);
		return (storedFile == false) ? String.valueOf(storedFile)  : directoryBuilder;
	}

	@Override
	public List<DownloadAgain> getDownloadList(Integer mappingId) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("FETCH_DESTTABID_PROCESSED_DATE_TRIGGERID"));
		query.setParameter("mappingId", mappingId);
		//query.setParameter("mappingId", mappingId);
		
		List list = query.list();
		List<DownloadAgain> downloadAgainList =new ArrayList<>();
		for(Object download:list) {
			Object [] data=(Object [])download;
			DownloadAgain downloadDtls=new DownloadAgain();
			downloadDtls.setTriggerId((Integer)data[1]);
			downloadDtls.setCoreTabName((String)data[0]);
			downloadDtls.setDateOfDownload((LocalDateTime)data[2]);
			downloadDtls.setCoreTabId((Integer)data[3]);
			downloadAgainList.add(downloadDtls);
		}
		return downloadAgainList;
	}
	

}








